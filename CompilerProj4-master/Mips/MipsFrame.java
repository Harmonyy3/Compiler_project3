package Mips;
import java.util.Hashtable;
import java.util.Stack;

import Temp.TempMap;
import Symbol.Symbol;
import Temp.Temp;
import Temp.TempList;
import Temp.Label;
import Frame.Frame;
import Frame.Access;
import Frame.AccessList;
import Frame.InReg;
import Frame.Proc;
import Frame.InFrame;

//For task 2, implement a method allocLocal in Mips.MipsFrame and write code for allocating formal parameters.
// parameters, local variables, saved register, and return address 
public class MipsFrame extends Frame implements TempMap {
  

  private int count = 0;
  private int localCount = 0;

  public int frameSize() {
      return localCount * wordSize;  // total bytes for locals
  }

  public int framesizeOffset(int base) {
      return base + frameSize();
}


  public Frame newFrame(Symbol name, Util.BoolList formals) {
    Label label;
    if (name == null)
      label = new Label();
    else if (this.name != null)
      label = new Label(this.name + "." + name + "." + count++);
    else
      label = new Label(name);
    return new MipsFrame(label, formals);
  }

  public MipsFrame() {}
  private MipsFrame(Label n, Util.BoolList f) {
    name = n;
    formals = makeFormals(f);
  }
  
  //for testing purposes
  public MipsFrame(Symbol name, Util.BoolList formals) {
    this(new Label(name), formals);
  }

  public class MipsRegs {
    public static final Temp FP = new Temp();
    public static final Temp SP = new Temp();
    public static final Temp V0 = new Temp();
}

  private static final int wordSize = 4;
  public int wordSize() { return wordSize; }

  //if escape is true, allocate in frame; else allocate in temperay register
  public AccessList locals = null;  // inside MipsFrame
  public Access allocLocal(boolean escape) {
    Access a;
    if (escape) {
        localCount++;
        a = new InFrame(this, -localCount * wordSize);
    } else {
        a = new InReg(this, new Temp());
    }
    locals = new AccessList(a, locals);  // track all locals
    return a;
}

  //allocate formals based on whether they escape or not
  private AccessList makeFormals(Util.BoolList formals) {

    System.out.println("Making formals");
    if (formals == null) return null;



    int offset = 8; // FP + 8
    AccessList head = null, tail = null;
    int index = 0;

    for (Util.BoolList f = formals; f != null; f = f.tail) {
        Access a;
        if (f.head) {
            // escaped → in frame
            a = new InFrame(this, offset);
            offset += wordSize;
        } else {
            // non-escaping → in registers
            Temp reg;
            switch(index) {
                case 0: reg = A0; System.out.println("A0"); break;
                case 1: reg = A1; System.out.println("A1"); break;
                case 2: reg = A2; System.out.println("A2"); break;
                case 3: reg = A3; System.out.println("A3"); break;
                default: reg = new Temp(); break; // beyond 4 args
            }
            a = new InReg(this, reg);
            index++;
        }

        if (head == null) head = tail = new AccessList(a, null);
        else {
            tail.tail = new AccessList(a, null);
            tail = tail.tail;
        }
    }

    return head;
}

@Override
public TempList registers() {
    return allRegs();   // or callerSaves+calleeSaves, or whatever you choose
}

@Override
public String tempMap(Temp t) {
    if (t == FP) return "$fp";
    if (t == SP) return "$sp";
    if (t == RA) return "$ra";
    if (t == RV) return "$v0";   // optional
    //System.out.println("Temp being called is = " + t.toString());
    return null;               // tells CombineMap: ask allocator
}





//-----------------------------------------------------------------------------------------------
  //according to pp. 198 of textbook, we need to make the following registers for Codegen.java
  //Special register, ZERO, FP, SP, RA, V0,RV
  static final Temp ZERO = new Temp();
  public static final Temp FP = new Temp(); // virtual frame pointer
  static final Temp SP = new Temp(); // real stack pointer
  public static final Temp V0 = new Temp(); // function result
  static final Temp V1 = new Temp();
  static final Temp RA = new Temp(); // return address
  static final Temp RV = new Temp();  // return value register
  static final Temp AT = new Temp(); // reserved for assembler 

  // Argument registers, A0, A1, A2, A3
  public static final Temp A0 = new Temp();
  public static final Temp A1 = new Temp();
  public static final Temp A2 = new Temp();
  public static final Temp A3 = new Temp();

  // call-saved registers, calldefs, calleesaves, callersaves
  // Caller-saved temporaries, not save
  static final Temp T0 = new Temp();
  static final Temp T1 = new Temp();
  static final Temp T2 = new Temp();
  static final Temp T3 = new Temp();
  static final Temp T4 = new Temp();
  static final Temp T5 = new Temp();
  static final Temp T6 = new Temp();
  static final Temp T7 = new Temp();

  // extra caller-saved temporaries
  static final Temp T8 = new Temp();
  static final Temp T9 = new Temp();

  //calleesaves, contents saved for use later
  static final Temp S0 = new Temp();
  static final Temp S1 = new Temp();
  static final Temp S2 = new Temp();
  static final Temp S3 = new Temp();
  static final Temp S4 = new Temp();
  static final Temp S5 = new Temp();
  static final Temp S6 = new Temp();
  static final Temp S7 = new Temp();

  static final Temp K0 = new Temp(); // reserved for OS kernel
  static final Temp K1 = new Temp(); // reserved for OS kernel
  static final Temp GP = new Temp(); // pointer to global area
  static final Temp S8 = new Temp(); // actual frame pointer

  int maxArgs = 0;

  

  public String string(Label lab, String string) {
    int length = string.length();
    String lit = "";
    for (int i = 0; i < length; i++) {
      char c = string.charAt(i);
      switch(c) {
      case '\b': lit += "\\b"; break;
      case '\t': lit += "\\t"; break;
      case '\n': lit += "\\n"; break;
      case '\f': lit += "\\f"; break;
      case '\r': lit += "\\r"; break;
      case '\"': lit += "\\\""; break;
      case '\\': lit += "\\\\"; break;
      default:
        if (c < ' ' || c > '~') {
          int v = (int)c;
          lit += "\\" + ((v>>6)&7) + ((v>>3)&7) + (v&7);
        } else
          lit += c;
        break;
      }
    }

    return "\t.data\n\t.word " + length + "\n" + lab.toString()
      + ":\t.asciiz\t\"" + lit + "\"";
  }

  private static final Label badPtr = new Label("_BADPTR");
  public Label badPtr() {
    return badPtr;
  }

  private static final Label badSub = new Label("_BADSUB");
  public Label badSub() {
    return badSub;
  }
  public Assem.InstrList codegen(Tree.Stm stm) {
    return (new Codegen(this)).codegen(stm);
  }

  private static final Hashtable<Temp, String> tempToString = new Hashtable<>(32);
static {
    tempToString.put(ZERO, "$0");
    tempToString.put(AT, "$at");
    tempToString.put(V0, "$v0");
    tempToString.put(V1, "$v1");
    tempToString.put(A0, "$a0");
    tempToString.put(A1, "$a1");
    tempToString.put(A2, "$a2");
    tempToString.put(A3, "$a3");
    tempToString.put(T0, "$t0");
    tempToString.put(T1, "$t1");
    tempToString.put(T2, "$t2");
    tempToString.put(T3, "$t3");
    tempToString.put(T4, "$t4");
    tempToString.put(T5, "$t5");
    tempToString.put(T6, "$t6");
    tempToString.put(T7, "$t7");
    tempToString.put(T8, "$t8");
    tempToString.put(T9, "$t9");
    tempToString.put(S0, "$s0");
    tempToString.put(S1, "$s1");
    tempToString.put(S2, "$s2");
    tempToString.put(S3, "$s3");
    tempToString.put(S4, "$s4");
    tempToString.put(S5, "$s5");
    tempToString.put(S6, "$s6");
    tempToString.put(S7, "$s7");
    tempToString.put(K0, "$k0");
    tempToString.put(K1, "$k1");
    tempToString.put(GP, "$gp");
    tempToString.put(SP, "$sp");
    tempToString.put(FP, "$fp"); // virtual
    tempToString.put(S8, "$s8"); // alias for FP
    tempToString.put(RA, "$ra");
}


// @Override
//     public String tempMap(Temp t) {
//         // Map temps to actual MIPS registers if you have a mapping
//         // For now, just return the temp name
//         return t.toString();
//     }
// pool of free temps
static Stack<String> freeTemps = new Stack<>();
static {
    for (int i = 9; i >= 0; i--) freeTemps.push("$t" + i);
}

static public Temp test(String s) {
  Temp T3 = new Temp();
  return T3;
}

// temp → register mapping
//static Hashtable<Temp,String> tempToString = new Hashtable<>();

// get register for a temp
public static String tempToString(Temp t) {
    if (tempToString.containsKey(t)) return tempToString.get(t);

    if (!freeTemps.isEmpty()) {
        String reg = freeTemps.pop();
        tempToString.put(t, reg);
        return reg;
    } else {
        // spill to $s0-$s7 if no $t available
        String reg = "$s" + tempCounter++;
        tempToString.put(t, reg);
        return reg;
    }
}

// free a temp when it dies
public static void freeTemp(Temp t) {
    String reg = tempToString.get(t);
    if (reg.startsWith("$t")) freeTemps.push(reg);
}

static int tempCounter = 0;



  public TempList allRegs() {
    return append(callerSaves, calleeSaves);
}

private static TempList append(TempList a, TempList b) {
    if (a == null) return b;
    TempList tail = a;
    while (tail.tail != null) tail = tail.tail;
    tail.tail = b;
    return a;
}


  public Assem.InstrList append(Assem.InstrList a, Assem.InstrList b) {
    if (a == null)
      return b;
    Assem.InstrList p;
    for (p = a; p.tail != null; p = p.tail);
    p.tail = b;
    return a;
  }


  static TempList L(Temp h, TempList t) {
    return new TempList(h, t);
  }
  static TempList L(Temp h) {
    return new TempList(h, null);
  }
  static TempList L(TempList a, TempList b) {
    return new TempList(a, b);
  }

  static public TempList specialRegs, argRegs, callerSaves, calleeSaves;
  {
    // registers dedicated to special purposes
    specialRegs = L(ZERO,L(AT,L(K0,L(K1,L(GP,L(FP,L(SP,L(RA))))))));
    // registers in which to pass outgoing arguments (including static link)
    argRegs     = L(A0,L(A1,L(A2,L(A3))));
    // registers that the called procedure (callee) must preserve for caller
    calleeSaves = L(S0,L(S1,L(S2,L(S3,L(S4,L(S5,L(S6,L(S7,L(S8)))))))));
    // registers that the callee may trash
    callerSaves = L(T0,L(T1,L(T2,L(T3,L(T4,L(T5,L(T6,L(T7,L(T8,L(T9))))))))));
    callerSaves = L(V0,L(V1, callerSaves));
  }

  public TempList calldefs, returnSink;
  {
    // registers live on return (flattened)
    returnSink = append(new TempList(V0, null),
                    append(specialRegs, calleeSaves));


    // registers defined by a call (flattened)
    calldefs = append(new TempList(RA, null),
                  append(argRegs, callerSaves));

  }

  public Assem.InstrList procEntryExit2(Assem.InstrList body) {

    
    // Just append return-sink instruction
    Assem.InstrList retSink = new Assem.InstrList(new Assem.OPER("", null, returnSink), null);
    return append(body, retSink);
}



  public Proc procEntryExit3(Assem.InstrList body) {
    int frameSize = maxArgs * wordSize - localCount;

    String pre =
        "\t.text\n" +
        name + ":\n" +
        name + "_framesize=" + frameSize;

    // Correct MIPS return sequence
    String post = "\tjr $ra\n\tnop";
    if(name.toString().equals("main")) {
      post = "\tli $v0, 10\n" + "\tsyscall\n";
    }
    

    if (frameSize != 0) {
        pre += "\n\tsubu $sp " + name + "_framesize";
        post = "\taddu $sp " + name + "_framesize\n" + post;
    }

    return new Proc(pre, body, post);
}



public Tree.Stm saveTemps(TempList t, AccessList a, Tree.Stm body) {
    if (t == null || a == null)
      return body;
    body = saveTemps(t.tail, a.tail, body);
    Tree.Stm move = new Tree.MOVE(a.head.exp(new Tree.TEMP(FP)),
				  new Tree.TEMP(t.head));
    if (body == null)
      return move;
    return new Tree.SEQ(move, body);
  }

  public Tree.Stm restoreTemps(TempList t, AccessList a, Tree.Stm body) {
    if (t == null || a == null)
      return body;
    body = restoreTemps(t.tail, a.tail, body);
    Tree.Stm move = new Tree.MOVE(new Tree.TEMP(t.head),
				  a.head.exp(new Tree.TEMP(FP)));
    if (body == null)
      return move;
    return new Tree.SEQ(body, move);
  }










  public Tree.Exp externalCall(String name, Tree.ExpList args) {
      return new Tree.CALL(
          new Tree.NAME(new Label(name)),
          args
      );
  }

  public Temp FP() {
      return FP; 
  }

  public Temp SP() {
      return SP;
  }

  public Temp RV() {
      return MipsRegs.V0;
  }


}