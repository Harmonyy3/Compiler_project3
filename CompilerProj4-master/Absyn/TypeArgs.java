package Absyn;

public class TypeArgs extends Dec{
    public PointList pointers;      // can be null
    public BrackList brackets;      // can be null

    public TypeArgs(PointList p, BrackList b) {
        this.pointers = p;
        this.brackets = b;
    }

    public TypeArgs(PointList p) {
        this(p, null);
    }

    public TypeArgs(BrackList b) {
        this(null, b);
    }
}