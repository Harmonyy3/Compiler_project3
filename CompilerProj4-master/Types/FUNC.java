package Types;

public class FUNC extends Type {
   public Symbol.Symbol fieldName;
   public Type fieldType;
   public FUNC tail;
   public boolean isPointer;
   public boolean escape;


   public FUNC(Symbol.Symbol n, Type t, FUNC x, boolean isPointer) {
    fieldName = n;
    fieldType = t;
    tail = x;
    this.isPointer = isPointer;
    //this.escape = escape;
}

   
   // public RECORD(Symbol.Symbol n, Type t, RECORD x) {
   //     fieldName=n; fieldType=t; tail=x;
   // }
   public boolean coerceTo(Type t) {
	return this==t.actual();
   }
}
