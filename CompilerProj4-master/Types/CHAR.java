package Types;

public class CHAR extends Type {
    public CHAR() {}
    public boolean coercedTo(Type t) {return (t.actual() instanceof CHAR);}
}
