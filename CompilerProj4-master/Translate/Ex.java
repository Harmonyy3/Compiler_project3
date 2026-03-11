package Translate;

import Tree.*;
import Temp.Label;

/**
 * Ex: Expression wrapper for ANSIC89 IR.
 * Wraps a Tree.Exp (IR expression) and provides unEx, unNx, unCx methods.
 */
public class Ex extends Exp {
    public Tree.Exp exp;  // The IR expression

    // Constructor
    public Ex(Tree.Exp e) {
        super(null);
        this.exp = e;
    }

    /** Return this expression as an IR expression */
    @Override
    public Tree.Exp unEx() {
        return exp;
    }

    /** Return this expression as a statement (evaluate and discard) */
    @Override
    public Tree.Stm unNx() {
        return new UEXP(exp); // evaluate expression, no result
    }

    /**
     * Convert expression to conditional jump.
     * For ANSIC89, treat 0 as false, nonzero as true.
     */
    @Override
    public Tree.Stm unCx(Label t, Label f) {
        return new CJUMP(CJUMP.NE, exp, new CONST(0), t, f);
    }

    @Override
    public String toString() {
        return "Ex(exp=" + exp + ")";
    }
}
