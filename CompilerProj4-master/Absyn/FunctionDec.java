package Absyn;
import Symbol.Symbol;

public class FunctionDec extends Dec {
    public Symbol name;
    public bflist bflist;
    public Type result;   // optional return type
    public Param params;      // function body (expression or compound statement)
    public Stm body;      // next declaration in the list

    public boolean leaf = true;
//marked false if not leaf by analysis AMY
    public FunctionDec(int p, bflist a, Type r, Symbol n, Param b, Stm x) {
        pos = p;
        bflist = a;
        result = r;
        name = n;
        params = b;
        body = x;
    }
}
