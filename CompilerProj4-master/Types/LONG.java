package Types;

public class LONG extends Type {
	public LONG () {}
	public boolean coerceTo(Type t) {return (t.actual() instanceof LONG);}
}