package Absyn;

public class ReturnStm extends Stm {
    public Exp exp; 

    public ReturnStm(int p, Exp e) {
        super(p); // Call the constructor of the superclass Stm
        pos = p; 
        exp = e;
    }
}