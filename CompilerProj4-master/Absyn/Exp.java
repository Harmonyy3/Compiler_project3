package Absyn;

/** Base class for all expressions. */
public abstract class Exp {
    public int pos = -1;
    public Exp() {}
    public Exp(int pos) { this.pos = pos; }
}
