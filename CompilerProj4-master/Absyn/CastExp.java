package Absyn;
import Symbol.Symbol;

public class CastExp extends Exp {
    public TypeName type;
    public Exp exp;

    public CastExp(int p, TypeName t, Exp e) {
        pos = p;
        type = t;
        exp = e;

    }
}