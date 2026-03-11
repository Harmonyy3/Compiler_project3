package Absyn;
import Symbol.Symbol;

public class TypeName{

    public String name;
    public int pos;

    public TypeName(int p, String name) {
        this.name = name;
        this.pos = p;
    }

    @Override
    public String toString() {
        return name;
    }
}