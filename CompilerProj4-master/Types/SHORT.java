package Types;

public class SHORT extends Type {
	public SHORT () {}
	public boolean coerceTo(Type t) {return (t.actual() instanceof SHORT);}
}
