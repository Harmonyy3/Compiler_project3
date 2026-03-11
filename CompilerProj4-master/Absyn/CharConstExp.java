package Absyn;

public class CharConstExp extends Exp {
    public char value;

    public CharConstExp(int p, char v) {
        pos = p;
        value = v;
    }
}
