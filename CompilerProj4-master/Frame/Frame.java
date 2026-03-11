package Frame;

import Temp.TempList;
import Temp.TempMap;

public abstract class Frame implements TempMap{
  public Temp.Label name;
  public AccessList formals;
  public AccessList locals;
  abstract public Frame newFrame(Symbol.Symbol name, Util.BoolList formals);
  abstract public Access allocLocal(boolean escape);
  abstract public Proc procEntryExit3(Assem.InstrList body);
  abstract public Assem.InstrList procEntryExit2(Assem.InstrList body);
  abstract public Tree.Exp externalCall(String name, Tree.ExpList args);
  abstract public String tempMap(Temp.Temp t);
  abstract public Tree.Stm saveTemps(TempList t, AccessList a, Tree.Stm body);
  abstract public Tree.Stm restoreTemps(TempList t, AccessList a, Tree.Stm body);
  public abstract TempList registers();

  abstract public Assem.InstrList append(Assem.InstrList a, Assem.InstrList b);

  abstract public String string(Temp.Label label, String value);
  abstract public Assem.InstrList codegen(Tree.Stm stm);

  public abstract Temp.Temp FP();   // frame pointer
  public abstract Temp.Temp SP();   // stack pointer
  public abstract Temp.Temp RV();   // return value register


  

}
