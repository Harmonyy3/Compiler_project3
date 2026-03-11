package Translate;

import Temp.Label;
import Tree.Stm;

public abstract class Exp {
    public Types.Type type; // semantic type

    public Exp(Types.Type t) {
        this.type = t;
    }

    // Subclasses must implement these
    public abstract Tree.Exp unEx();              // convert to IR expression
    public abstract Stm unNx();                  // convert to IR statement
    public abstract Stm unCx(Label t, Label f); // convert to conditional jump
}
