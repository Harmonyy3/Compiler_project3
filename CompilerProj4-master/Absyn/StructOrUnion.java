package Absyn;
import Symbol.Symbol;

public class StructOrUnion extends Dec{
    public Symbol symbol;  // make sure this exists and is public
    public StructOrUnion(Symbol s) { this.symbol = s; }
}