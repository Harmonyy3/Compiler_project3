package Absyn;

public class FieldAccessExp extends Exp {
    public Exp record;
    public String field;

    public FieldAccessExp(int p, Exp r, String f) {
        pos = p;
        record = r;
        field = f;
    }

}