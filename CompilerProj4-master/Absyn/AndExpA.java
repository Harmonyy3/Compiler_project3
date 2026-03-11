package Absyn;
import Symbol.Symbol;

public class AndExpA extends Exp {
    public Exp left;

    public Exp right;


    public AndExpA(int pos, Exp left, Exp right){
        this.pos = pos;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "("+left+ " && " + right + ")";
    }

    
}
