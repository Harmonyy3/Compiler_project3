package Absyn;

public class UnaryOpExp extends Exp {
    public static final int NEG = 1, 
                            NOT = 2, 
                            PREINC = 3, 
                            PREDEC = 4,
                            POSTINC = 5, 
                            POSTDEC = 6;

    public static final int BITWISEAND = 7, 
                            TIMES = 8, 
                            PLUS = 9, 
                            MINUS = 10, 
                            TILDE = 11;

    public int op;
    public Exp exp;

    public UnaryOpExp(int p, int o, Exp e) {
        pos = p;
        op = o;
        exp = e;
    }
}