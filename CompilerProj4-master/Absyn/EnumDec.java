package Absyn;
import Symbol.Symbol;

public class EnumDec extends Dec {
    public String name;
    public EnumList enumerators;

    public EnumDec(int pos, String n, EnumList x ) {
        super(pos);              // ✅ store position like other Decs
        this.name = n;
        this.enumerators = x;
    }
}
