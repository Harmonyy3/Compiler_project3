package Absyn;

public class DefaultStm extends Stm {
    public Stm stm;
    
    public DefaultStm(int pos, Stm s) {
        super(pos);
        this.stm = s;
    }
    
}