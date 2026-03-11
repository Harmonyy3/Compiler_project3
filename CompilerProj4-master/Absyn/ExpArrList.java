package Absyn;

public class ExpArrList extends Dec{
    public ExpArr head;
    public ExpArrList tail;

    // Constructor for a list element with tail
    public ExpArrList(ExpArr h, ExpArrList t) {
        this.head = h;
        this.tail = t;
    }

    // Convenience constructor for a single element
    public ExpArrList(ExpArr h) {
        this(h, null);
    }
}

