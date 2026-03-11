package Absyn;

/** sizeof expression applied to an expression. */
public class SizeofExp extends Exp {
    public Exp expr;           // the expression inside sizeof

    public SizeofExp(Exp expr) { 
        super(); 
        this.expr = expr; 
    }

    public SizeofExp(int pos, Exp expr) { 
        super(pos); 
        this.expr = expr; 
    }
}
