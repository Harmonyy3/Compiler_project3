package Absyn;

public class IntConstExp extends Exp {
    public int value;

    public IntConstExp(int p, int v) {
        pos = p;
        value = v;
    }
}
