package Absyn;

public class CompoundStm extends Stm {
    public DecList decls;
    public StmList stms;

    public CompoundStm(int pos, DecList decls, StmList stms) {
        super(pos);
        this.decls = decls;
        this.stms = stms;
    }
}