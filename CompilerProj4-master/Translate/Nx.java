package Translate;

import Tree.*;
import Temp.Label;

/**
 * Nx: Statement wrapper for ANSIC89 IR.
 * Wraps a Tree.Stm (IR statement) and provides unEx, unNx, unCx.
 */
public class Nx extends Exp {
    public Tree.Stm stm;  // The IR statement

    // Constructor
    public Nx(Tree.Stm s) {
        super(null);  
        this.stm = s;
    }

    /** Convert to an expression (ESEQ) that evaluates statement then returns 0 */
    @Override
    public Tree.Exp unEx() {
        // In C89, statements have no value, so wrap as ESEQ with 0
        return new ESEQ(stm, new CONST(0));
    }

    /** Return the statement directly */
    @Override
    public Tree.Stm unNx() {
        return stm;
    }

    /** Convert statement to conditional jump.
     * Useful if used as the condition of an if/while (non-zero means true)
     */
    @Override
    public Tree.Stm unCx(Label t, Label f) {
        // Execute the statement, then jump to true label if nonzero
        return new SEQ(stm, new CJUMP(CJUMP.NE, new CONST(1), new CONST(0), t, f));
    }

    @Override
    public String toString() {
        return "Nx(stm=" + stm + ")";
    }
}
