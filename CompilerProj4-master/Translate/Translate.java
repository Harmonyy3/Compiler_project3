package Translate;
import Mips.MipsFrame;
import Symbol.Symbol;
import Tree.BINOP;
import Tree.CALL;
import Tree.CJUMP;
import Tree.CONST;
import Tree.ESEQ;
import Tree.JUMP;
import Tree.MEM;
import Tree.SEQ;
import Tree.TEMP;
import Tree.UEXP;
import Temp.Temp;
import Temp.Label;

public class Translate {
  public Frame.Frame frame;
  public Translate(Frame.Frame f) {
    frame = f;
  }
  private Frag frags;
  public void procEntryExit(Frame.Frame frame, Tree.Stm body) {

    // Ensure function ends with a jump to return label
    Label ret = new Label(frame.name.toString() + "_ret");
    

    // BODY
    Tree.Stm wrapped =
        new SEQ(
            body,
            LABEL(ret)     // return label
        );

    // Create the ProcFrag and add to the global frag list
    ProcFrag frag = new ProcFrag(wrapped, frame);
    frag.next = frags;
    frags = frag;
}

  public Frag getResult() {
    return frags;
  }

  private static Tree.Exp CONST(int value) {
    return new Tree.CONST(value);
  }
  private static Tree.Exp NAME(Label label) {
    return new Tree.NAME(label);
  }
  private static Tree.Exp TEMP(Temp temp) {
    return new Tree.TEMP(temp);
  }
  private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) {
    return new Tree.BINOP(binop, left, right);
  }
  private static Tree.Exp MEM(Tree.Exp exp) {
    return new Tree.MEM(exp);
  }
  private static Tree.Exp CALL(Tree.Exp func, Tree.ExpList args) {
    return new Tree.CALL(func, args);
  }
  private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp) {
    if (stm == null)
      return exp;
    return new Tree.ESEQ(stm, exp);
  }

  private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src) {
    return new Tree.MOVE(dst, src);
  }
  private static Tree.Stm UEXP(Tree.Exp exp) {
    return new Tree.UEXP(exp);
  }
  private static Tree.Stm JUMP(Label target) {
    return new Tree.JUMP(target);
  }
  private static
  Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r, Label t, Label f) {
    return new Tree.CJUMP(relop, l, r, t, f);
  }
  private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) {
    if (left == null)
      return right;
    if (right == null)
      return left;
    return new Tree.SEQ(left, right);
  }
  private static Tree.Stm LABEL(Label label) {
    return new Tree.LABEL(label);
  }

  private static Tree.ExpList ExpList(Tree.Exp head, Tree.ExpList tail) {
    return new Tree.ExpList(head, tail);
  }
  private static Tree.ExpList ExpList(Tree.Exp head) {
    return ExpList(head, null);
  }
  private static Tree.ExpList ExpList(ExpList exp) {
    if (exp == null)
      return null;
    return ExpList(exp.head.unEx(), ExpList(exp.tail));
  }

  public Exp Error() {
    return new Ex(CONST(0));
  }

  public Exp SimpleVar(Frame.Access access) {
      // START from the current frame pointer
      Tree.Exp fp = new Tree.TEMP(access.home.FP());

      // ASK the Access object how to compute its memory location
      return new Ex(access.exp(fp));
  }

  public Exp InitExp(ExpList init, int size, boolean isLocal) {
    if (init == null) {
        // return NULL pointer
        return new Ex(new CONST(0));
    }

    int count = 0;
    for (ExpList p = init; p != null; p = p.tail) count++;

    if (isLocal) {
        // --- Local stack allocation ---
        // Reserve space on stack: count * size
        TEMP base = new TEMP(new Temp()); // pseudo-register representing stack pointer offset
        Tree.Stm seq = new Tree.MOVE(
            base,
            new BINOP(BINOP.MINUS, new TEMP(MipsFrame.FP), new CONST(count * size))
        );

        // Fill array elements on stack
        int offset = 0;
        for (ExpList p = init; p != null; p = p.tail) {
            Exp element = p.head;

            // MEM(base + offset) = element
            MEM lhs = new MEM(new BINOP(BINOP.PLUS, base, new CONST(offset)));
            seq = new SEQ(seq, new Tree.MOVE(lhs, element.unEx()));

            offset += size;
        }

        // Return base pointer (stack address)
        return new Ex(new ESEQ(seq, base));

    } else {
        // --- Heap allocation (dynamic array) ---
        Exp mallocCall = new Ex(
            new CALL(
                new Ex(new Tree.NAME(new Label("malloc"))).unEx(),
                new Tree.ExpList(
                    new BINOP(BINOP.MUL, new CONST(count), new CONST(size)),
                    null
                )
            )
        );

        TEMP base = new TEMP(new Temp());
        Tree.Stm seq = new Tree.MOVE(base, mallocCall.unEx());

        int offset = 0;
        for (ExpList p = init; p != null; p = p.tail) {
            Exp element = p.head;
            MEM lhs = new MEM(new BINOP(BINOP.PLUS, base, new CONST(offset)));
            seq = new SEQ(seq, new Tree.MOVE(lhs, element.unEx()));
            offset += size;
        }

        return new Ex(new ESEQ(seq, base));
    }
}



  



  // public Exp FieldVar(Exp record, int index) {
  //   Label bad = frame.badPtr();
  //   Label ok = new Label();
  //   Temp r = new Temp();
  //   index *= frame.wordSize();
  //   return new Ex
  //     (ESEQ(SEQ(MOVE(TEMP(r), record.unEx()),
	// 	SEQ(CJUMP(CJUMP.EQ, TEMP(r), CONST(0), bad, ok),
	// 	    LABEL(ok))),
	//     MEM(BINOP(BINOP.PLUS, TEMP(r), CONST(index)))));
  // }

  // public Exp SubscriptVar(Exp array, Exp index) {
  //   Label bad = frame.badSub();
  //   Label check = new Label();
  //   Label ok = new Label();
  //   Temp a = new Temp();
  //   Temp i = new Temp();
  //   int size = frame.wordSize();
  //   return new Ex
  //     (ESEQ
  //      (SEQ
	// (MOVE(TEMP(a), array.unEx()),
	//  SEQ(MOVE(TEMP(i), index.unEx()),
	//      SEQ(CJUMP(CJUMP.LT, TEMP(i), CONST(0), bad, check),
	// 	 SEQ(LABEL(check),
	// 	     SEQ(CJUMP(CJUMP.GT, TEMP(i),
	// 		       MEM(BINOP(BINOP.PLUS, TEMP(a), CONST(-size))),
	// 		       bad, ok),
	// 		 LABEL(ok)))))),
	// MEM(BINOP(BINOP.PLUS, TEMP(a),
	// 	  BINOP(BINOP.MUL, TEMP(i), CONST(size))))));
  // }


  public Exp IntExp(int value) {
    return new Ex(new Tree.CONST(value));
}

public Exp CharExp(char value) {
    return new Ex(new Tree.CONST(value));
}


  private java.util.Hashtable strings = new java.util.Hashtable();
  public Exp StringExp(String lit) {
    String u = lit.intern();
    Label lab = (Label)strings.get(u);
    if (lab == null) {
      lab = new Label();
      strings.put(u, lab);
      DataFrag frag = new DataFrag(frame.string(lab, u));
      frag.next = frags;
      frags = frag;
    }
    return new Ex(NAME(lab));
  }

  private Tree.Exp CallExp(Symbol f, ExpList args) {
    return frame.externalCall(f.toString(), ExpList(args));
  }

  public Exp FunExp(Symbol f, ExpList args) {
    return new Ex(CallExp(f, args));
  }

  // Replace your existing FunctionBodyCmpdStm with this:
public Exp FunctionBodyCmpdStm(ExpList decs, ExpList stms) {
    Tree.Stm block = null;

    // Add declarations (these are Nx)
    for (ExpList p = decs; p != null; p = p.tail) {
        Tree.Stm s = p.head.unNx();
        block = (block == null) ? s : new SEQ(block, s);
    }

    // Add statements and remember the real last expression (if any)
    Exp lastExp = null;
    for (ExpList p = stms; p != null; p = p.tail) {
        Exp e = p.head;
        Tree.Stm s;
        try {
            // If it's a statement (Nx), unNx will succeed
            s = e.unNx();
            // If it is an expression used as a statement, we still consider it
            // as candidate for lastExp (the last expression should be returned)
            lastExp = e;
        } catch (Throwable ex) {
            // If unNx fails, it must be an expression; wrap as UEXP for side effects
            s = new UEXP(e.unEx());
            // this is an expression; keep as candidate for lastExp
            lastExp = e;
        }
        block = (block == null) ? s : new SEQ(block, s);
    }

    // No statements or declarations at all
    if (block == null) {
        // No work to do; return 0 as expression result (or Nx(null) if you prefer)
        return new Nx(null);
    }

    // If there is no last expression, just return the block as Nx
    if (lastExp == null) {
        return new Nx(block);
    }

    // There *is* a last expression: append MOVE(TEMP(V0), lastExp) to the block
    Tree.Stm setRv;
    try {
        // get the expression value
        Tree.Exp rvExp = lastExp.unEx();
        setRv = new Tree.MOVE(new Tree.TEMP(MipsFrame.V0), rvExp);
    } catch (Throwable t) {
        // If lastExp.unEx() fails for some reason, fallback to moving 0
        setRv = new Tree.MOVE(new Tree.TEMP(MipsFrame.V0), new Tree.CONST(0));
    }

    Tree.Stm full = new SEQ(block, setRv);

    // return as Nx (statement). FunctionDec will see it's an Nx and will not
    // add another MOVE to V0; but V0 is already set here.
    return new Nx(full);
}




  public Exp ProcExp(Symbol f, ExpList args) {
    return new Nx(UEXP(CallExp(f, args)));
  }


  public Exp OpExp(int op, Exp left, Exp right) {
    switch(op) {
    case Absyn.BinOpExp.PLUS:
      return new Ex(BINOP(BINOP.PLUS,  left.unEx(), right.unEx()));
    case Absyn.BinOpExp.MINUS:
      return new Ex(BINOP(BINOP.MINUS, left.unEx(), right.unEx()));
    case Absyn.BinOpExp.TIMES:
      return new Ex(BINOP(BINOP.MUL,   left.unEx(), right.unEx()));
    case Absyn.BinOpExp.DIVIDE:
      return new Ex(BINOP(BINOP.DIV,   left.unEx(), right.unEx()));
    case Absyn.BinOpExp.LT:
      return new RelCx(CJUMP.LT, left.unEx(), right.unEx());
    case Absyn.BinOpExp.LEQ:
      return new RelCx(CJUMP.LE, left.unEx(), right.unEx());
    case Absyn.BinOpExp.GT:
      return new RelCx(CJUMP.GT, left.unEx(), right.unEx());
    case Absyn.BinOpExp.GEQ:
      return new RelCx(CJUMP.GE, left.unEx(), right.unEx());
    case Absyn.BinOpExp.EQ:
      return new RelCx(CJUMP.EQ, left.unEx(), right.unEx());
    case Absyn.BinOpExp.NEQ:
      return new RelCx(CJUMP.NE, left.unEx(), right.unEx());
    case Absyn.BinOpExp.AND:
      return new Ex(BINOP(BINOP.AND, left.unEx(), right.unEx()));
    case Absyn.BinOpExp.OR:
      return new Ex(BINOP(BINOP.OR, left.unEx(), right.unEx()));
    case Absyn.BinOpExp.MODULUS:
      return new Ex(BINOP(BINOP.DIV,
          BINOP(BINOP.MINUS, left.unEx(),
                BINOP(BINOP.MUL,
                      right.unEx(),
                      BINOP(BINOP.DIV, left.unEx(), right.unEx()))),
          right.unEx()
      ));
    case Absyn.BinOpExp.LSHIFT:
      return new Ex(BINOP(BINOP.LSHIFT, left.unEx(), right.unEx()));
    case Absyn.BinOpExp.RSHIFT:
      return new Ex(BINOP(BINOP.RSHIFT, left.unEx(), right.unEx()));
    default:
      throw new Error("Translate.OpExp");
    }
  }

  // public Exp StrOpExp(int op, Exp left, Exp right) {
  //   Tree.Exp cmp = frame.externalCall("strcmp",
	// 			      ExpList(left.unEx(),
	// 				      ExpList(right.unEx())));
  //   switch(op) {
  //   case Absyn.OpExp.GT:
  //     return new RelCx(CJUMP.GT, cmp, CONST(0));
  //   case Absyn.OpExp.LT:
  //     return new RelCx(CJUMP.LT, cmp, CONST(0));
  //   case Absyn.OpExp.GE:
  //     return new RelCx(CJUMP.GE, cmp, CONST(0));
  //   case Absyn.OpExp.LE:
  //     return new RelCx(CJUMP.LE, cmp, CONST(0));
  //   case Absyn.OpExp.EQ:
  //     return new RelCx(CJUMP.EQ, cmp, CONST(0));
  //   case Absyn.OpExp.NE:
  //     return new RelCx(CJUMP.NE, cmp, CONST(0));
  //   default:
  //     throw new Error("Translate.StrOpExp");
  //   }
  // }

  // private Tree.Stm initRecord(Temp r, int i, ExpList init, int wordSize) {
  //   if (init == null)
  //     return null;
  //   return
  //     SEQ(MOVE(MEM(BINOP(BINOP.PLUS, TEMP(r), CONST(i))), init.head.unEx()),
	//   initRecord(r, i + wordSize, init.tail, wordSize));
  // }

  public Exp SeqExp(ExpList list) {
    Tree.Stm stm = null;
    for(; list.tail != null; list = list.tail)
        stm = SEQ(stm, list.head.unNx());
    return new Ex(new Tree.ESEQ(stm, list.head.unEx()));
}


  public Exp AssignExp(Exp lhs, Exp rhs) {
    //System.out.println("Temp lhs = " + lhs.unEx().)
    return new Nx(new Tree.MOVE(lhs.unEx(), rhs.unEx()));
}


  public Exp IfExp(Exp cc, Exp aa, Exp bb) {
    return new IfThenElseExp(cc, aa, bb);
  }

  public Exp WhileExp(Exp test, Exp body, Label done) {
    Label c = new Label();
    Label b = new Label();
    return new Nx(SEQ(SEQ(SEQ(LABEL(c), test.unCx(b, done)),
			  SEQ(SEQ(LABEL(b), body.unNx()), JUMP(c))),
		      LABEL(done)));
  }

  public Exp DoWhileExp(Exp body, Exp test, Label done) {
    Label b = new Label();
    Label c = new Label();

    return new Nx(
        SEQ(
            SEQ(
                LABEL(b),
                body.unNx()
            ),
            SEQ(
                LABEL(c),
                SEQ(
                    test.unCx(b, done),
                    LABEL(done)
                )
            )
        )
    );
}

  public Exp GoTo(Label target) {
      return new Nx(new JUMP(target));
  }

  public Exp Label(Label label) {
    return new Nx(new Tree.LABEL(label));
  }



  // public Exp ForExp(Access i, Exp lo, Exp hi, Exp body, Label done) { ====> for loops are different in c need to redo
  //   Label b = new Label();
  //   Label inc = new Label();
  //   Temp limit = new Temp();
  //   Temp home = i.home.frame.FP();
  //   return new Nx
  //     (SEQ
  //      (SEQ
	// (SEQ(SEQ(MOVE(i.acc.exp(TEMP(home)), lo.unEx()),
	// 	 MOVE(TEMP(limit), hi.unEx())),
	//      CJUMP(CJUMP.LE, i.acc.exp(TEMP(home)), TEMP(limit), b, done)),
	//  SEQ(SEQ
	//      (SEQ(LABEL(b), body.unNx()),
	//       CJUMP(CJUMP.LT, i.acc.exp(TEMP(home)), TEMP(limit), inc, done)),
	//      SEQ(SEQ(LABEL(inc),
	// 	     MOVE(i.acc.exp(TEMP(home)),
	// 		  BINOP(BINOP.PLUS, i.acc.exp(TEMP(home)), CONST(1)))),
	// 	 JUMP(b)))),
	// LABEL(done)));
  // }

  public Exp BreakExp(Label done) {
    return new Nx(JUMP(done));
  }

  


  public Exp VarDec(Frame.Access a, Exp init) {

    // Compute address of the variable
    Tree.Exp dst = a.exp(new TEMP(frame.FP())); 

    // If no initializer: default initialize to 0
    Tree.Exp rhs;
    if (init == null) {
        rhs = new CONST(0);
    } else {
        rhs = init.unEx();
    }

    return new Nx(MOVE(dst, rhs));
}



  public Exp TypeDec() {
    return new Nx(null);
  }

  public Exp FunctionDec(Label name, Exp body) {
    Tree.Stm bodyStm;

    try {
        // If body is an expression, treat it as: $v0 = expr
        Tree.Exp value = body.unEx();
        bodyStm = new Tree.MOVE(new TEMP(MipsFrame.V0), value);
    } catch (Throwable t) {
        // Otherwise body is already a statement
        bodyStm = body.unNx();
    }

    // DO NOT PUT new LABEL(name) HERE!
    // FRAME.procEntryExit will insert the correct entry label.

    return new Nx(bodyStm);
}




}
