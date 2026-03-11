package Absyn;

import Symbol.Symbol;

public class bflist extends Dec {
    public Symbol name;    // parameter name
    public bfval typ;        // parameter type (TypeDec or Type)
    public boolean escape = false; // escape analysis flag
    //AMY the escape analysis will make it true if escape
    public bflist tail;    // next parameter in list

    public bflist(bfval t, bflist tail) {
        this.typ = t;
        this.tail = tail;
    }
}
