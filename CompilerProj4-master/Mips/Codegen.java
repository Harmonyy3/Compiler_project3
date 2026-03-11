package Mips;
import Temp.Temp;
import Temp.TempList;
import Temp.Label;
import Temp.LabelList;
import java.util.Hashtable;

public class Codegen {
  MipsFrame frame;
  public Codegen(MipsFrame f) {frame = f;}

  private Assem.InstrList ilist = null, last = null;

  private void emit(Assem.Instr inst) {
    if (last != null)
      last = last.tail = new Assem.InstrList(inst, null);
    else {
      if (ilist != null)
	throw new Error("Codegen.emit");
      last = ilist = new Assem.InstrList(inst, null);
    }
  }

  public Assem.InstrList codegen(Tree.Stm s) {
    munchStm(s);
    Assem.InstrList l = ilist;
    ilist = last = null;
    return l;
  }

  static Assem.Instr OPER(String a, TempList d, TempList s, LabelList j) {
    return new Assem.OPER("\t" + a, d, s, j);
  }
  static Assem.Instr OPER(String a, TempList d, TempList s) {
    return new Assem.OPER("\t" + a, d, s);
  }
  static Assem.Instr MOVE(String a, Temp d, Temp s) {
    return new Assem.MOVE("\t" + a, d, s);
  }

  static TempList L(Temp h) {
    return new TempList(h, null);
  }
  static TempList L(Temp h, TempList t) {
    return new TempList(h, t);
  }

  void munchStm(Tree.Stm s) {
    if (s instanceof Tree.MOVE) 
      munchStm((Tree.MOVE)s);
    else if (s instanceof Tree.UEXP)
      munchStm((Tree.UEXP)s);
    else if (s instanceof Tree.JUMP)
      munchStm((Tree.JUMP)s);
    else if (s instanceof Tree.CJUMP)
      munchStm((Tree.CJUMP)s);
    else if (s instanceof Tree.LABEL)
      munchStm((Tree.LABEL)s);
    else
      throw new Error("Codegen.munchStm");
  }

  private static Tree.CONST CONST16(Tree.Exp e) {
    if (e instanceof Tree.CONST) {
      Tree.CONST c = (Tree.CONST)e;
      int value = c.value;
      if (value == (short)value)
	return c;
    }
    return null;
  }

  private static boolean commute(Tree.BINOP e) {
    Tree.CONST left = CONST16(e.left);
    Tree.CONST right = CONST16(e.right);
    if (left == null)
      return right != null;
    if (right == null) {
      e.left = e.right;
      e.right = left;
    }
    return true;
  }

  void munchStm(Tree.MOVE s) {
    // MOVE(MEM, Exp)
    if (s.dst instanceof Tree.MEM) {
      Tree.MEM mem = (Tree.MEM)s.dst;

      // MOVE(MEM(+ Exp CONST), Exp)
      if (mem.exp instanceof Tree.BINOP) {
	Tree.BINOP b = (Tree.BINOP)mem.exp;
	if (b.binop == Tree.BINOP.PLUS && commute(b)) {
	  int right = ((Tree.CONST)b.right).value;
	  Temp left = (b.left instanceof Tree.TEMP)
	    ?((Tree.TEMP)b.left).temp
	    :munchExp(b.left);
	  String off = Integer.toString(right);
	  if (left == frame.FP()) {
	    left = frame.SP();
	    off = Integer.toString(frame.framesizeOffset(right)); // right = base offset
        left = frame.SP();

	  }
	  emit(OPER("sw `s0 " + off + "(`s1)",
		    null, L(munchExp(s.src), L(left))));
	  return;
	}
      }

      // MOVE(MEM(CONST), Exp)
      Tree.CONST exp = CONST16(mem.exp);
      if (exp != null) {
	emit(OPER("sw `s0 " + exp.value + "(`s1)",
		  null, L(munchExp(s.src), L(frame.ZERO))));
	return;
      }

      // MOVE(MEM(TEMP), Exp)
      if (mem.exp instanceof Tree.TEMP) {
	Temp temp = ((Tree.TEMP)mem.exp).temp;
	if (temp == frame.FP) {
	  emit(OPER("sw `s0 " + frame.name + "_framesize" + "(`s1)",
		    null, L(munchExp(s.src), L(frame.SP()))));
	  return;
	}
      }

      // MOVE(MEM(Exp), Exp)
      emit(OPER("sw `s0 (`s1)",
		null, L(munchExp(s.src), L(munchExp(mem.exp)))));
      return;
    }

    // From here on dst must be a TEMP
    Temp dst = ((Tree.TEMP)s.dst).temp;

    // MOVE(TEMP, MEM)
    if (s.src instanceof Tree.MEM) {
      Tree.MEM mem = (Tree.MEM)s.src;

      // MOVE(TEMP, MEM(+ Exp CONST))
      if (mem.exp instanceof Tree.BINOP) {
	Tree.BINOP b = (Tree.BINOP)mem.exp;
	if (b.binop == Tree.BINOP.PLUS && commute(b)) {
	  int right = ((Tree.CONST)b.right).value;
	  Temp left = (b.left instanceof Tree.TEMP)
	    ?((Tree.TEMP)b.left).temp
	    :munchExp(b.left);
	  String off = Integer.toString(right);
	  if (left == frame.FP()) {
	    left = frame.SP();
	    off = Integer.toString(frame.framesizeOffset(right)); // right = base offset
left = frame.SP();

	  }
	  emit(OPER("lw `d0 " + off + "(`s0)", L(dst), L(left)));
	  return;
	}
      }

      // MOVE(TEMP, MEM(CONST))
      Tree.CONST exp = CONST16(mem.exp);
      if (exp != null) {
	emit(OPER("lw `d0 " + exp.value + "(`s0)", L(dst), L(frame.ZERO)));
	return;
      }

      // MOVE(TEMP, MEM(TEMP))
      if (mem.exp instanceof Tree.TEMP) {
	Temp temp = ((Tree.TEMP)mem.exp).temp;
	if (temp == frame.FP()) {
	  emit(OPER("lw `d0 " + frame.name + "_framesize" + "(`s0)",
		    L(dst), L(frame.SP())));
	  return;
	}
      }

      // MOVE(TEMP, MEM(Exp))
      emit(OPER("lw `d0 (`s0)", L(dst), L(munchExp(mem.exp))));
      return;
    }

    // MOVE(TEMP, Exp)
    Temp src = munchExp(s.src);
    if (dst != src) {
      emit(MOVE("move `d0 `s0", dst, src));
      return;
    }
    
  }

  void munchStm(Tree.UEXP s) {
    munchExp(s.exp);
  }

  void munchStm(Tree.JUMP s) {
    if (s.exp instanceof Tree.NAME) {
      Tree.NAME name = (Tree.NAME)s.exp;
      // JUMP(Tree.NAME, LabelList)
      emit(OPER("b " + name.label.toString(), null, null, s.targets));
      return;
    }
    // JUMP(Exp, LabelList)
    emit(OPER("jr `s0", null, L(munchExp(s.exp)), s.targets));
    return;
  }

  private static boolean commute(Tree.CJUMP s) {
    Tree.CONST left = CONST16(s.left);
    Tree.CONST right = CONST16(s.right);
    if (left == null)
      return right != null;
    if (right == null) {
      s.left = s.right;
      s.right = left;
      switch (s.relop) {
      case Tree.CJUMP.EQ:
      case Tree.CJUMP.NE:
	break;
      case Tree.CJUMP.LT:
	s.relop = Tree.CJUMP.GT;
	break;
      case Tree.CJUMP.GE:
	s.relop = Tree.CJUMP.LE;
	break;
      case Tree.CJUMP.GT:
	s.relop = Tree.CJUMP.LT;
	break;
      case Tree.CJUMP.LE:
	s.relop = Tree.CJUMP.GE;
	break;
      case Tree.CJUMP.ULT:
	s.relop = Tree.CJUMP.UGT;
	break;
      case Tree.CJUMP.UGE:
	s.relop = Tree.CJUMP.ULE;
	break;
      case Tree.CJUMP.UGT:
	s.relop = Tree.CJUMP.ULT;
	break;
      case Tree.CJUMP.ULE:
	s.relop = Tree.CJUMP.UGE;
	break;
      default:
	throw new Error("bad relop in Codegen.commute");
      }
    }
    return true;
  }

  private static String[] CJUMP = new String[10];
  static {
    CJUMP[Tree.CJUMP.EQ ] = "beq";
    CJUMP[Tree.CJUMP.NE ] = "bne";
    CJUMP[Tree.CJUMP.LT ] = "blt";
    CJUMP[Tree.CJUMP.GT ] = "bgt";
    CJUMP[Tree.CJUMP.LE ] = "ble";
    CJUMP[Tree.CJUMP.GE ] = "bge";
    CJUMP[Tree.CJUMP.ULT] = "bltu";
    CJUMP[Tree.CJUMP.ULE] = "bleu";
    CJUMP[Tree.CJUMP.UGT] = "bgtu";
    CJUMP[Tree.CJUMP.UGE] = "bgeu";
  }

  void munchStm(Tree.CJUMP s) {
    if (commute(s)) {
      int right = ((Tree.CONST)s.right).value;
      // CJUMP(op, Exp, CONST, Label, Label)
      emit(OPER(CJUMP[s.relop] + " `s0 " + right + " " + s.iftrue.toString(),
		null, L(munchExp(s.left)),
		new LabelList(s.iftrue, new LabelList(s.iffalse, null))));
      return;
    }

    // CJUMP(op, Exp, Exp, Label, Label)
    emit(OPER(CJUMP[s.relop] + " `s0 `s1 " + s.iftrue.toString(),
	      null, L(munchExp(s.left), L(munchExp(s.right))),
	      new LabelList(s.iftrue, new LabelList(s.iffalse, null))));
    return;
  }

  void munchStm(Tree.LABEL l) {
    emit(new Assem.LABEL(l.label.toString() + ":", l.label));
    return;
  }

  Temp munchExp(Tree.Exp s) {
    if (s instanceof Tree.CONST)
      return munchExp((Tree.CONST)s);
    else if (s instanceof Tree.NAME)
      return munchExp((Tree.NAME)s);
    else if (s instanceof Tree.TEMP)
      return munchExp((Tree.TEMP)s);
    else if (s instanceof Tree.BINOP)
      return munchExp((Tree.BINOP)s);
    else if (s instanceof Tree.MEM)
      return munchExp((Tree.MEM)s);
    else if (s instanceof Tree.CALL)
      return munchExp((Tree.CALL)s);
    else
      throw new Error("Codegen.munchExp");
  }

  Temp munchExp(Tree.CONST e) {
    if (e.value == 0)
      return frame.ZERO;
    Temp t = new Temp();
    emit(OPER("li `d0 " + e.value, L(t), null));
    return t;
  }

  Temp munchExp(Tree.NAME e) {
    Temp t = new Temp();
    emit(OPER("la `d0 " + e.label.toString(), L(t), null));
    return t;
  }

  Temp munchExp(Tree.TEMP e) {
    if (e.temp == frame.FP()) {
      Temp t = new Temp();
      emit(OPER("addu `d0 `s0 " + frame.framesizeOffset(0),
		L(t), L(frame.SP())));
      return t;
    }
    return e.temp;
  }

  private static String[] BINOP = new String[10];
  static {
    BINOP[Tree.BINOP.PLUS   ] = "add";
    BINOP[Tree.BINOP.MINUS  ] = "sub";
    BINOP[Tree.BINOP.MUL    ] = "mulo";
    BINOP[Tree.BINOP.DIV    ] = "div";
    BINOP[Tree.BINOP.AND    ] = "and";
    BINOP[Tree.BINOP.OR     ] = "or";
    BINOP[Tree.BINOP.LSHIFT ] = "sll";
    BINOP[Tree.BINOP.RSHIFT ] = "srl";
    BINOP[Tree.BINOP.ARSHIFT] = "sra";
    BINOP[Tree.BINOP.XOR    ] = "xor";
  }

  private static int shift(int i) {
    int shift = 0;
    if ((i >= 2) && ((i & (i - 1)) == 0)) {
      while (i > 1) {
	shift += 1;
	i >>= 1;
      }
    }
    return shift;
  }

  Temp munchExp(Tree.BINOP e) {
    Temp t = new Temp();
    
    switch (e.binop) {
    case Tree.BINOP.PLUS:
      if (commute(e)) {
	int right = ((Tree.CONST)e.right).value;
	Temp left = (e.left instanceof Tree.TEMP)
	  ?((Tree.TEMP)e.left).temp
	  :munchExp(e.left);
	String off = Integer.toString(right);
	if (left == frame.FP()) {
	  left = frame.SP();
	  off = Integer.toString(frame.framesizeOffset(right)); // right = base offset
left = frame.SP();

	}
	emit(OPER("add `d0 `s0 " + off, L(t), L(left)));
	return t;
      }
      break;
    case Tree.BINOP.MUL:
      if (e.right instanceof Tree.CONST) {
	int shift = shift(((Tree.CONST)e.right).value);
	if (shift != 0) {
	  emit(OPER("sll `d0 `s0 " + shift, L(t), L(munchExp(e.left))));
	  return t;
	}
      }
      if (e.left instanceof Tree.CONST) {
	int shift = shift(((Tree.CONST)e.left).value);
	if (shift != 0) {
	  emit(OPER("sll `d0 `s0 " + shift, L(t), L(munchExp(e.right))));
	  return t;
	}
      }
      // fall through
    case Tree.BINOP.AND:
    case Tree.BINOP.OR:
    case Tree.BINOP.XOR:
      if (commute(e)) {
	emit(OPER(BINOP[e.binop] + " `d0 `s0 " + ((Tree.CONST)e.right).value,
		  L(t), L(munchExp(e.left))));
	return t;
      }
      break;
    case Tree.BINOP.DIV:
      if (e.right instanceof Tree.CONST) {
	int shift = shift(((Tree.CONST)e.right).value);
	if (shift != 0) {
	  emit(OPER("sra `d0 `s0 " + shift, L(t), L(munchExp(e.left))));
	  return t;
	}
      }
      // fall through
    default:
      Tree.CONST right = CONST16(e.right);
      if (right != null) {
	emit(OPER(BINOP[e.binop] + " `d0 `s0 " + right.value,
		  L(t), L(munchExp(e.left))));
	return t;
      }
      break;
    }

    emit(OPER(BINOP[e.binop] + " `d0 `s0 `s1",
	      L(t), L(munchExp(e.left), L(munchExp(e.right)))));
    return t;
  }

  Temp munchExp(Tree.MEM e) {
    Temp t = new Temp();

    // MEM(+ Exp CONST)
    if (e.exp instanceof Tree.BINOP) {
      Tree.BINOP b = (Tree.BINOP)e.exp;
      if (b.binop == Tree.BINOP.PLUS && commute(b)) {
	int right = ((Tree.CONST)b.right).value;
	Temp left = (b.left instanceof Tree.TEMP)
	  ?((Tree.TEMP)b.left).temp
	  :munchExp(b.left);
	String off = Integer.toString(right);
	if (left == frame.FP()) {
	  left = frame.SP();
	  off = Integer.toString(frame.framesizeOffset(right)); // right = base offset
left = frame.SP();

	}
	emit(OPER("lw `d0 " + off + "(`s0)", L(t), L(left)));
	return t;
      }
    }

    // MEM(CONST)
    Tree.CONST exp = CONST16(e.exp);
    if (exp != null) {
      emit(OPER("lw `d0 " + exp.value + "(`s0)", L(t), L(frame.ZERO)));
      return t;
    }

    // MEM(TEMP)
    if (e.exp instanceof Tree.TEMP) {
      Temp temp = ((Tree.TEMP)e.exp).temp;
      if (temp == frame.FP()) {
	emit(OPER("lw `d0 " + frame.name + "_framesize" + "(`s0)",
		  L(t), L(frame.SP())));
	return t;
      }
    }

    // MEM(Exp)
    emit(OPER("lw `d0 (`s0)", L(t), L(munchExp(e.exp))));
    return t;
  }

  Temp munchExp(Tree.CALL s) {
    if (s.func instanceof Tree.NAME) {
      emit(OPER("jal " + ((Tree.NAME)s.func).label.toString(),
		frame.calldefs, munchArgs(0, s.args)));
      return frame.V0;
    }
    emit(OPER("jal `d0 `s0",
	      frame.calldefs, L(munchExp(s.func), munchArgs(0, s.args))));
    return frame.V0;
  }

  private TempList munchArgs(int i, Tree.ExpList args) {
    if (args == null)
      return null;
    Temp src = munchExp(args.head);
    if (i > frame.maxArgs)
      frame.maxArgs = i;
    switch (i) {
    case 0:
      emit(MOVE("move `d0 `s0", frame.A0, src));
      break;
    case 1:
      emit(MOVE("move `d0 `s0", frame.A1, src));
      break;
    case 2:
      emit(MOVE("move `d0 `s0", frame.A2, src));
      break;
    case 3:
      emit(MOVE("move `d0 `s0", frame.A3, src));
      break;
    default:
      emit(OPER("sw `s0 " + (i-1)*frame.wordSize() + "(`s1)",
		null, L(src, L(frame.SP()))));
      break;
    }
    return L(src, munchArgs(i+1, args.tail));
  }
}
