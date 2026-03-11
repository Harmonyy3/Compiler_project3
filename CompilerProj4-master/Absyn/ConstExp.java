package Absyn;

public class ConstExp extends Exp {
    public Object value;   // Can hold Integer, Character, Double, or String (for enum names)
    public int typeTag;    // Optional: track what kind of constant this is

    // Type tags (optional for later semantic use)
    public static final int INT = 0;
    public static final int CHAR = 1;
    public static final int FLOAT = 2;
    public static final int ENUM = 3;

    public ConstExp(int p, Object v, int type) {
        pos = p;
        value = v;
        typeTag = type;
    }

    @Override
    public String toString() {
        switch (typeTag) {
            case INT: return "ConstExp (int): " + value;
            case CHAR: return "ConstExp (char): '" + value + "'";
            case FLOAT: return "ConstExp (float): " + value;
            case ENUM: return "ConstExp (enum): " + value;
            default: return "ConstExp: " + value;
        }
    }
}
