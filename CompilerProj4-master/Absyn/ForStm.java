package Absyn;

public class ForStm extends Stm {
    public Exp init;
    public Exp condition;
    public Exp increment;
    public Stm body;

    public ForStm(int pos, Exp init, Exp condition, Exp increment, Stm body) {
        super(pos);
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }
}