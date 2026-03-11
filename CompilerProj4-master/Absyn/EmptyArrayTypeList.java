package Absyn;

public class EmptyArrayTypeList extends Dec{
    public Dec head;
    public EmptyArrayTypeList tail;

    public EmptyArrayTypeList(Dec h, EmptyArrayTypeList t) {
        this.head = h;
        this.tail = t;
    }

    // Convenience constructor for single element
    public EmptyArrayTypeList(Dec h) {
        this(h, null);
    }
}