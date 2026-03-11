package Absyn;

// List of pointers (each TIMES token)
public class PointList extends Dec{
    public Dec head;       // TIMES token
    public PointList tail;

    public PointList(Dec h) {
        this.head = h;
    }

    // convenience constructor for single pointer
    public PointList() {
        this(null);
    }
}
