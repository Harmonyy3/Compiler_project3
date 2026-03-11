package Absyn;

public class SelectStm extends Stm {
    public Exp expression;
    public Stm Stm1;
    public Stm Stm2; 

    public SelectStm(int pos, Exp expression, Stm Stm1, Stm Stm2) {
        super(pos);
        this.expression = expression;
        this.Stm1 = Stm1;
        this.Stm2 = Stm2;
    }
}