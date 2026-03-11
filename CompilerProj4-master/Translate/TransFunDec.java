package Translate;

import Frame.Frame;
//import Translate.Exp;   // import Exp from IR tree

/**
 * Represents an entire translated function:
 *  - The frame describing formal/local allocation
 *  - The IR expression for the function body
 */
public class TransFunDec {

    public final Frame frame;
    public final Exp body;   // change type to Exp

    public TransFunDec(Frame frame, Exp body) {
        this.frame = frame;
        this.body = body;
    }
}
