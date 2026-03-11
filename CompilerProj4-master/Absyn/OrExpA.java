package Absyn;

public class OrExpA extends Exp{
 
    public Exp left;

    public Exp right;


    public OrExpA(int pos, Exp left, Exp right){
        this.pos = pos;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "("+left+ " || " + right + ")";
    }

    
}    

