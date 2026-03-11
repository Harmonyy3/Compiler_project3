package Absyn;
import Symbol.Symbol;

public class VarDec extends Dec {
    public Symbol name;        // variable name
    public boolean escape = false;
    //set to false since my code will mark it true AMY
    public Type typ;           // the declared type (has its own .name)
    public Exp init;           // initializer expression
    public bflist params;

    public VarDec(int p, bflist par, Symbol n, Type t, Exp i) {
        pos = p;
        params = par;
        name = n;
        typ = t;
        init = i;
    }
}
