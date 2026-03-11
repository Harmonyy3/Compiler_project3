package Absyn;
import Symbol.Symbol;;

public class Enum extends Dec{
    public String name;
    public Exp value;

    public Enum(int pos, String n) {
        super(pos);
        this.name = n;
        this.value = null;
    }
    
    public Enum(int pos, String n, Exp c) {
        super(pos);
        this.name = n;
        this.value = c;
    }
}
