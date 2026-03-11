package Absyn;

public class PointerAccessExp extends Exp {
    public Exp pointer;
    public String field;

    public PointerAccessExp(int p, Exp ptr, String f) {
        pos = p;
        pointer = ptr;
        field = f;
    }
}