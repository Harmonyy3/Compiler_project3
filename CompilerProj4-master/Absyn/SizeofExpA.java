package Absyn;

public class SizeofExpA extends Exp{

    public Exp expr;           // used for sizeof(expression)
    public TypeName typeName;  // used for sizeof(type)
    public boolean isType;     // true if sizeof(type), false if sizeof(expr)

    public SizeofExpA(int pos, Exp expr) {
        this.pos = pos;
        this.expr = expr;
        this.isType = false;
    }

    public SizeofExpA(int pos, TypeName typeName) {
        this.pos = pos;
        this.typeName = typeName;
        this.isType = true;
    }

    @Override
    public String toString() {
        if (isType)
            return "(sizeof(" + typeName + "))";
        else
            return "(sizeof(" + expr + "))";
    }
}
