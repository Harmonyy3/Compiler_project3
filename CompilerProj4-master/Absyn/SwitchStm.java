package Absyn;

public class SwitchStm extends Stm {
    public Exp exp;
    public Stm body;

    public SwitchStm(int pos, Exp exp, Stm body) {
        super(pos);
        this.exp = exp;
        this.body = body;
    }
}