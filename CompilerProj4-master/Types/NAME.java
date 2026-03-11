package Types;

public class NAME extends Type {
   public Symbol.Symbol name;
   public Type binding;
   
   //public NAME(Symbol.Symbol n) {name=n;}
   // 1-arg constructor (still useful for forward declarations)
    public NAME(Symbol.Symbol n) {
        name = n;
        binding = null;
    }

    // 2-arg constructor for typedefs, etc.
    public NAME(Symbol.Symbol n, Type b) {
        name = n;
        binding = b;
    }
   
   public boolean isLoop() {
      Type b = binding; 
      boolean any;
      binding=null;
      if (b==null) any=true;
      else if (b instanceof NAME)
            any=((NAME)b).isLoop();
      else any=false;
      binding=b;
      return any;
     }
     
   public Type actual() {return binding.actual();}

   public boolean coerceTo(Type t) {
	return this.actual().coerceTo(t);
   }
   public void bind(Type t) {binding = t;}
}
