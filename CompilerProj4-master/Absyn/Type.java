package Absyn;

public class Type{
    public int pos;
    public TypeName typeName;
    public TypeArgs typeArgs; // optional, can be null

    public Type(int p, TypeName n, TypeArgs a) {
        //super(n != null ? n.name : null);
        this.pos = p;
        this.typeName = n;
        this.typeArgs = a;
    }

    public Type(int p, TypeName n) {
        this(p, n, null);
    }

    // @Override
    // public String toString() {
    //     return name != null ? name.toString() : "Type";
    // }
}
