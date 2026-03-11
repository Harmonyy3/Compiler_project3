package Absyn;
import Symbol.Symbol;

public class SizeofTypeExp extends Exp {
    public TypeName type;
    public SizeofTypeExp(TypeName type) { super(); this.type = type; }
    public SizeofTypeExp(int pos, TypeName type) { super(pos); this.type = type;}
}