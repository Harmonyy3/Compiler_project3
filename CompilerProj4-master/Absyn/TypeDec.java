package Absyn;
import Symbol.Symbol;

public class TypeDec extends Dec {
    public Symbol name;   // The name of the type being defined
    public Type ty;       // The type it defines
    public Dec next;      // For linked list of type declarations

    public TypeDec(int p,Type t, Symbol x) {
        pos = p;
        ty = t;
        name = x;
    }
}
