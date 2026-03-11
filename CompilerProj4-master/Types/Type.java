package Types;

public abstract class Type {
   public static final Type VOID = new VOID();
   public static final Type INT = new INT();
   public static final Type CHAR = new CHAR();
   public static final Type SHORT = new SHORT();
   public static final Type LONG = new LONG();
   public static final Type DOUBLE = new DOUBLE();
   public static final Type FLOAT = new FLOAT();
   public static final Type STRING = new STRING();
   //public static final Type ENUM = new ENUM();
   //public static final Type STRUCT = new STRUCT();


   public Type actual() {return this;}
         
   public boolean coerceTo(Type t) {return false;}
}
