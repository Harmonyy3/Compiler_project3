package Types;
import java.util.Map;
import Symbol.Symbol;

public class STRUCT extends Type {
    public final Symbol name;
    public final Map<Symbol, Type> fields;   // fieldName → fieldType
    

    public STRUCT(Symbol name, Map<Symbol, Type> fields) {
        this.name = name;
        this.fields = fields;
    }

    

}
