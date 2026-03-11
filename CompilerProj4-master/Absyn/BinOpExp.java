package Absyn;

public class BinOpExp extends Exp {
    public static final int 
    PLUS = 1, 
    MINUS = 2, 
    TIMES = 3, 
    DIVIDE = 4,
    EQ = 5, 
    NEQ = 6, 
    LT = 7, 
    LEQ = 8, 
    GT = 9, 
    GEQ = 10,
    AND = 11, 
    OR = 12, 
    MODULUS = 13, 
    LSHIFT = 14, 
    RSHIFT = 15;

    public int op;
    public Exp left;
    public Exp right;

    public BinOpExp(int p, int o, Exp l, Exp r) {
        pos = p;
        op = o;
        left = l;
        right = r;
    }
}