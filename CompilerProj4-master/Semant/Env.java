package Semant;

import Symbol.Table;
import Symbol.Symbol;
import Types.Type;
import Types.FUNC;
import Types.NAME;

class Env {
    Table venv;       // value environment  (variables + functions)
    Table tenv;       // type environment   (types)
    ErrorMsg.ErrorMsg errorMsg;

    private static Symbol sym(String s) {
        return Symbol.symbol(s);
    }

    // Shorthand helper for RECORD
    // private static final Types.VOID VOID = Semant.VOID;
    // private static RECORD RECORD(Symbol n, Type t, RECORD x) {
    //     return new RECORD(n, t, x);
    // }
    // private static RECORD RECORD(Symbol n, Type t) {
    //     return new RECORD(n, t, null);
    // }

    // private static FunEntry FunEntry(RECORD params, Type result) {
    //     return new FunEntry(params, result);
    // }

    Env(ErrorMsg.ErrorMsg err) {
        errorMsg = err;
        venv = new Table();
        tenv = new Table();



        
        NAME INT = new NAME(sym("int"));
        INT.bind(Semant.INT);
        tenv.put(sym("int"), INT);

        // char
        NAME CHAR = new NAME(sym("char"));
        CHAR.bind(Semant.CHAR);
        tenv.put(sym("char"), CHAR);

        // double
        NAME DOUBLE = new NAME(sym("double"));
        DOUBLE.bind(Semant.DOUBLE);
        tenv.put(sym("double"), DOUBLE);

        // void
        NAME VOIDTYPE = new NAME(sym("void"));
        VOIDTYPE.bind(Semant.VOID);
        tenv.put(sym("void"), VOIDTYPE);

        NAME FLOAT = new NAME(sym("float"));
        FLOAT.bind(Semant.FLOAT);
        tenv.put(sym("float"), FLOAT);

        NAME LONG = new NAME(sym("long"));
        LONG.bind(Semant.LONG);
        tenv.put(sym("long"), LONG);

        NAME SHORT = new NAME(sym("short"));
        SHORT.bind(Semant.SHORT);
        tenv.put(sym("short"), SHORT);

      
    }
}
