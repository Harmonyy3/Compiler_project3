package Absyn;
import Symbol.Symbol;
public class OpExpA extends Exp {
    public Exp left;
    public Exp right;
    public int oper;

    // Operator constants
    public final static int PLUS = 0, MINUS = 1, MUL = 2, DIV = 3;
    public final static int EQ = 4, NE = 5, LT = 6, LE = 7, GT = 8, GE = 9;
    public final static int BITAND = 10, BITXOR = 11, BITOR = 12;
    public final static int PREINC = 13, PREDEC = 14;

    public OpExpA(int pos, int oper, Exp left, Exp right){
        this.pos = pos;
        this.oper = oper;
        this.left = left;
        this.right = right;
    }    

    @Override
    public String toString() {
        String opStr;
        switch (oper) {
            case BITAND: opStr = "&"; break;
            case BITXOR: opStr = "^"; break;
            case BITOR:  opStr = "|"; break;
            case PLUS:   opStr = "+"; break;
            case MINUS:  opStr = "-"; break;
            // Add others as needed...
            default: opStr = "?";
        }
        return "(" + left + " " + opStr + " " + right + ")";
    }
}