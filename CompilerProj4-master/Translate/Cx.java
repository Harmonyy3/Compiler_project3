package Translate;

import Types.Type;
import Temp.Temp;
import Temp.Label;
import Tree.*;

/**
 * Cx: Conditional expression for ANSIC89 IR.
 * Represents expressions that produce a boolean outcome.
 */
abstract class Cx extends Exp {

    public Cx() {
        super(Type.INT); // Boolean expressions are integers (0 = false, nonzero = true)
    }

    /** Convert conditional to an expression (0 or 1) */
    @Override
    public Tree.Exp unEx() {
        Temp r = new Temp();
        Label t = new Label();
        Label f = new Label();

        return new ESEQ(
                new SEQ(
                    new MOVE(new TEMP(r), new CONST(1)),   // assume true
                    new SEQ(
                        unCx(t, f),                    // jump based on actual condition
                        new SEQ(
                            new LABEL(f),              // false branch
                            new SEQ(
                                new MOVE(new TEMP(r), new CONST(0)), // set r = 0
                                new LABEL(t)             // true label
                            )
                        )
                    )
                ),
                new TEMP(r)
            );
    }

    /** Convert conditional to a statement in a statement context */
    @Override
    public Tree.Stm unNx() {
        Label join = new Label();
        return new SEQ(unCx(join, join), new LABEL(join));
    }

    /** Subclasses must implement conditional jump */
    public abstract Tree.Stm unCx(Label t, Label f);
}
