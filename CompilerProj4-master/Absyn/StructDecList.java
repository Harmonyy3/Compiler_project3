package Absyn;
import Symbol.Symbol;

public class StructDecList extends Dec{
    public int pos;
    public StructDecList prev;  // previous declarations (can be null)
    public Type type;             // type of the field
    public Symbol name;         // name of the field
    public Object semicolon;    // optional; you can make this int or null if unused

    public StructDecList(int p, Type t, Symbol n) {
        this(p, null, t, n);
    }

    public StructDecList(int p, StructDecList s, Type t, Symbol n) {
        this.pos = p;
        this.prev = s;
        this.type = t;
        this.name = n;
    }
}