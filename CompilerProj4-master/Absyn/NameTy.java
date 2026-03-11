package Absyn;
import Symbol.Symbol;

public class NameTy extends Dec {
    public Symbol name;
    public int pos;

    public NameTy(Symbol n) {
        this.name = n;
        this.pos = -1; // optional: ignore for now
    }
}