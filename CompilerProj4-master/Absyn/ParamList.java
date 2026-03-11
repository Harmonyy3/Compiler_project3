package Absyn;

import Symbol.Symbol;

public class ParamList extends Dec {
    public Type type;
    public Symbol name;
    public ParamList head;   // for linked list

    // single parameter
    public ParamList(Type t, Symbol n) {
        this.type = t;
        this.name = n;
        this.head = null;
    }

    // linked list of parameters
    public ParamList(ParamList h, Type t, Symbol n) {
        this.head = h;
        this.type = t;
        this.name = n;
    }
}