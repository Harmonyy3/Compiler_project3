package Semant;

import Frame.Frame;

public class FunEntry extends Entry {
  public Types.FUNC formals;
  public Types.Type result;
  public Frame frame;

  FunEntry(Types.FUNC f, Types.Type r, Frame fr) {
    formals = f;
    result = r;
    frame = fr;
  }
}
