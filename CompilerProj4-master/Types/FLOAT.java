package Types;

public class FLOAT extends Type {
	public FLOAT () {}
	public boolean coerceTo(Type t) {return (t.actual() instanceof FLOAT);}
}
