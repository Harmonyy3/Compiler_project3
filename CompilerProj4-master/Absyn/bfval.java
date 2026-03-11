package Absyn;

import Symbol.Symbol;

public class bfval extends Dec {
    public String value; // stores CONST, VOLATILE, etc.

    public bfval(String v) {
        this.value = v;
    }
}