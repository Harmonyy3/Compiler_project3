package Semant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Iterator;

//import Absyn.*;
import Symbol.Symbol;
import Temp.Label;
import Temp.Temp;
import Absyn.AssignExp;
import Absyn.AssignExpA;
import Absyn.CharConstExp;
import Absyn.Dec;
import Absyn.DecList;
import Absyn.ExpArrList;
import Absyn.Init;
import Absyn.InitList;
import Absyn.ParamList;
import Absyn.SimpleVar;
import Absyn.Stm;
import Absyn.UnaryOpExp;
import Absyn.VarDec;
import Absyn.VarExp;
import Absyn.bflist;
import Frame.Access;
import Frame.AccessList;
import Frame.Frame;
import Mips.MipsFrame;
import Symbol.Symbol;
import Types.ARRAY;
import Types.CHAR;
import Types.INT;
import Types.NAME;
import Types.NIL;
import Types.FUNC;
import Types.STRUCT;
import Types.Type;
import Types.VOID;
import Types.UNION;

import Absyn.OpExp;

import Translate.*;
import Tree.CONST;
import Tree.SEQ;
import Frame.InFrame;
import Frame.InReg;
import Frame.Proc;





  // when translating a label declaration (something like LabelStm), add it to labelEnv
  // i.e., labelEnv.add(declaredLabel);

public class Semant {
  Env env;
  public Translate translate;
  public Frame frame;

    Stack<Set<Symbol>> varScopes = new Stack<>();
    Stack<Set<Symbol>> typeScopes = new Stack<>();

    public void beginScope() {
        env.venv.beginScope();
        env.tenv.beginScope();

        varScopes.push(new HashSet<>());
        typeScopes.push(new HashSet<>());
    }

    public void endScope() {
        env.venv.endScope();
        env.tenv.endScope();

        varScopes.pop();
        typeScopes.pop();
    }

    boolean varInCurrentScope(Symbol s) {
        return varScopes.peek().contains(s);
    }

    void addVarToCurrentScope(Symbol s) {
        varScopes.peek().add(s);
    }

    boolean typeInCurrentScope(Symbol s) {
        return typeScopes.peek().contains(s);
    }

    void addTypeToCurrentScope(Symbol s) {
        typeScopes.peek().add(s);
    }


  private boolean inLoop = false;
  Map<Symbol, Label> labelEnv = new HashMap<>();

  
  
  public Semant(ErrorMsg.ErrorMsg err) {
    this(new Env(err));
    this.frame = new Mips.MipsFrame();
    this.translate = new Translate(this.frame);
  }
  Semant(Env e) {
    this.frame = new Mips.MipsFrame();
    env = e;
  }

  public Exp transProgExp(Absyn.Exp exp) {
    return transExp(exp).exp;
  }

  public Exp transProgStm(Absyn.Stm stm) {
    return transStm(stm).exp;
  }

  public Exp transProgDecList(DecList list) {
    Tree.Stm seq = null;

    while (list != null) {
        Exp d = transProgDec(list.head);  // each dec returns a Translate.Exp
        Tree.Stm s = d.unNx();            // declarations return a statement

        if (seq == null)
            seq = s;
        else
            seq = new Tree.SEQ(seq, s);

        list = list.tail;
    }

    // If no declarations, return an empty statement
    if (seq == null)
        seq = new Tree.UEXP(new Tree.CONST(0));

    return new Nx(seq);
}


  public Exp transProgDec(Absyn.Dec dec) {
    return transDec(dec);
  }

  private boolean typeEq(Type a, Type b) {
    a = actual(a);
    b = actual(b);

    // base primitives
    if (a instanceof Types.CHAR && b instanceof Types.CHAR) return true;
    if (a instanceof Types.DOUBLE && b instanceof Types.DOUBLE) return true;
    if (a instanceof Types.FLOAT && b instanceof Types.FLOAT) return true;
    if (a instanceof Types.INT && b instanceof Types.INT) return true;
    if (a instanceof Types.LONG && b instanceof Types.LONG) return true;
    if (a instanceof Types.SHORT && b instanceof Types.SHORT) return true;
    if (a instanceof Types.VOID && b instanceof Types.VOID) return true;

    // array vs array
    if (a instanceof ARRAY && b instanceof ARRAY) {
        ARRAY aa = (ARRAY)a;
        ARRAY bb = (ARRAY)b;
        //System.out.println("BOTH ARRAY");

        // compare dimensions
        if(aa.dims == null) {
          if (aa.emptyArrayDimSize != bb.dims.size()) {
            //System.out.println("FALSE?:");
            return false;
          }
        } else if (bb.dims == null) {
          if (aa.dims.size() != bb.emptyArrayDimSize) {
            //System.out.println("FALSE?:");
            return false;
          }
        } else {
          if (aa.dims.size() != bb.dims.size()) {
            System.out.println("AA: " + aa.dims.size() + ", BB: " + bb.dims.size());
            return false;
          }
        }
        

        // compare element types
        if (!typeEq(aa.element, bb.element)) return false;

        return true;
    }

    // if(a instanceof ARRAY && b instanceof Types.STRING) {
    //   ARRAY aa = (ARRAY)a;
    //   System.out.println("inside TypeEq");
    //   if(aa.element instanceof CHAR && b instanceof Types.STRING) {
    //     return true;
    //   }
    // }

    // initializer vs array
    if (!(a instanceof ARRAY) && b instanceof ARRAY) {
        ARRAY bb = (ARRAY)b;

        // Compare a against element type
        return typeEq(a, bb.element);
    }

    

    // default reference comparison
    return a == b;
}

private void validateArrayDims(ARRAY array1, ARRAY array2, int pos, String msg) {
              List<Integer> dimsFieldType = new ArrayList<>();
              List<Integer> dimsActualTy = new ArrayList<>();
              dimsFieldType = array1.dims;
              dimsActualTy = array2.dims;
              int dimsFSize = dimsFieldType.size();
              int dimsASize = dimsActualTy.size();

              if(dimsFSize != dimsASize) {
                env.errorMsg.error(pos,
                  msg);
              } else {
                //System.out.println("sizes match for array FunCall");
                for(int i = 0, f = 0; i < dimsFSize && f < dimsASize; i++, f++) {
                  if(dimsFieldType.get(i) != dimsActualTy.get(f)) {
                    env.errorMsg.error(pos,
                    msg + " expected size: " +
                    dimsFieldType.get(i) + " got size: " + dimsActualTy.get(f)
                  );
                  }
                } 
              }
   }

   private void checkArrayInitializer(Absyn.InitList cur, ARRAY arrayType, int pos) {
    if (cur == null || arrayType == null) return;

    Type elemType = arrayType.element;

    Absyn.InitList it = cur;
    while (it != null) {

        Absyn.Init init = it.head;

        // If nested list, recurse
        if (init.list != null) {
            
            checkArrayInitializer(init.list, arrayType, pos);
        }

        // If scalar, type-check
        else {
            ExpTy value = transExp(init.exp);
            if (value == null || value.ty == null) {
                env.errorMsg.error(pos, "Invalid initializer expression");
            } else if (!typeEq(value.ty, elemType)) {
                env.errorMsg.error(pos,
                                "Cannot assign " + value.ty.getClass().getSimpleName() + 
                                " to " + arrayType.getClass().getSimpleName() + " of " + 
                                elemType.getClass().getSimpleName());
            }
        }

        it = it.tail;
    }
}


  private void validateUnionInitializer(UNION unionType, Absyn.Exp initiator, int pos) {
      Absyn.Init top = (Absyn.Init) initiator;
      Absyn.InitList inits = ((Absyn.Init)top.exp).list;
      if (inits == null) {
        //env.errorMsg.error(inits.pos, "Struct: " + structType.name.toString() + " initializer is empty");
        ExpTy right = transExp(initiator);
        if(!typeEq(unionType, right.ty)){
          if(right.ty instanceof UNION) {
            env.errorMsg.error(pos, "Type mismatch: cannot assign " + right.ty.getClass().getSimpleName() + 
            " " + ((UNION)right.ty).name.toString() +
            " to " + unionType.getClass().getSimpleName() + " " + unionType.name.toString());
          } else {
            env.errorMsg.error(pos, "Type mismatch: cannot assign " + right.ty.getClass().getSimpleName() + 
            " to " + unionType.getClass().getSimpleName() + " " + unionType.name.toString());
          }
        }
        return;
    }

      int actualCount = 0;
      Absyn.InitList cur = inits;

      while (cur != null) {
          actualCount++;

          // Translate each item in the initializer list
          Absyn.Init init = cur.head;
          if (init != null) {
              if (init.exp != null) {
                  // Simple expression initializer
                  ExpTy e = transExp(init.exp);
                  if (e == null || e.ty == null) {
                      env.errorMsg.error(pos, "Invalid initializer expression for union.");
                  }
                  Map.Entry<Symbol, Type> first = null;
                  for (Map.Entry<Symbol, Type> entry : unionType.fields.entrySet()) {
                      first = entry;  // will end up being the last one because first one is pushed to the back of the list
                  }
                  Type firstFieldType = first.getValue();
                  if(firstFieldType instanceof ARRAY && e.ty instanceof ARRAY) { 
                    if(((ARRAY)firstFieldType).dims != null) {
                      String errorMsg = "Array size mismatch for first field in union declaration";
                      validateArrayDims((ARRAY)firstFieldType, (ARRAY)e.ty, pos, errorMsg);
                    } else {
                      if(((ARRAY)firstFieldType).emptyArrayDimSize != ((ARRAY)e.ty).dims.size()) {
                        env.errorMsg.error(pos, "Array size mismatch for first field in union declaration");
                      }
                    }
                    
                  } else {
                    //System.out.println("they not arrays");
                    //System.out.println("Actual: " + e.ty.getClass().getSimpleName() + " Expected: " + firstFieldType.getClass().getSimpleName());
                    if(!typeEq(firstFieldType, e.ty)) {
                      env.errorMsg.error(pos, "Cannot assign " + e.ty.getClass().getSimpleName() + " to first field of type " + firstFieldType.getClass().getSimpleName() + " in union declaration");
                    }
                  }
              } else if (init.list != null) {
                  // Nested initializer list (e.g. { {1, 2} })
                  // This is invalid for unions — only one simple element allowed
                  env.errorMsg.error(pos, "Union initializer cannot contain nested lists.");
              }
          }

          cur = cur.tail;
      }

      if (actualCount > 1) {
          env.errorMsg.error(pos, "Cannot assign more than one value in a union initialization.");
      }
  }

private void validateStructInitializer(STRUCT structType, Absyn.Exp init, int pos) {
    Absyn.Init top = (Absyn.Init) init;
    Absyn.InitList inits;
    if(top.exp != null) {
      inits = ((Absyn.Init)top.exp).list;
    } else {
      inits = top.list;
    }
    
    if (inits == null) {
        //env.errorMsg.error(inits.pos, "Struct: " + structType.name.toString() + " initializer is empty");
        ExpTy right = transExp(init);
        if(!typeEq(structType, right.ty)){
            env.errorMsg.error(pos, "Type mismatch: cannot assign " + right.ty.getClass().getSimpleName() + " to " + structType.getClass().getSimpleName());
        }
        return;
    }

    // Count initializers
    int actualCount = 0;
    Absyn.InitList cur = inits;
    while (cur != null) {
        actualCount++;
        cur = cur.tail;
    }

    int expectedCount = structType.fields.size();
    if (actualCount > expectedCount) {
        env.errorMsg.error(pos, "Struct initializer size mismatch: expected "
                        + expectedCount + ", got " + actualCount);
        return;
    }

    // Walk fields and initializer expressions in order
    Iterator<Type> fieldTypes = structType.fields.values().iterator();
    cur = inits;
    Type actualType;
    actualType = null;
    while (cur != null) {
        Type expectedType = fieldTypes.next();
        

        if (cur.head.exp != null) {
            // simple expression
            actualType = transExp(cur.head.exp).ty;
        } else if (cur.head.list != null) {
            // nested initializer for array or struct
            if (expectedType instanceof STRUCT) {
                validateStructInitializer((STRUCT) expectedType, cur.head, pos);
                actualType = expectedType;
            } else if (expectedType instanceof ARRAY) {
                //System.out.println("Array detected in Struct");
                ARRAY arrType = (ARRAY) expectedType;

                if (cur.head.list == null) {
                    env.errorMsg.error(pos, "Array field initializer expected a list");
                } else {
                    // Reuse loopEachDecSize easily
                    if (arrType.dims != null && !loopEachDecSize(arrType.toExpArrList(), cur.head.list)) {
                        env.errorMsg.error(pos, "Array initializer size/type mismatch for struct field");
                    } else if(arrType.dims == null) {
                      initiateEmptyArray(cur.head.list, arrType.emptyArrayDimSize, pos);
                    }
                }

                actualType = transInitList(cur.head.list).ty;
            }
            //actualType = expectedType;
        } else {
            env.errorMsg.error(pos,"Invalid initializer: expected expression or nested list");
        }

        // System.out.println("expected: " + expectedType.getClass().getSimpleName() + " actual: " + actualType.getClass().getSimpleName() );
        // if(expectedType instanceof ARRAY){
        // }
        // if(actualType instanceof ARRAY) {
        //   System.out.println("actual type an array!");
        //   actualType = ((Types.ARRAY)actualType).element;
        //   System.out.println(actualType.getClass().getSimpleName());
        //   //actualType = ((Types.ARRAY)actualType).element;
        // }
        if(actualType != null) {
          if (!typeEq(actualType, expectedType)) {
            if(expectedType instanceof ARRAY) {
              env.errorMsg.error(pos, "Struct initializer type mismatch: cannot assign "
                              + actualType.getClass().getSimpleName() + " to " + expectedType.getClass().getSimpleName() + " of " + ((Types.ARRAY)expectedType).element.getClass().getSimpleName() );
            } else {
              env.errorMsg.error(pos, "Struct initializer type mismatch: cannot assign "
                              + actualType.getClass().getSimpleName() + " to " + expectedType.getClass().getSimpleName());
            }  
            //return;
          }
        }

        cur = cur.tail;
    }
}



  // private boolean correctArraySubSize(int declaredSize, Absyn.InitList initList) {
  //   System.out.println("declared SubSize: " + declaredSize);
  //   int count = 0;
  //   while (initList != null) {
  //     count++;
  //     initList = initList.tail;
  //   }

  //   System.out.println("SubCount: " + count);
  //   return count == declaredSize;
  // }

  // private boolean correctArraySize(int declaredSize, Absyn.InitList initList) {
  //   System.out.println("declared Size: " + declaredSize);
  //   boolean sizeMatch = false;
  //   int count = 0;      
  //   while (initList != null) {
  //       count++;
  //       //Absyn.InitList test = initList.head.list;
  //       // System.out.println("initList: " + initList);
  //       // System.out.println("initList Head: " + initList.head);
  //       // System.out.println("initList Head Expresion: " + initList.head.exp);
  //       // System.out.println("initList Tail: " + initList.tail);
  //       if(initList.head !=null && initList.tail == null) {
  //         System.out.println("head not null");
  //         if(!correctArraySubSize(declaredSize, initList)) {
  //           sizeMatch = false;
  //           return sizeMatch;
  //         }
  //       } else if(initList.tail != null) {
  //           System.out.println("tail not null");
  //           if(!correctArraySize(declaredSize, initList.tail)) {
  //             sizeMatch = false;
  //             return sizeMatch;
  //           }
        
  //       }
  //     System.out.println("list in list: " + initList);
  //     initList = initList.tail;
  //   }
  //     System.out.println("count size: " + count);

  //     if(count == declaredSize) {
  //       sizeMatch = true;
  //     } else {
  //       sizeMatch = false;
  //       //return sizeMatch;
  //     }
    
  //   return sizeMatch;
  // }

private List<Integer>  initiateEmptyArray(Absyn.InitList inits, int expectedDim, int pos) {
    List<Integer> dims = new ArrayList<>();
    collectDims(inits, dims, pos, expectedDim, 0);
    return dims;
}

private int sizeOfInitList(Absyn.InitList list) {
    int c = 0;
    for (Absyn.InitList it = list; it != null; it = it.tail)
        c++;
    return c;
}


private void collectDims(Absyn.InitList inits, List<Integer> dims, int pos, int expectedDims, int depth) {
    if (inits == null) return;

    boolean hasNested = false;
    boolean hasScalar = false;

    int count = 0;
    Absyn.InitList leftmostList = null;

    // First pass: determine counts and check for mix
    // First pass: determine counts and check for mix
    for (Absyn.InitList it = inits; it != null; it = it.tail) {
        count++;
        if (it.head != null) {
            if (it.head.list != null) {
                hasNested = true;

                // Save the leftmost list
                if (leftmostList == null)
                    leftmostList = it.head.list;
            } else {
                hasScalar = true;
            }
        }
    }

    // Error if mix of scalars and nested lists
    if (hasNested && hasScalar) {
        env.errorMsg.error(pos, "Invalid array initialization");
    }

    // NEW: enforce uniform sizes of nested lists
    if (hasNested) {
        int expectedSize = sizeOfInitList(leftmostList);
        for (Absyn.InitList it = inits; it != null; it = it.tail) {
            if (it.head != null && it.head.list != null) {
                int actualSize = sizeOfInitList(it.head.list);
                if (actualSize != expectedSize) {
                    env.errorMsg.error(pos, "Inconsistent array dimension at depth " + depth);
                }
            }
        }
    }


    // Only update dims along the leftmost path
    if (dims.size() < expectedDims) {
        dims.add(count);
    }

    // Depth check: if we go deeper than declared array dimensions → error
    if (depth + 1 > expectedDims) {
        env.errorMsg.error(pos, "Initializer has more dimensions than declared array");
        return;
    }

    // Recurse into all nested lists to check for errors
    for (Absyn.InitList it = inits; it != null; it = it.tail) {
        if (it.head != null && it.head.list != null) {
            collectDims(it.head.list, dims, pos, expectedDims, depth + 1);
        }
    }

    // If scalar level reached before expectedDims → error
    if (!hasNested && depth + 1 < expectedDims) {
        env.errorMsg.error(pos, "Initializer has fewer dimensions than declared array");
    }
}

private boolean validateDims(Absyn.ExpArrList arraySizes, Absyn.InitList inits) {
    //System.out.println("ArraySizes: " + ((Absyn.IntConstExp)arraySizes.head.constExpr).value);
    if (arraySizes == null) {
        //System.out.println("ArraySizes: NULL");
        
        return true;
    }

    
    int declaredSize = ((Absyn.IntConstExp)arraySizes.head.constExpr).value;
    int count = 0;
    Absyn.InitList it = inits;
    while (it != null) {
        count++;
        it = it.tail;
    }
    if (count != declaredSize) {
        env.errorMsg.error(arraySizes.pos, "Array dimension mismatch" + ": expected " + declaredSize + " got " + count);
        //System.out.println("Dimension mismatch: expected " + declaredSize + " got " + count);
        return false;
    }

    
    Absyn.ExpArrList nextSizes = arraySizes.tail;
    if (nextSizes != null) {
        it = inits;
        while (it != null) { 
            Absyn.Init child = it.head;
            if (child == null) {
                System.out.println("InitList element is null where child list expected");
                return false;
            }
            Absyn.InitList childList = child.list;
            if (childList == null) {
                
                System.out.println("Expected nested initializer list for next dimension but found none");
                return false;
            }
            if (!validateDims(nextSizes, childList)) return false;
            it = it.tail;
        }
    } else {
        it = inits;
        while (it != null) {
            Absyn.Init child = it.head;
            if (child == null) return false;
            if (child.list != null) {
                System.out.println("Extra nested list present but no declared dimension left");
                return false;
            }
            it = it.tail;
        }
    }

    return true;
}

// must reverse the dimensions list because the head is the inermost dimension
private Absyn.ExpArrList reverseDims(Absyn.ExpArrList dims) {
    Absyn.ExpArrList result = null;
    while (dims != null) {
        result = new Absyn.ExpArrList(dims.head, result);
        dims = dims.tail;
    }
    return result;
}


private boolean loopEachDecSize(Absyn.ExpArrList arraySizeList, Absyn.InitList initList) {
    Absyn.ExpArrList correctOrder = reverseDims(arraySizeList);
    return validateDims(correctOrder, initList);
}

  private Type actual(Type t) {
      while (t instanceof NAME && ((NAME)t).binding != null)
          t = ((NAME)t).binding;
      return t;
  }

  ExpTy transVar(Absyn.Var v) {
      if (v instanceof Absyn.SimpleVar) {
          //System.out.println("TransVar: is SimpleVar");
          return transSimpleVar((Absyn.SimpleVar)v);
      }
      
      env.errorMsg.error(v.pos, "Unknown Var kind");
      return null;
  }

  ExpTy transSimpleVar(Absyn.SimpleVar v) {

    Entry entry = (Entry) env.venv.get(v.name);
    //System.out.println("entry for var " + v.name + " is : " + (entry !=null ? entry.getClass().getSimpleName() : "null"));
    //System.out.println(v.name);
    //System.out.println("Simple var and instance of VarEntry");
    if (!(entry instanceof VarEntry)) {
        //System.out.println("Simple var is not instancce of VarEntry");
        env.errorMsg.error(v.pos, "Undefined variable: " + v.name);
        return new ExpTy(new Ex(null), Type.INT);   // placeholder
    }

    VarEntry var = (VarEntry) entry;
    // System.out.println("VarEntry found for variable: " + v.name + " where access is " + var.access != null ? var.access.getClass().getSimpleName() : "null");
    // System.out.println("VarEntry Type is: " + var.ty.getClass().getSimpleName());
    System.out.println(var.ty.getClass().getSimpleName());

    Exp translated = translate.SimpleVar(var.access);   // placeholder for now

    return new ExpTy(translated, var.ty);
}

  /* formely "static final Types.CHAR   CHAR    = new Types.CHAR();"" and so on
   * now, CHAR is a CHAR instance typed as "Type" (works with ExpTy constructor) instead of typed as CHAR (too specific)
   * MAKE SURE IN TYPES.CHAR: public class CHAR extends Type {...} */

  static final Type   CHAR    = new Types.CHAR();
  static final Type   DOUBLE  = new Types.DOUBLE();
  static final Type   FLOAT   = new Types.FLOAT();
  static final Type   INT     = new Types.INT();
  static final Type   LONG    = new Types.LONG();
  static final Type   SHORT   = new Types.SHORT();
  //static final Type   ENUM    = new Types.ENUM();
  static final Type   VOID    = new Types.VOID();
  
//region: declarations
  // DECLARATION (Dec) DISPATCHER
  Exp transDec(Absyn.Dec d) {
    //System.out.println("Translating Dec of Type: " + d.getClass().getName());
    if (d == null) {
      return null;
    } else if (d instanceof Absyn.VarDec) {
      //System.out.println("Translating VarDec");
      return transVarDec((Absyn.VarDec)d);
      // ^ var declaration
    } else if (d instanceof Absyn.FunctionDec) {
      return transFunDec((Absyn.FunctionDec)d);
      // ^ func declaration
    } else if (d instanceof Absyn.TypeDec) {
      return transTypeDefDec((Absyn.TypeDec)d);
      // ^ typdef declaration
    } else if (d instanceof Absyn.StructOrUnionDec) {
      return transStructOrUnionDec((Absyn.StructOrUnionDec)d);
      // ^ struct or union declaration
    } else if (d instanceof Absyn.Param) {
      System.out.println("NULL");
      return null;
    } else if (d instanceof Absyn.EnumDec) {
      return transEnumDec((Absyn.EnumDec)d);
      // ^ enum declaration
    } else
    //instance of init?
      //System.out.println("init");
        
    {
      //System.out.println("current Dec Type: " + d.getClass().getName());
      env.errorMsg.error(d.pos,"transDec: unknown Dec type");
      return null; //placeholder not sure
    }
  }


  //  TRANSLATE DECLIST
  Exp transDecList(Absyn.DecList d) {
    //if d is null...
    
    //else...
    //Exp head = transDec(d.head);
    // ^ head is a dec
    //Exp tail = transDecList(d.tail);
    // ^ tail is declist (?)
    return null;
  }

  //  TRANSLATE VARDEC
  Exp transVarDec(Absyn.VarDec v) {
      boolean escapes = false;
      if (v == null) return new Nx(null);   // *** CHANGED (never return null)

      if (env.venv.get(v.name) != null && varInCurrentScope(v.name)) {
          env.errorMsg.error(v.pos, "Variable already declared: " + v.name);
          return new Nx(null);
      } else {
        addVarToCurrentScope(v.name);
        //Access access = frame.allocLocal(escapes);
      }
      //transExp(v.init);

      boolean isArray = false;
      boolean isPointer = false;
      boolean isStruct = false;
      boolean isUnion = false;
      boolean isEmptyArray = false;

      if (v.typ != null && v.typ.typeArgs != null) {
          if (v.typ.typeArgs.brackets != null && v.typ.typeArgs.pointers != null) {
              isArray = true;
              isPointer = true;
              if(v.typ.typeArgs.brackets.empty != null) {
                isEmptyArray = true;
              }
          } else if (v.typ.typeArgs.brackets != null) {
              //System.out.println("dis be a array" + v.typ.typeArgs.brackets);
              //transDec(v.typ.typeArgs.brackets);
              if(v.typ.typeArgs.brackets.empty != null) {
                isEmptyArray = true;
              }
              isPointer = false;
              isArray = true;
          } else if (v.typ.typeArgs.pointers != null) {
              isPointer = true;
              isArray = false;
          }
      }

      Type ty = transType(v.typ);
      //System.out.println("the current Type is: " + ty.getClass().getSimpleName());
      if (ty == null) ty = Type.INT;   // default recovery

      if (ty instanceof Types.STRUCT) {
          isStruct = true;
      } else if (ty instanceof Types.UNION) {
          isUnion = true;
      }

      /* ========================
        ARRAY type handling
        ======================== */
      if (isArray) {
          //List<Integer> dimsList = new ArrayList<>();
          //ty = (Types.ARRAY)(ty);
          ExpArrList arrayDecSize = v.typ.typeArgs.brackets.expArrList;
          Absyn.Init top = (Absyn.Init) v.init;
          if (top == null) {
              env.errorMsg.error(v.pos, "Array " + v.name + " must be initialized with a list");
              // do NOT return
          } else {
            if(!isEmptyArray) {
              Absyn.Exp topString = null;
              Absyn.InitList initList = ((Absyn.Init) top.exp).list;
              if(initList == null) {
                  if(top != null) {
                    Absyn.Init topExp = ((Absyn.Init)top.exp);
                    if(topExp != null) {
                      topString = topExp.exp;
                      System.out.println(topString.getClass().getName());
                    }
                  }
              }
              if (initList == null && !(topString instanceof Absyn.StringExp)) {
                  env.errorMsg.error(v.pos, "Array must have initializer list");
              } else {
                if(!(topString instanceof Absyn.StringExp)) {
                  if (v.init != null && !loopEachDecSize(arrayDecSize, initList)) {
                        env.errorMsg.error(v.pos, "Array size mismatch");
                      // DO NOT return
                  }
                }
              }
            } else {
              //System.out.println("Empty array inside vardec");
              //System.out.println("v.init: " + (((Absyn.Init)v.init).exp).exp.getClass().getSimpleName());
              Absyn.Exp topString = null;
              Absyn.InitList initList = ((Absyn.Init) top.exp).list;
              if(initList == null) {
                  if(top != null) {
                    Absyn.Init topExp = ((Absyn.Init)top.exp);
                    if(topExp != null) {
                      topString = topExp.exp;
                      //System.out.println(topString.getClass().getName());
                    }
                  }
              }
              
              if (initList == null && !(topString instanceof Absyn.StringExp)) {
                  env.errorMsg.error(v.pos, "Array must have initializer list");
              } else {
                if(!(topString instanceof Absyn.StringExp)) {
                  List<Integer> dims = new ArrayList<>(); 
                  int count = 0;
                  Absyn.EmptyArrayTypeList dimsListSize = ((Absyn.EmptyArrayTypeList)v.typ.typeArgs.brackets.empty);
                  while( dimsListSize != null) {
                    count++;
                    dimsListSize = dimsListSize.tail;
                  }
                  dims = initiateEmptyArray(initList, count, v.pos);
                  Collections.reverse(dims);
                  // for (int d : dims) {
                  //     System.out.println(d);
                  // }

                  
                  ((Types.ARRAY)ty).dims = dims;
                } else {
                  List<Integer> dims = new ArrayList<>(); 
                  dims.add(((Absyn.StringExp)topString).value.length());
                  ((Types.ARRAY)ty).dims = dims;
                }
              }
            }
          }

          // Collect dimensions
          // ExpArrList cur = arrayDecSize;
          // while (cur != null) {
          //     int curDecSize = ((Absyn.IntConstExp) cur.head.constExpr).value;
          //     dimsList.add(curDecSize);
          //     cur = cur.tail;
          // }

          //ARRAY arrayType = new Types.ARRAY(ty, dimsList);
          //ty = arrayType;
      }

      /*========================
        INSERT VARIABLE IN ENV
        ALWAYS BEFORE INIT CHECK
        ========================*/
        Access access = frame.allocLocal(escapes);
        VarEntry varEntry = new VarEntry(access, ty, isPointer);
        //System.out.println("VarDec Acces = " + access.getClass().getSimpleName());
        env.venv.put(v.name, varEntry);   // *** MOVED UP / ALWAYS EXECUTES

      


      /* ========================
        STRUCT initializer / UNION
        ======================== */
      if (isStruct) {
          Absyn.Init top = (Absyn.Init) v.init;
          if (top == null) {
              //env.errorMsg.error(v.pos, "Struct " + v.name + " must be initialized with a list");
              return new Nx(null); // *** still no null
            }
          // Absyn.VarExp simpleDec;
          // if(v.init instanceof Absyn.VarExp) {
          //   ExpTy rightTy;
          //   Absyn.SimpleVar rightVar;
          //   simpleDec = (Absyn.VarExp) v.init;
          //   rightVar = (Absyn.SimpleVar)simpleDec.var;
          //   rightTy = transExp(rightVar);
            
          //   if(!typeEq())
          // }
          validateStructInitializer((STRUCT) actual(ty), v.init, v.pos);


          return new Nx(null);
      } else if (isUnion) {
          Absyn.Init init = (Absyn.Init) v.init;

          if (init == null) {
              // No initializer — fine
              return new Nx(null);
          }
          //Absyn.InitList initList = ((Absyn.Init)init.exp).list;
          validateUnionInitializer((UNION) actual(ty), v.init, v.pos);

          // Case 1: { value } — single element initializer
          // if (init.isSingleValue()) {
          //     // assign to first field in union — allowed
          //     return new Nx(null);
          // }

          // // Case 2: { field = value } — named field assignment
          // if (init.isFieldAssignment() && init.count() == 1) {
          //     // allowed — initializing one specific union field
          //     return new Nx(null);
          // }

          // Case 3: anything else — error
          // env.errorMsg.error(v.pos,
          //     "Union '" + v.name + "' can only be initialized with one field or one value");
          return new Nx(null);
      }



      /* ========================
        SCALAR / POINTER INIT
        ======================== */
      ExpTy initExp = null;
      //ExpList initListExp = null;

      if (v.init != null) {
          initExp = transExp(v.init);
          Absyn.Init top = (Absyn.Init) v.init;
          Absyn.InitList inits = ((Absyn.Init)top.exp).list;
          if(inits == null) {
            //System.out.println("NULL");
            if (!isPointer) {
              if(initExp != null) {
                //System.out.println("The Above is NULL ^^^");
                if (!typeEq(initExp.ty, ty)) {
                    if (ty instanceof ARRAY) {
                        //System.out.println("here: " + ((ARRAY)(initExp.ty)).dims.get(0) + " " + ((ARRAY)(ty)).emptyArrayDimSize);
                        env.errorMsg.error(v.pos,
                            "Cannot assign " + initExp.ty.getClass().getSimpleName() + " to " + ty.getClass().getSimpleName() + " of " + ((Types.ARRAY)ty).element.getClass().getSimpleName());
                    } else {
                        env.errorMsg.error(v.pos,
                            "Cannot assign " + initExp.ty.getClass().getSimpleName() + " to " + ty.getClass().getSimpleName());
                    }
                    // *** DO NOT return
                }
              }
            } else {
                if (!(initExp.ty instanceof INT)) {
                    env.errorMsg.error(v.pos, "Cannot assign non-int to pointer");
                    // *** DO NOT return
                }
            }
          } else {
            //System.out.println("NOT NULL");
            // InitList list = ((Absyn.Init)v.init).list;
            // InitList current = list.tail;
            //System.out.println("current is: " + current);
            Absyn.InitList cur = inits;
            while(cur != null) {
              //System.out.println("Cur Head: " + ((Absyn.IntConstExp)cur.head.exp).value);
              initExp = transExp(cur.head);
              //initListExp = new ExpList(initExp.exp, initListExp);
              if (!isPointer) {
                if(ty instanceof ARRAY && cur.head.list != null) {
                  checkArrayInitializer(cur.head.list, (ARRAY) ty, v.pos);
                } else {
                    if (!typeEq(initExp.ty, ty)) {
                        if (ty instanceof ARRAY) {
                            //System.out.println("VAR DEC");
                            env.errorMsg.error(v.pos,
                                "Cannot assign " + initExp.ty.getClass().getSimpleName() + " to " + ty.getClass().getSimpleName() + " of " + ((Types.ARRAY)ty).element.getClass().getSimpleName());
                        } else {
                            env.errorMsg.error(v.pos,
                                "Cannot assign " + initExp.ty.getClass().getSimpleName() + " to " + ty.getClass().getSimpleName());
                        }
                        // *** DO NOT return
                    }
                }
              } else {
                  if (!(initExp.ty instanceof INT)) {
                      env.errorMsg.error(v.pos, "Cannot assign non-int to pointer");
                      // *** DO NOT return
                  }
              }
              cur = cur.tail;
            }
          }
      }
      Absyn.Exp flat = flattenInit(v.init);
      ExpTy expty = transExp(flat);
      Exp treeexp = null;
      if(initExp == null) {
        treeexp = translate.VarDec(access, null);
      } else { 
           treeexp = translate.VarDec(access, expty.exp);

      }
       
      return treeexp;
  }


  // TRANSLATE FUNCTIONDEC
Exp transFunDec(Absyn.FunctionDec f) {
    if (f == null) return null;

    // 1) Determine return type
    Type returnType = (f.result != null) ? transType(f.result) : Type.VOID;

    /* ---- 2) BUILD PARAMETER LIST (FUNC) IN REVERSE ORDER ---- */
    FUNC params = null;
    ParamList pl = (f.params != null) ? f.params.paramList : null;

    for (; pl != null; pl = pl.head) {
        boolean isPointer = false;
        boolean isArray = false;
      if(pl.type.typeArgs != null) {
        if(pl.type.typeArgs.pointers != null && pl.type.typeArgs.brackets != null) {
          isPointer = true;
          isArray = true;
        } else if (pl.type.typeArgs.brackets != null) {
          isArray = true;
        } else if (pl.type.typeArgs.pointers != null) {
          isPointer = true;
        } else {
          isPointer = false;
          isArray = false;
        }
      }
        Symbol paramName = pl.name;
        Type paramType = transType(pl.type);

        // Prepend to FUNC to store in reverse order
        if(isPointer && isArray) {
          paramType = Type.INT;
          params = new FUNC(paramName, paramType, params, isPointer);
        } else if (isArray) {
          params = new FUNC(paramName, paramType, params, isPointer);
        } else if(isPointer) {
          paramType = Type.INT;
          params = new FUNC(paramName, paramType, params, isPointer);
        } else {
          params = new FUNC(paramName, paramType, params, isPointer);
        }
        
    }

    Util.BoolList escapes = null;
      for (FUNC r = params; r != null; r = r.tail) {
          // r.escape should be a boolean you stored earlier
          escapes = new Util.BoolList(true, escapes);
      }

    
    this.frame = new MipsFrame().newFrame(f.name, escapes);


   // System.out.println("Function: " + f.name);
    //AccessList al = frame.formals;
    FUNC rr = params;
    // while (al != null && rr != null) {
    //     System.out.println("  formal " + rr.fieldName + " -> " + al.head);
    //     al = al.tail;
    //     rr = rr.tail;
    // }


    /* ---- 3) ENTER FUNCTION NAME FIRST (so recursion works) ---- */
    env.venv.put(f.name, new FunEntry(params, returnType, frame));
    
    /* ---- 4) BEGIN FUNCTION SCOPE ---- */
    //env.venv.beginScope();
    beginScope();
    

    /* ---- 5) ENTER PARAMETERS AS LOCAL VARIABLES ---- */
    AccessList a = frame.formals;
    for (FUNC r = params; r != null; r = r.tail, a = a.tail) {
        boolean isPointer = r.isPointer;
        // System.out.println("ADDING PARAM → " 
        //     + r.fieldName + " : " + r.fieldType.getClass().getSimpleName());
        env.venv.put(r.fieldName, new VarEntry(a.head, r.fieldType, isPointer));
    }

    /* ---- 6) TYPE-CHECK BODY ---- */
    ExpTy bodyExp = null;
    if (f.body != null)
        bodyExp = transStm(f.body);

    if (bodyExp != null && !typeEq(bodyExp.ty, returnType)) {
        env.errorMsg.error(f.pos,
              "Function " + f.name
            + " returns wrong type: expected "
            + returnType.getClass().getSimpleName()
            + ", got "
            + bodyExp.ty.getClass().getSimpleName());
    }

    /* ---- 7) END SCOPE ---- */
    endScope();
    //env.venv.endScope();

    Exp irBody = (bodyExp != null && bodyExp.exp != null) ? bodyExp.exp : new Nx(new Tree.LABEL(frame.name));

    // 11) Ensure non-null IR for body
    Tree.Stm bodyStm = irBody.unNx();

    // 13) Create and store a ProcFrag (THIS is when you add to frags)
    translate.procEntryExit(frame, bodyStm);

      // --- Convert IR to instruction list ---
    

    // 12) Build function declaration IR
    return translate.FunctionDec(frame.name, irBody);

}




  //  TRANSLATE TYPEDEFDEC
  Exp transTypeDefDec(Absyn.TypeDec t) {
    if(env.tenv.get(t.name) != null && typeInCurrentScope(t.name)) {
      env.errorMsg.error(t.pos, "Type " +  t.name.toString() + " already exists");
    } else {
      addTypeToCurrentScope(t.name);
    }

    //the original type
    Type actual = transType(t.ty);

    //create new alias for type
    Types.NAME alias = new Types.NAME(t.name, actual);

    //add alias to type enviornment
    env.tenv.put(t.name, alias);
    //env.venv.put(t.name, actual);
    return new Ex(new CONST(0));

  }

  //  TRANSLATE STRUCTORUNIONDEC
  Exp transStructOrUnionDec(Absyn.StructOrUnionDec s) {
      if(env.tenv.get(s.name) != null && typeInCurrentScope(s.name)) {
        env.errorMsg.error(s.pos, "Struct or Union " +  s.name.toString() + " already exists");
      } else {
        addTypeToCurrentScope(s.name);
      }


      Map<Symbol, Type> fieldMap = new LinkedHashMap<>();
      Absyn.StructDecList listSU = s.structOrUnion;

      while (listSU != null) {
          //System.out.println("ListSU: " + listSU.name.toString());
          Type ty = transType(listSU.type);
          fieldMap.put(listSU.name, ty);
          listSU = listSU.prev;
      }

      if(s.strun.toString().equals("struct")) {
        Types.STRUCT structType = new Types.STRUCT(s.name, fieldMap);
        env.tenv.put(s.name, structType);
      } else if(s.strun.toString().equals("union")) {
        //System.out.println("We is Union");
        Types.UNION unionType = new Types.UNION(s.name, fieldMap);
        env.tenv.put(s.name, unionType);
      }
      

      

      return new Ex(new CONST(0));
  }

  //  TRANSLATE ENUMLIST
  Exp transEnumList(Absyn.EnumList e, int currentValue) {
    if (e == null) {
      return null;
    }

    // e.head is a dec, but is actually an enum, so cast e.head to Absyn.Enum to access its VALUE field
    Absyn.Enum currentEnum = (Absyn.Enum)e.head;

    // if e.head's value field is NOT empty
    if (currentEnum.value != null) {
      
      // check if what is stored in the value field is an intexp
      if (currentEnum.value instanceof Absyn.IntConstExp) {
        // if it is, its declared type is still exp, so cast Absyn.IntExp onto currentEnum.value
        // get the int inside the intexp (the second .value) and assign it to "current value"
        currentValue = ((Absyn.IntConstExp)currentEnum.value).value;
      
      } else {
        env.errorMsg.error(currentEnum.pos, "enum value must be integer constant");
      }
    
    } else {
      // assign the current enum an IntExp with its position and numeric value
      currentEnum.value = new Absyn.IntConstExp(currentEnum.pos, currentValue);
    }

    transEnumList(e.tail, currentValue + 1);
    // transenumlist the tail of the list currently being translated
    // increment current value for the next enumerator in the list
    
    return null;
  }

  //  TRANSLATE ENUMDEC
  Exp transEnumDec(Absyn.EnumDec e) {
    if (e == null) {
      return null;
    }

    // add the enum type to the type environment
    Types.ENUM enumType = new Types.ENUM(e.name);
    env.tenv.put(Symbol.symbol(e.name), enumType);

    if (e.enumerators != null) {
      // translate each enumerator, assign integer values
      transEnumList(e.enumerators, 0);
    }

    return new Ex(new CONST(0));
  }



// PUBLIC: called from VarDec
public Absyn.Exp flattenInit(Absyn.Exp init) {
    if (!(init instanceof Absyn.Init))
        return init;      // not an initializer → leave as-is

    Absyn.Init i = (Absyn.Init) init;
    Absyn.Init actualInit = ((Absyn.Init)i.exp);
    // Case 1: scalar initializer
    if (actualInit.exp != null) {
        //System.out.println("we here");
        if(actualInit.exp instanceof Absyn.StringExp) {
          System.out.println("StringExp detected cant flatten");
          return makeStringInitList((Absyn.StringExp)actualInit.exp);
        } else {
          return init;  // already flat
        }
        
    }

    // Case 2: Init(InitList) → flatten the list
    if (actualInit.list != null) {
        Absyn.InitList flat = flattenList(actualInit.list, null);
        return new Absyn.Init(flat);
    }

    // Case 3: Init(dec) → illegal in C89
    if (actualInit.dec != null) {
        throw new Error("designated initializers not allowed in ANSI C89");
    }

    return i;
}


private Absyn.Exp makeStringInitList(Absyn.StringExp strExp) {
    Absyn.InitList initList = null;
    for (int i = 0; i <= strExp.value.length() - 1; i++) {
        char c = strExp.value.charAt(i);
        Absyn.CharConstExp charExp = new Absyn.CharConstExp(0, c);
        Absyn.Init init = new Absyn.Init(charExp);
        initList = new Absyn.InitList(initList, init);
    }
    Absyn.CharConstExp charExp = new Absyn.CharConstExp(0, '\0');
    Absyn.Init init = new Absyn.Init(charExp);
    initList = new Absyn.InitList(initList, init);
    return new Absyn.Init(initList);
}


/* ------------------------------------------------------
   flattenList:
   Recursively flattens InitList into a *flat* InitList,
   containing only Init(exp) nodes.
   Order is preserved.
------------------------------------------------------- */
private Absyn.InitList flattenList(Absyn.InitList il, Absyn.InitList acc) {
    if (il == null)
        return acc;

    acc = flattenInitNode(il.head, acc);   // flatten head
    return flattenList(il.tail, acc);      // flatten tail
}



/* ------------------------------------------------------
   flattenInitNode:
   Flattens a single Init node (scalar or nested list)
------------------------------------------------------- */
private Absyn.InitList flattenInitNode(Absyn.Init init, Absyn.InitList acc) {

    // Case A: nested { ... }
    if (init.list != null) {
        return flattenList(init.list, acc);
    }

    // Case B: scalar Init(exp)
    if (init.exp != null) {
        return append(acc, new Absyn.Init(init.exp));
    }

    // Case C: designated initializer → illegal
    if (init.dec != null) {
        throw new Error("designated initializers not allowed in C89");
    }

    return acc;
}



/* ------------------------------------------------------
   append(list, element):
   Appends element at end of InitList
------------------------------------------------------- */
private Absyn.InitList append(Absyn.InitList list, Absyn.Init element) {
    if (list == null) return new Absyn.InitList(element);

    Absyn.InitList head = list;
    while (list.tail != null) {
        list = list.tail;
    }
    list.tail = new Absyn.InitList(element);
    return head;
}


  //  TRANSLATE INITLIST
   ExpTy transInitList(Absyn.InitList list) {
      if (list == null) return null;

      // Infer element type from the first element
      ExpTy first = transInit(list.head);
      Type elemType = first.ty;

      
      // Now validate all others
      InitList current = list;
      ExpList initListExp = null;
      while (current != null) {
          ExpTy item = transInit(current.head);
          initListExp = new ExpList(item.exp, initListExp);
          if (!typeEq(item.ty, elemType)) {
              // env.errorMsg.error(list.pos, 
              //     "Element type mismatch: expected "
              //     + elemType.getClass().getSimpleName() + ", found " + item.ty.getClass().getSimpleName()
              // );
              break;
          }
          
          //System.out.println("current Element is: " + ((Absyn.IntConstExp)current.head.exp).value);

          current = current.tail;
      }
      
      return new ExpTy(translate.InitExp(initListExp, sizeof(first.ty), true), elemType);
  }

  //  TRANSLATE INIT
  ExpTy transInit(Absyn.Init i) {
      if (i == null) return null;

      // if (i.exp != null) {
      //   //System.out.println("not a list");
      //     return transExp(i.exp);
      // }

      if (i.list != null) {
          //System.out.println("has a list");
          return transInitList(i.list);
      } else if (i.exp != null) {
        //System.out.println("not a list");
          return transExp(i.exp);
      }

      env.errorMsg.error(i.pos, "transInit: empty initializer");
      return null;
  }


  // TRANSLATE BITFIELDLIST
  Exp transBFList(Absyn.bflist b) {
    //if leaf...
    //ExpTy typ = transBFVal(b.typ);
    // ^ type of this BFVal

    //else...
    //Exp tail = transBFList(b.tail);
    // ^ next BFVal in BFList (?)
    return null;
  }

  //  TRANSLATE BFVAL
  ExpTy transBFVal(Absyn.bfval b) {
      if (b == null) return null;

      Type ty = null;

      // map bfval to actual Type
      switch (b.value) {   // assuming bfval has a field 'value' storing type name
          case "int":
              ty = Type.INT;
              break;
          case "char":
              ty = Type.CHAR;
              break;
          case "void":
              ty = Type.VOID;
              break;
          case "float":
              ty = Type.FLOAT;
              break;
          case "double":
              ty = Type.DOUBLE;
              break;
          case "long":
              ty = Type.LONG;
              break;
          case "short":
              ty = Type.SHORT;
              break;
          default:
              env.errorMsg.error(b.pos, "Unknown bfval type: " + b.value);
              return null;
      }

      return new ExpTy(null, ty);
  }


  //  TRANSLATE TYPENAME
  Type transTypeName(String t, int pos) {
    //System.out.println("inside transTypeName");
    if (t == null)
      return VOID;

    Type ty = null;
    switch (t) {   // assuming bfval has a field 'value' storing type name
          case "int":
              ty = Type.INT;
              break;
          case "char":
              ty = Type.CHAR;
              break;
          case "void":
              ty = Type.VOID;
              break;
          case "float":
              ty = Type.FLOAT;
              break;
          case "double":
              ty = Type.DOUBLE;
              break;
          case "long":
              ty = Type.LONG;
              break;
          case "short":
              ty = Type.SHORT;
              break;
          default:
              Symbol sym = Symbol.symbol(t);
              Types.Type base = (Types.Type) env.tenv.get(sym);
              if (base == null) {
                  env.errorMsg.error(pos, "Undefined type: " + t);
                  break;
              }
              if (base instanceof Types.NAME) {
                  base = ((Types.NAME) base).actual();
              }
              ty = Type.VOID;

              //System.out.println(base.getClass().getSimpleName());


              //ty = transType(t);
      }
    return ty;
  }

  //  TRANSLATE TYPE
  Types.Type transType(Absyn.Type t) {
    if (t == null) {
        return Type.INT;   // default
    }

    // Base type name (int, char, struct Foo, etc)
    String name = t.typeName != null ? t.typeName.name : null;
    if (name == null) {
        env.errorMsg.error(t.typeName.pos, "Missing type name");
        return Type.INT;
    }

    Symbol sym = Symbol.symbol(name);
    Types.Type base = (Types.Type) env.tenv.get(sym);

    if (base == null) {
        env.errorMsg.error(t.typeName.pos, "Undefined type: " + name);
        base = Type.INT;   // recovery
    }

    // Resolve aliased types
    if (base instanceof Types.NAME) {
        base = ((Types.NAME) base).actual();
    }

      if (t.typeArgs != null && t.typeArgs.brackets != null) {

          Absyn.ExpArrList dims = t.typeArgs.brackets.expArrList;
          //System.out.println("Test");

          List<Integer> dimList = new ArrayList<>();
          if(dims != null) {
            while (dims != null) {
                Absyn.Exp dimExp = dims.head.constExpr;
                if (!(dimExp instanceof Absyn.IntConstExp)) {
                    env.errorMsg.error(dimExp.pos, "Array size must be constant integer");
                    dimList.add(1);   // recovery
                } else {
                    dimList.add(((Absyn.IntConstExp) dimExp).value);
                }
                dims = dims.tail;
            }
          } else {
            //System.out.println("Test");
            if(t.typeArgs.brackets.empty != null) {
              Absyn.EmptyArrayTypeList dimSize = ((Absyn.EmptyArrayTypeList)t.typeArgs.brackets.empty);
              int count = 0;

              while(dimSize != null) {
                count ++;
                dimSize = dimSize.tail;
              }
              return new Types.ARRAY(base, count);
            }
            //ok now we must loop through the initlist somehow and set those dims to the the array dims.
          }

          return new Types.ARRAY(base, dimList);
      } 


    return base;
}



//endregion

//region: Statements
//  STATEMENT (Stm) DISPATCHER
  ExpTy transStm(Absyn.Stm s) {
     //System.out.println("Translating Stm of Type: " + s.getClass().getName());
    if (s == null) {
      return null;
    } else if (s instanceof Absyn.CompoundStm) {
      return transCmpdStm((Absyn.CompoundStm)s);
      // ^ compound statment
    } else if (s instanceof Absyn.Expstm) {
      return transExpStm((Absyn.Expstm)s);
      //return null;
      // ^ expression statement
    } else if (s instanceof Absyn.SelectStm) {
      return transSelectStm((Absyn.SelectStm)s);
      //return null;
      // ^ selection statement
    } else if (s instanceof Absyn.WhileStm) {
      return transWhileStm((Absyn.WhileStm)s);
      // ^ iteration statement 1/3, "while"
    } else if (s instanceof Absyn.DoWhileStm) {
      return transDoWhileStm((Absyn.DoWhileStm)s);
      // ^ iteration statement 2/3, "do while"
    } else if (s instanceof Absyn.ForStm) {
      return transForStm((Absyn.ForStm)s);
      // ^ iteration statement 3/3, "for"
    } else if (s instanceof Absyn.GotoStm) {
      //System.out.println("GOTO STMT");
      return transGotoStm((Absyn.GotoStm)s);
      // ^ jump statement 1/4, "goto"
    } else if (s instanceof Absyn.ContinueStm) {
      return transContStm((Absyn.ContinueStm)s);
      // ^ jump statment 2/4, "continue"
    } else if (s instanceof Absyn.BreakStm) {
      return transBrkStm((Absyn.BreakStm)s);
      // ^ jump statement 3/4, "break"
    } else if (s instanceof Absyn.ReturnStm) {
      return transReturnStm((Absyn.ReturnStm)s);
      //return null;
      // ^ jump statement 4/4, "return"
    } else if (s instanceof Absyn.LabelStm) {
      return transLabelStm((Absyn.LabelStm)s);
      //return null;
      // ^ label statement
    } else {
      env.errorMsg.error(s.pos, "transStm: unknown Stm type");
      return null; //place holder not sure
    }
  }

  private ExpList appendExpList(ExpList list, Exp exp) {
    if (list == null) 
        return new ExpList(exp, null);

    ExpList p = list;
    while (p.tail != null) p = p.tail;
    p.tail = new ExpList(exp, null);
    return list;
}

  ExpTy transCmpdStm(Absyn.CompoundStm s) {
      if (s == null)
          return new ExpTy(new Nx(null), Type.VOID);   // empty block

      beginScope();
      // env.venv.beginScope();
      // env.tenv.beginScope();

      ExpList decList = null;
      ExpList stmList = null;
      for (Absyn.DecList d = s.decls; d != null; d = d.tail) {
        Exp decIR = transDec(d.head); // returns Translate.Exp
        decList = appendExpList(decList, decIR);
          //transDec(d.head);   // ignore returned IR; they do not form value
      }

      
      ExpTy last = new ExpTy(new Nx(null), Type.VOID);  // default
      for (Absyn.StmList stm = s.stms; stm != null; stm = stm.tail) {
          last = transStm(stm.head);
        stmList = appendExpList(stmList, last.exp);
      }

      
      endScope();
      // env.venv.endScope();
      // env.tenv.endScope();
      Exp fullBodyIR = translate.FunctionBodyCmpdStm(decList, stmList);

      return new ExpTy(fullBodyIR, last.ty);   // result of last statement
  }

  ExpTy transExpStm(Absyn.Expstm s) {
      if (s == null || s.expression == null)
          return new ExpTy(new Nx(null), Type.VOID);

      
      ExpTy et = transExp(s.expression);

      
      if (et == null || et.exp == null)
          return new ExpTy(new Nx(null), Type.VOID);

      return et;   
  }

  ExpTy transReturnStm(Absyn.ReturnStm r) {
      //System.out.println("RETURN STMT");

      if (r == null) {
          //System.out.println("NULL RETURN STMT");
          return new ExpTy(new Nx(null), Type.VOID);
      }

      
      if (r.exp == null) {
          //System.out.println("RETURN VOID");
          return new ExpTy(new Nx(null), Type.VOID);
      }

      
      ExpTy t = transExp(r.exp);

      if (t == null) {
          //System.out.println("NULL ExpTy from return expression");
          return new ExpTy(new Nx(null), Type.VOID);
      }

      // System.out.println("Return expression IR: " + t.exp.getClass().getName());
      // System.out.println("Return type: " + t.ty);

      
      return t;
  }

  ExpTy transSelectStm(Absyn.SelectStm s) {
    if (s == null) {return null;}

    ExpTy condTy = null;
    ExpTy thenTy = null;
    ExpTy elseTy = null;
    if (s.expression != null) {
      condTy = transExp(s.expression);
    }


    if (s.Stm1 != null) {
      thenTy = transStm(s.Stm1);
    }
    if (s.Stm2 != null) {
      elseTy = transStm(s.Stm2);
    }

     Exp treeexp = null;
    if(elseTy != null) {
      treeexp = translate.IfExp(condTy.exp, thenTy.exp, elseTy.exp);
    } else {
      treeexp = translate.IfExp(condTy.exp, thenTy.exp,  new Ex(new CONST(0)));
    }
    
    return new ExpTy(treeexp, Type.VOID);
  }

  ExpTy transWhileStm (Absyn.WhileStm w) {
    if (w == null) {return null;}

    ExpTy testTy = transExp(w.test);
    // check that the type of "test" checks for a loop
    if (w.test instanceof AssignExpA) {
      env.errorMsg.error(w.pos, "while loop condition cannot be an assign expression");
    }

    if (!(testTy.ty instanceof Types.INT)) {
      env.errorMsg.error(w.pos, "while loop condition must be int so it can be evaluated to true or false");
    }

    boolean oldInLoop = inLoop;
    inLoop = true;

    ExpTy bodyExp = null;
    if (w.body != null) {
      bodyExp = transStm(w.body);
    }
    Label done = new Label();
    Exp treeexp = translate.WhileExp(testTy.exp, bodyExp.exp, done);

    inLoop = oldInLoop;

    return new ExpTy(treeexp, Type.VOID);
  }

  ExpTy transDoWhileStm(Absyn.DoWhileStm d) {
    if (d == null) {return null;}

    boolean oldInLoop = inLoop;
    inLoop = true;

    ExpTy bodyExp = null;
    if (d.body != null) {
      bodyExp = transStm(d.body);
    }
    
    inLoop = oldInLoop;
    ExpTy condTy = null;
    if (d.condition != null) {
      condTy = transExp(d.condition);
      if (d.condition instanceof AssignExpA) {
        env.errorMsg.error(d.pos, "dp-while loop condition cannot be an assign expression");
      }

      // check that the type of condition can be evaluated for a loop
      if (!(condTy.ty instanceof Types.INT)) {
        env.errorMsg.error(d.pos, "dp-while loop condition must be an INT so it can be evaulated as T/F");
      }
    }

    Exp treeexp = translate.DoWhileExp(bodyExp.exp, condTy.exp, new Label());

    return new ExpTy(treeexp, Type.VOID);
  }

 ExpTy transGotoStm(Absyn.GotoStm g) {
    Label target = labelEnv.get(g.label);
    if (target == null) {
        env.errorMsg.error(g.pos, "undefined label: " + g.label);
        // fallback to a fresh label to avoid null pointer, optional
        target = new Label(g.label.toString());
    }

    Exp treeexp = translate.GoTo(target);
    return new ExpTy(treeexp, Type.VOID);
}


  ExpTy transForStm(Absyn.ForStm f) {
    if (f == null) {
      return null;
    }

    if (f.init != null) {transExp(f.init);}
    
    if (f.condition != null) {
      ExpTy condTy = transExp(f.condition);

      if (!condTy.ty.coerceTo(Type.INT)) {
        env.errorMsg.error(f.pos, "for loop condition must be of type INT so it can be evaluated");
      }
    }
    
    if (f.increment != null) {transExp(f.increment);}

    boolean oldInLoop = inLoop;
    inLoop = true;

    if (f.body != null) {transStm(f.body);}
    
    inLoop = oldInLoop;

    return null;
  }

  ExpTy transContStm(Absyn.ContinueStm c) {
    if (!inLoop) {env.errorMsg.error(c.pos, "'continue' not inside a loop");}

    return null;
  }

  ExpTy transBrkStm(Absyn.BreakStm b) {
    if (b == null) {return null;}

    if (!inLoop) {
      env.errorMsg.error(b.pos, "break not inside loop");
    }

    return null;
  }

  ExpTy transLabelStm(Absyn.LabelStm l) {
    if (l == null) return null;

    // 1️⃣ Create or retrieve the IR label
    Label irLabel = null;
    if (l.label != null) {
        irLabel = new Label(l.label.toString());
        labelEnv.put(l.label, irLabel);  // store in label environment for gotos
    }

    // 2️⃣ Translate the statement attached to the label
    ExpTy bodyExp = null;
    if (l.stm != null) {
        bodyExp = transStm(l.stm);
    }

    // 3️⃣ Chain the LABEL and body using SEQ
    Exp treeexp;
    if (bodyExp != null) {
        Tree.Stm labelStm = translate.Label(irLabel).unNx();  // Nx -> Stm
        Tree.Stm bodyStm  = bodyExp != null ? bodyExp.exp.unNx() : new Tree.UEXP(new CONST(0));

        treeexp = new Nx(new SEQ(labelStm, bodyStm));

    } else {
        treeexp = translate.Label(irLabel);
    }

    // 4️⃣ Return as ExpTy with VOID type
    return new ExpTy(treeexp, Type.VOID);
}


//endregion

//region: Expressions
  //  EXPRESSION (Exp) DISPATCHER
  ExpTy transExp(Absyn.Exp e) {
    if(e != null) {
      System.out.println("Translating Exp of Type: " + e.getClass().getName());
    }
    
    if (e == null) {
      return null;
    } else if (e instanceof Absyn.VarExp) {
      return transVarExp((Absyn.VarExp)e);
      //return null;
      // ^ var expression (?)
    } else if (e instanceof Absyn.StringExp) {
      return transStringExp((Absyn.StringExp)e);
      //return null;
      // ^ string expression (?)
    } else if(e instanceof Absyn.IntConstExp) {
      return transIntConstExp ((Absyn.IntConstExp)e);
      // ^ integer constant expression
    } else if (e instanceof Absyn.CharConstExp) {
      return transCharConstExp((Absyn.CharConstExp)e);
      //return null;
      // ^ char constant expression
    } else if (e instanceof Absyn.AssignExpA) {
      return transAssignExp((Absyn.AssignExpA)e);
      //return null;
      // ^ assignment expression
    } else if (e instanceof Absyn.UnaryOpExp) {
      return transUnaryOpExp((Absyn.UnaryOpExp)e);
      //return null;
      // ^ unary operator expression (?)
    } else if (e instanceof Absyn.ArrayAccessExp) {
      return transArrayAccessExp((Absyn.ArrayAccessExp)e);
      //return null;
      // ^ array access expression (?)
    } else if (e instanceof Absyn.CastExp) {
      return transCastExp((Absyn.CastExp)e);
      //return null;
      // ^ cast expression
    } else if (e instanceof Absyn.SizeofExp) {
      return transSizeofExp((Absyn.SizeofExp)e);
      //return null;
      // ^ sizeof expression (?)
    } else if (e instanceof Absyn.SizeofTypeExp) {
      return transSizeofTypeExp((Absyn.SizeofTypeExp)e);
      //return null;
      // ^ sizeof type expression (?)
    } else if (e instanceof Absyn.CondExpA) {
      return transCondExpA((Absyn.CondExpA)e);
      //return null;
      // ^ conditional expression
    } else if (e instanceof Absyn.BinOpExp) {
      return transBinOpExp((Absyn.BinOpExp)e);
      // ^ binary operator expression (?)
    } else if (e instanceof Absyn.OpExpA) {
      return transOpExpA((Absyn.OpExpA)e);
      // ^ some bitwise expression (?)
    } else if (e instanceof Absyn.OrExpA) {
      return transOrExpA((Absyn.OrExpA)e);
      // ^ logcal or expression
    } else if (e instanceof Absyn.AndExpA) {
      return transAndExp((Absyn.AndExpA)e);
      //return null;
      // ^ logical and expression
    } else if (e instanceof Absyn.SeqExp) {
      return transSeqExp((Absyn.SeqExp)e);
      //return null;
      // ^ sequential (?) expression
    } else if (e instanceof Absyn.CallExp) {
      return transCallExp((Absyn.CallExp)e);
      //return null;
      // ^ call expression (?)
    } else if (e instanceof Absyn.FieldAccessExp) {
      return transFieldAccessExp((Absyn.FieldAccessExp)e);
      //return null;
      // ^ field access expression
    } else if (e instanceof Absyn.PointerAccessExp) {
      return transPointAccExp((Absyn.PointerAccessExp)e);
      // ^ pointer acceess expression
    } else if (e instanceof Absyn.InitList) {
      //return transInitList((Absyn.InitList)e);
      return null;
    } else if (e instanceof Absyn.Init) {
      return transInit((Absyn.Init)e);
    } else if (e instanceof Absyn.OpExp) {
      return transOpExp((Absyn.OpExp)e);
    } else {
      throw new Error("transExp: unknown Exp type");
    }
  }

  
  ExpTy transCondExpA(Absyn.CondExpA c) {
    if (c == null) {return null;}

    ExpTy condTy = transExp(c.condition);
    ExpTy thenTy = transExp(c.thenExp);
    ExpTy elseTy = transExp(c.elsExp);

    // must be int so can evaluate to T/F
    if (!condTy.ty.coerceTo(Type.INT)) {
      env.errorMsg.error(c.pos, "condition must be an int");
    }

    // prevents if x > 3 then x + 1 else x - 1.3
    // compiler can't determine a single type for the whole expression if "then" and "else" mismatch
    if (!thenTy.ty.actual().coerceTo(elseTy.ty.actual())) {
      env.errorMsg.error(c.pos, "then and else branches must have compatible types");
    }

    // if "then" and "else" are the same type return that type
    return new ExpTy(null, thenTy.ty.actual());
   }

  ExpTy transStringExp(Absyn.StringExp s) {
    if (s == null) {return null;}

    List<Integer> dim = new ArrayList<>();

    dim.add(s.value.length());

    Types.ARRAY charArray = new ARRAY(CHAR, dim); 

    Exp treeexp = translate.StringExp(s.value);

    return new ExpTy(treeexp, charArray);
  }

  ExpTy transArrayAccessExp(Absyn.ArrayAccessExp a) {
    if (a == null) return null;
    //System.out.println("a.array: " + a.array.getClass().getName());
    //System.out.println("bouta trans Exp");
    ExpTy arr = transExp(a.array);
    //System.out.println("arr: " + arr.getClass().getName());
    if (arr == null || arr.ty == null) {
        env.errorMsg.error(a.pos, "Invalid array expression");
        return null;
    }

    Type baseType = actual(arr.ty);

    if (!(baseType instanceof ARRAY)) {
        env.errorMsg.error(a.pos, "Attempting array access on non-array type");
        return null;
    }
    ARRAY arrayType = (ARRAY) baseType;

    ExpTy idx = transExp(a.index);
    if (idx == null || idx.ty == null) {
      env.errorMsg.error(a.pos, "Invalid array index expression");
      return null;
    }
        

    if (!typeEq(idx.ty, Type.INT)) {
      env.errorMsg.error(a.pos, "Array index must be INT, found: " + idx.ty);
      return null;
    }

    if (a.index instanceof Absyn.IntConstExp) {
        int indexVal = ((Absyn.IntConstExp)a.index).value;

        int lastIdx = arrayType.dims.size() - 1;
        int dimSize = arrayType.dims.get(lastIdx);

        if (indexVal < 0 || indexVal >= dimSize) {
            env.errorMsg.error(a.pos, 
                "Array index out of bounds: " + indexVal +
                " (size = " + dimSize + ")"
            );
            //return new ExpTy(null, Type.INT);
        }
    }

    Type resultType;
    if (arrayType.dims != null && arrayType.dims.size() > 1) {
        // remove from the END
        List<Integer> remaining = arrayType.dims.subList(0, arrayType.dims.size() - 1);
        resultType = new ARRAY(arrayType.element, remaining);
    } else {
        resultType = arrayType.element;
    }

   int elemSize = sizeof(arrayType.element); // e.g., char=1, int=4

    // IR for: arrExp + idxExp * elemSize
    Tree.Exp addr = new Tree.BINOP(
        Tree.BINOP.PLUS,
        arr.exp.unEx(),
        new Tree.BINOP(
            Tree.BINOP.MUL,
            idx.exp.unEx(),
            new Tree.CONST(elemSize)
        )
    );

    // IR for: MEM(address)
    Tree.Exp memRead = new Tree.MEM(addr);

    return new ExpTy(new Ex(memRead), resultType);
}




  ExpTy transOpExp(Absyn.OpExp o) {
    ExpTy leftTy = transExp(o.left);
    ExpTy rightTy = transExp(o.right);

    if (!leftTy.ty.coerceTo(Type.INT) || !rightTy.ty.coerceTo(Type.INT)) {
      env.errorMsg.error(o.pos, "comparison/arithmetic operands must be int");
    }

    return new ExpTy(null, Type.INT);
  }


    ExpTy transUnaryOpExp(Absyn.UnaryOpExp u) {
      ExpTy subTy = transExp(u.exp);

      if(u.op != UnaryOpExp.BITWISEAND) {
        if (!subTy.ty.coerceTo(Type.INT)) {
          env.errorMsg.error(u.pos, "unary operator requires int operand");
        }
      } else {
        return new ExpTy(subTy.exp, subTy.ty);
      }  
      return new ExpTy(null, Type.INT);
    }

    ExpTy transCastExp(Absyn.CastExp e) {
      ExpTy expType = transExp(e.exp);
      Type castType = transTypeName(e.type.name, e.pos);

      if(expType != null) {
        if(!typeEq(castType, expType.ty)) {
          env.errorMsg.error(e.pos, "cannot cast type " + expType.ty.getClass().getSimpleName() + " to " + castType.getClass().getSimpleName());
          return new ExpTy(null, Type.INT);
        }
      } else {
        env.errorMsg.error(e.pos, "Expression type is null");
        return new ExpTy(null, Type.INT);
      }

      return new ExpTy(expType.exp, castType);
    }


  /* transVarExp
   * transStringExp
   * transIntConstExp
   * transCharConstExp
   * transAssignExp
   * transUnaryOpExp
   * transArrayAccessExp
   * transCastExp
   * transSizeofExp
   * transSizeofTypeExp
   * transCondExp */

    int sizeof(Type t) {
      if (t instanceof Types.INT) return 4;       // int = 4 bytes
      if (t instanceof Types.CHAR) return 1;      // char = 1 byte
      if (t instanceof Types.FLOAT) return 4;     // float = 4 bytes
      if (t instanceof Types.DOUBLE) return 8;    // double = 8 bytes
      // if (t instanceof Types.PTR) return 8;       // pointer = 8 bytes on 64-bit
      if (t instanceof Types.ARRAY) {             
          Types.ARRAY a = (Types.ARRAY) t;
          int totalElems = 1;
          if (a.dims != null && !a.dims.isEmpty()) {
              for (int dim : a.dims) {
                  totalElems *= dim;      // multiply all dimensions
              }
          } else {
              totalElems = a.emptyArrayDimSize > 0 ? a.emptyArrayDimSize : 1;
          }
          return sizeof(a.element) * totalElems;
      }

      if (t instanceof Types.STRUCT) {            // struct = sum of field sizes (simplified)
          Types.STRUCT s = (Types.STRUCT) t;
          int total = 0;
          for (Type f : s.fields.values()) total += sizeof(f);
          return total;
      }
      if (t instanceof Types.UNION) {             // union = max of field sizes
          Types.UNION u = (Types.UNION) t;
          int max = 0;
          for (Type f : u.fields.values()) max = Math.max(max, sizeof(f));
          return max;
      }
      return 4; // default size
  }


  ExpTy transSizeofTypeExp(Absyn.SizeofTypeExp s) {
    if (s == null || s.type == null) {return null;}

    Type ty = transTypeName(s.type.name, s.pos);

    if (ty == null) {
      env.errorMsg.error(s.pos, "empty/unknown type in sizeof");
    }

    // sizeof, if typecheck successful, always returns INT
    int size = sizeof(ty);
    return new ExpTy(new Ex(new CONST(size)), Type.INT);
  }

  ExpTy transSizeofExp(Absyn.SizeofExp s) {
      if (s == null) {
        return null;
  
      } else if (s.expr != null) {
        ExpTy expty = transExp(s.expr);       // get type of expr
        int size = sizeof(expty.ty);          // compute size manually
        return new ExpTy(new Ex(new CONST(size)), Type.INT);
  
      } else {
        env.errorMsg.error(s.pos, "error Message");
        return new ExpTy(null, Type.INT); // or return null if you prefer
      }
    }

  // ExpTy transSizeofExpA(Absyn.SizeofExpA s) {
  //   if (s == null) {
  //     return null;

  //   } else if (s.isType == true) {
  //     ExpTy t = new ExpTy(null, transTypeName(Symbol.symbol(s.typeName.name)));

  //     /* struct exmaple;         // forward declaration — incomplete type
  //     * struct example *p;       // OK (sizeof pointer is known)
  //     * sizeof(struct example);  // ERROR — incomplete type */
  //     // ^ implement helper function for checking type completeness?

  //     return new ExpTy(null, t.ty);

  //   } else if (s.expr != null) {
  //     ExpTy e = transExp(s.expr);
  //     return new ExpTy(null, INT);

  //   } else {
  //     env.errorMsg.error(s.pos, "error Message");
  //     return new ExpTy(null, Type.INT); // or return null if you prefer
  //   }
  // }

    ExpTy transVarExp(Absyn.VarExp e) {
        if (e == null || e.var == null) {
          System.out.println("this null");
          return new ExpTy(new Nx(null), Type.VOID);

        }
            
        return transVar(e.var);
    }


    ExpTy transAssignExp(AssignExpA e) {
        Type lhsType = null;
        
        if (e.left instanceof VarExp) {
            //System.out.println("NOT A NULL RETURN VAREXP");
            //System.out.println(e.pos + " the position is a var exp");
            VarExp leftVarExp = (VarExp)e.left;
            if (leftVarExp.var instanceof SimpleVar) {
                //System.out.println("NOT A NULL RETURN SIMPLEVAR");
                //System.out.println(e.pos + " is not a SIMPLEVAR");
                Symbol name = ((SimpleVar)leftVarExp.var).name;
                VarEntry entry = (VarEntry) env.venv.get(name);
                if (entry == null) {
                  env.errorMsg.error(e.pos, "Undeclared variable: " + name);
                  return new ExpTy(new Nx(null), Type.INT);
                }
                lhsType = entry.ty;
            } else {
                
                //System.out.println("NULL RETURN");
                //return null;
                //lhsType = transVar(leftVarExp.var).ty;
            }
        } else {
            //System.out.println("NOT VAREXP");
            //System.out.println(e.pos + " the position is not a var exp");
            if(transExp(e.left) != null) {
              lhsType = transExp(e.left).ty;
            } else {

              //System.out.println("The Above is NULL 4 ^^^");
            }
            
        }

        //System.out.println("e.right: " + e.right.getClass().getName());
        ExpTy right = transExp(e.right);
        ExpTy left = transExp(e.left); //may cause issues check later
        if(lhsType instanceof ARRAY && right.ty instanceof ARRAY) {
          if(((ARRAY)lhsType).dims != null) {
            String errorMsg = "Array size mismatch for assignment expression";
            validateArrayDims((ARRAY)lhsType, (ARRAY)right.ty, e.pos, errorMsg);
          } else {
            if(((ARRAY)lhsType).emptyArrayDimSize != ((ARRAY)right.ty).dims.size()) {
              env.errorMsg.error(e.pos, "Array size mismatch for assignment expression");
            }
          }
          
          if (!typeEq(lhsType, right.ty)) {
            env.errorMsg.error(e.pos, "Type mismatch: cannot assign " + right.ty.getClass().getSimpleName() + " of " + ((ARRAY)right.ty).element.getClass().getSimpleName() + 
            " to " + lhsType.getClass().getSimpleName() + " of " + ((ARRAY)lhsType).element.getClass().getSimpleName());
            return new ExpTy(new Nx(null), Type.INT);
          } else {
            Exp treeexp = translate.AssignExp(left.exp, right.exp);
            return new ExpTy(treeexp, lhsType);
          }
        } else {
          if(lhsType != null && right != null) {
            //System.out.println("both sides not null" + " lhsType: " + lhsType.getClass().getSimpleName() + " right.ty: " + right.ty.getClass().getSimpleName());
            if(!(lhsType instanceof ARRAY) && right.ty instanceof ARRAY) {
              //System.out.println("array to int");
              env.errorMsg.error(e.pos, "Type mismatch: cannot assign " + right.ty.getClass().getSimpleName() + " to " + lhsType.getClass().getSimpleName());
              return new ExpTy(new Nx(null), Type.INT);
            }
            if (!typeEq(lhsType, right.ty)) {
              env.errorMsg.error(e.pos, "Type mismatch: cannot assign " + right.ty.getClass().getSimpleName() + " to " + lhsType.getClass().getSimpleName());
              //System.out.println("=====================");
              return new ExpTy(new Nx(null), Type.INT);
            } else {
              Exp treeexp = translate.AssignExp(left.exp, right.exp);
              return new ExpTy(treeexp, lhsType);
            }
          } else {
            //System.out.println("The Above is NULL 3 ^^^");
            
          }
        }
        

        return new ExpTy(new Nx(null), Type.VOID);
    }



    public ExpTy transIntConstExp(Absyn.IntConstExp e) {
    if (e == null) return null;

    Type ty = Type.INT;  
    Exp treeexp = translate.IntExp(e.value);  
    return new ExpTy(treeexp, ty);
}


   // Example of translating a character constant expression
    public ExpTy transCharConstExp(CharConstExp e) {
        if (e == null) return null;

       
        Type ty = Type.CHAR;  

        
        Exp treeexp = translate.CharExp(e.value);     

        
        return new ExpTy(treeexp, ty);
    }

   ExpTy transBinOpExp(Absyn.BinOpExp b) {
    //if b is null: ...

    //if leaf node: ...
      //instance of: accaptable type -> return TYPE (?)
      //something else: error
    
    //else...
    ExpTy left = transExp(b.left);
    ExpTy right = transExp(b.right);

    //if the acutal type of left can be coerced to the actual type of right
    if(right != null && left != null) {

    
      if (left.ty.actual().coerceTo(right.ty.actual())) {
        //System.out.println(b.op);
        Exp treeexp = translate.OpExp(b.op, left.exp, right.exp);
        return new ExpTy(treeexp, left.ty.actual());
      } else {
        env.errorMsg.error(b.pos, "bitwise operation: left/right arg type mismatch");
        return new ExpTy(null, Type.INT);   // fallback
      }
    } else {
      //System.out.println("The Above is NULL 5 ^^^");
      return new ExpTy(null, Type.INT);
    }
   }

   ExpTy transOpExpA(Absyn.OpExpA o) {
    //if o is null: ...

    //if leaf node: ...
      //instance of: accaptable type -> return TYPE (?)
      //something else: error

    //else...
    ExpTy left = transExp(o.left);
    ExpTy right = transExp(o.right);

    //if the acutal type of left can be coerced to the actual type of right
    if (left.ty.actual().coerceTo(right.ty.actual())) {
      return new ExpTy(null, left.ty.actual());
    } else {
      env.errorMsg.error(o.pos, "bitwise operation: left/right arg type mismatch");
      return new ExpTy(null, Type.INT);   // ✅ fallback

    }
    
   }

   ExpTy transOrExpA(Absyn.OrExpA o) {
    //if o is null: ...

    //if leaf node: ...
      //instance of: accaptable type -> return TYPE (?)
      //something else: error

    //else...
    ExpTy left = transExp(o.left);
    ExpTy right = transExp(o.right);

    //if the acutal type of left can be coerced to the actual type of right
    if(left != null && right != null) {
      if (left.ty.actual().coerceTo(right.ty.actual())) {
        return new ExpTy(null, left.ty.actual());
      } else {
        env.errorMsg.error(o.pos, "bitwise operation: left/right arg type mismatch");
        return new ExpTy(null, Type.INT);   // ✅ fallback

      }
    } else {
      //System.out.println("The Above is NULL 2 ^^^");
      return new ExpTy(null, Type.INT);
    }
   }

   ExpTy transAndExp(Absyn.AndExpA a) {
    //if a is null: ...

    //if leaf node: ...
      //instance of: accaptable type -> return TYPE (?)
      //something else: error

    //else...
    ExpTy left = transExp(a.left);
    ExpTy right = transExp(a.right);

    //if the acutal type of left can be coerced to the actual type of right
    if (left.ty.actual().coerceTo(right.ty.actual())) {
      return new ExpTy(null, left.ty.actual());
    } else {
      env.errorMsg.error(a.pos, "bitwise operation: left/right arg type mismatch");
      return new ExpTy(null, Type.INT);   // fallback
    }
   }
   ExpTy transCallExp(Absyn.CallExp c) {
      if (c == null) return new ExpTy(new Nx(null), Type.VOID);

      // Ensure func name is simple
      if (!(c.func instanceof Absyn.VarExp)) {
          env.errorMsg.error(c.pos, "Function call must be a simple function name");
          return null;
      }

      Absyn.VarExp funcVar = (Absyn.VarExp)c.func;
      Symbol funcName = ((Absyn.SimpleVar)funcVar.var).name;

      // Lookup function
      Entry entry = (Entry)(env.venv.get(funcName));
      if (!(entry instanceof FunEntry)) {
          env.errorMsg.error(c.pos, "Calling a non-function: " + funcName);
          return null;
      }
      FunEntry fe = (FunEntry)entry;

      // Formals from function declaration
      FUNC formal = fe.formals;

      // Actual argument list
      Absyn.ExpList actual = c.args;

      // Compare actuals and formals
      while (actual != null && formal != null) {
          ExpTy actualTy = transExp(actual.head);

        if(!formal.isPointer) {
          //System.out.println("Call Type: " + actualTy.ty.getClass().getSimpleName() + " Fun Type: " + formal.fieldType.getClass().getSimpleName());
          if (actualTy == null || actualTy.ty == null) {
              env.errorMsg.error(actual.head.pos, "Bad argument");
          
          } else if (formal.fieldType instanceof ARRAY && actualTy.ty instanceof ARRAY) {
            if(!typeEq(((ARRAY)formal.fieldType).element, ((ARRAY)actualTy.ty).element) ) {
              env.errorMsg.error(c.pos,
                  "Argument type mismatch: expected "
                  + formal.fieldType.getClass().getSimpleName() + " of type " + ((ARRAY)formal.fieldType).element.getClass().getSimpleName()
                  + ", got "
                  + actualTy.ty.getClass().getSimpleName() + " of type " + ((ARRAY)actualTy.ty).element.getClass().getSimpleName()
              );
            } else {
              if(((ARRAY)formal.fieldType).dims == null) {
                if(((ARRAY)formal.fieldType).emptyArrayDimSize != ((ARRAY)actualTy.ty).dims.size()) {
                  env.errorMsg.error(c.pos, "Array Size mismatch for function call");
                }
                new ARRAY(((ARRAY)actualTy.ty).element, ((ARRAY)actualTy.ty).dims);
              } else {
                String erroMsg = "Array Size mismatch for function call";
                validateArrayDims((ARRAY)formal.fieldType, (ARRAY)actualTy.ty, c.pos, erroMsg);
              }
              
              
            }
              
              
          } else if (!typeEq(formal.fieldType, actualTy.ty)) {
              env.errorMsg.error(actual.head.pos,
                  "Argument type mismatch: expected "
                  + formal.fieldType.getClass().getSimpleName()
                  + ", got "
                  + actualTy.ty.getClass().getSimpleName()
              );
          }
        } else if (formal.isPointer) {
          if (actualTy == null || actualTy.ty == null) {
              env.errorMsg.error(actual.head.pos, "Bad argument");
          } else if (!(actualTy.ty instanceof INT)) {
                  env.errorMsg.error(actual.head.pos, "Cannot assign non-int to pointer");
                  // *** DO NOT return
              
          }
        }

          actual = actual.tail;
          formal = formal.tail;
      }

      // Too many args
      if (actual != null) {
          env.errorMsg.error(c.pos, "Too many arguments in call to " + funcName);
      }

      // Too few args
      if (formal != null) {
          env.errorMsg.error(c.pos, "Too few arguments in call to " + funcName);
      }

      // c.args is Absyn.ExpList
      Absyn.ExpList a = c.args;

      ExpList irArgs = null;
      ExpList irTail = null;

      while (a != null) {
          // translate each AST arg into IR 
          ExpTy irArg = transExp(a.head);

          ExpList node = new ExpList(irArg.exp, null);

          if (irArgs == null) {
              irArgs = node;
              irTail = node;
          } else {
              irTail.tail = node;
              irTail = node;
          }

          a = a.tail;
      }

      // now call the IR call constructor with the translated arg list
      Exp treeexp = translate.FunExp(funcName, irArgs);

      // Return result type
      return new ExpTy(treeexp, fe.result);
  }


  ExpTy transFieldAccessExp(Absyn.FieldAccessExp fieldExp) {
    // Step 1: Translate the record expression (left side)
      ExpTy recordExp = transExp(fieldExp.record);

      // Step 2: Get the type of the record expression
      Types.Type recordType = recordExp.ty.actual();

      // Step 3: Ensure it’s a STRUCT or UNION
      if (!(recordType instanceof Types.STRUCT) && !(recordType instanceof Types.UNION)) {
          env.errorMsg.error(fieldExp.pos, "Field access on non-struct/union type");
          return new ExpTy(null, Type.INT); // default error recovery
      }

      // Step 4: Convert the field name to a symbol
      Symbol fieldSym = Symbol.symbol(fieldExp.field);

      // Step 5: Lookup field type in the structure
      Types.Type fieldType = null;
      if (recordType instanceof Types.STRUCT) {
          fieldType = ((Types.STRUCT) recordType).fields.get(fieldSym);
      } else if (recordType instanceof Types.UNION) {
          ((Types.UNION)recordType).setActiveField(fieldSym);
          //System.out.println("Active field: " + ((Types.UNION)recordType).getActiveType());
          fieldType = ((Types.UNION) recordType).fields.get(fieldSym);
      }

      // Step 6: Report error if field doesn’t exist
      if (fieldType == null) {
          env.errorMsg.error(fieldExp.pos, "Unknown field '" + fieldExp.field + "'");
          return new ExpTy(null, Type.INT);
      }

      return new ExpTy(new Nx(null), fieldType);
  }

  ExpTy transSeqExp(Absyn.SeqExp s) {
    if (s == null || s.list == null) return null;
    return transExpList(s.list);
  }

  ExpTy transPointAccExp(Absyn.PointerAccessExp p) {
      if (p == null) return null;

      // 1. Translate pointer expression
      Absyn.Var pointerVar = null;
      Symbol pointerName = null;
      ExpTy ptrTy = null;
      if(p.pointer instanceof VarExp) {
        pointerVar = ((Absyn.VarExp)p.pointer).var;
        //System.out.println(pointerVar.getClass().getSimpleName());
        pointerName = ((SimpleVar)pointerVar).name;
        //System.out.println(pointerName.toString());
        ptrTy = transVar(pointerVar);
        //System.out.println(ptrTy.getClass().getSimpleName());
      } else {
        env.errorMsg.error(p.pos, "pointer must be a variable");
      }
      

      //Type ptrTy = transType(base);
      if (ptrTy == null) return null;

      //System.out.println(p.pointer.getClass().getSimpleName());
      // 2. Must be a struct type
      if (!(ptrTy.ty instanceof Types.STRUCT) && !(ptrTy.ty instanceof Types.UNION)) {
          //System.out.println(ptrTy.ty.getClass().getSimpleName());
          env.errorMsg.error(p.pos, "field access requires a struct pointer or union pointer");
          return new ExpTy(null, Type.INT); // fallback
      } else {
          Entry entry = (Entry)(env.venv.get(pointerName));
          // if (!(entry instanceof VarEntry)) {
          //     //System.out.println("Simple var is not instancce of VarEntry");
          //     //env.errorMsg.error(p.pos, "Undefined variable: ");
          //     return new ExpTy(new Ex(null), Type.INT);   // placeholder
          // }

          VarEntry var = (VarEntry) entry;
          
          if(var.isPointer) {
            if(ptrTy.ty instanceof Types.STRUCT) {
              Types.STRUCT structTy = (Types.STRUCT) ptrTy.ty;

              // 3. Check field exists
              if (!structTy.fields.containsKey(Symbol.symbol(p.field))) {
                  env.errorMsg.error(p.pos, "field '" + p.field + "' does not exist in struct");
                  return new ExpTy(null, Type.INT); // fallback
              }

              // 4. Return type of field
              //Type fieldType = structTy.fields.get(Symbol.symbol(p.field));
              return new ExpTy(null, Type.INT);
            } else if(ptrTy.ty instanceof Types.UNION) {
                Types.UNION unionTy = (Types.UNION) ptrTy.ty;
                

                // 3. Check field exists
                if (!unionTy.fields.containsKey(Symbol.symbol(p.field))) {
                    env.errorMsg.error(p.pos, "field '" + p.field + "' does not exist in union");
                    return new ExpTy(null, Type.INT); // fallback
                }
                unionTy.setActiveField(Symbol.symbol(p.field));

                // 4. Return type of field
                //Type fieldType = structTy.fields.get(Symbol.symbol(p.field));
                return new ExpTy(null, Type.INT);
            } else {
              env.errorMsg.error(p.pos, "field access requires a struct or union pointer");
              return new ExpTy(null, Type.INT);
            }
          } else {
            env.errorMsg.error(p.pos, "field access requires a struct or union pointer");
            return new ExpTy(null, Type.INT); 
          }
          
      }
      

      
  }
   /* transSeqExp
   * transCallExp
   * transPointAccExp
   */
//endregion

//region: Lists
   /* Absyn.ExpList */

   Exp transStmList(Absyn.StmList s) {
    if (s == null) {return null;}

    if (s.head != null) {transStm(s.head);}
    transStmList(s.tail);

    return null;
   }

  ExpTy transExpList(Absyn.ExpList e) {
    if (e == null) {return null;}

    if (e.head != null) transExp(e.head);
    transExpList(e.tail);

    return null;
   }
   
   /* Absyn.EmptyArrayTypeList
   * Absyn.ExpArrList
   
   * Absyn.BrackList
   * Absyn.PointList
   * Absyn.InitList
   * Absyn.StructDecList
   * Absyn.EnumList
   * Absyn.ParamList
   * Absyn.ParamTypeList */
}







//every expression returns a type, plus maybe additional stuff
// is this thing left recursive?
