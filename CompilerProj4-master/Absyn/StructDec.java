package Absyn;
import Symbol.Symbol;

public class StructDec extends Dec{
    
    public StructDecList declarations;

    public StructDec(StructDecList d) {
        this.declarations = d;
    }
}
