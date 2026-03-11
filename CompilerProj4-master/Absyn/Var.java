package Absyn;

/** Base class for variables (lvalues). */
public abstract class Var {
    public int pos = -1;
    public Var() {}
    public Var(int pos) { this.pos = pos; }
}
