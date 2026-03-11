package Absyn;

public class ArrayAccessExp extends Exp {
    public Exp array;
    public Exp index;

    public ArrayAccessExp(int p, Exp a, Exp i) {
        pos = p;
        array = a;
        index = i;
    }
}