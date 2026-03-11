package Types;
import java.util.Map;
import Symbol.Symbol;

public class UNION extends Type {
    public final Symbol name;
    public final Map<Symbol, Type> fields;   // fieldName → fieldType
    private Symbol activeField;

    public UNION(Symbol name, Map<Symbol, Type> fields) {
        this.name = name;
        this.fields = fields;
    }

    public void setActiveField(Symbol field) {
        this.activeField = field;
    }

    public Type getActiveType() {
        return fields.get(activeField);
    }
}
