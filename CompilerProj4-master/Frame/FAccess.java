package Frame;

import Tree.Exp;

public abstract class FAccess {
    public abstract Exp exp(Exp framePointer);
    public abstract String toString();
}
