package Absyn;

public class DoWhileStm extends Stm {
    public Stm body;
    public Exp condition;

    public DoWhileStm(int pos, Stm body, Exp condition) {
        super(pos);
        this.body = body;
        this.condition = condition;
    }
}