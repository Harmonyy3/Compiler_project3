package Absyn;

public class CallExp extends Exp {
    public Exp func;
    public ExpList args;

    public CallExp(int p, Exp f, ExpList a) {
        pos = p;
        func = f;
        args = a;
    }
}   