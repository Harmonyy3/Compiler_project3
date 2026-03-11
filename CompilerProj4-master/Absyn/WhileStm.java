package Absyn;

public class WhileStm extends Stm {
    public Exp test;
    public Stm body;

    public WhileStm(int pos, Exp test, Stm body) {
        super(pos);
        this.test = test;
        this.body = body;
    }
}