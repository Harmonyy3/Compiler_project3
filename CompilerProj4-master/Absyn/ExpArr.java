package Absyn;

public class ExpArr extends Dec{
    public Exp constExpr;

    public ExpArr(Exp c) {
        this.constExpr = c;
    }
}