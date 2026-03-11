package Absyn;

public class Expstm extends Stm {
    public Exp expression;

    public Expstm(int pos, Exp expression) {
        super(pos);
        this.expression = expression;
    }

    
    public Exp toExp() {
        return expression;
    }
}
