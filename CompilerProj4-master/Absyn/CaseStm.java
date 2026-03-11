package Absyn;

public class CaseStm extends Stm {
    public Exp exp;
    public Stm body;

    public CaseStm (int pos, Exp e, Stm b) {
        super(pos);
        this.exp = e;   
        this.body = b;
    }
}