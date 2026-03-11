package Absyn;

public class CondExpA extends Exp {
    public Exp condition;
    public Exp thenExp;
    public Exp elsExp;

    public CondExpA(int pos, Exp condition, Exp thenExp,
    Exp elsExp) {
        this.pos = pos;
        this.condition = condition;
        this.thenExp = thenExp;
        this.elsExp = elsExp;
    }

    @Override
    public String toString(){
        return "(" + condition + "?" + thenExp + ":" + elsExp + ")";
    }
    

}
