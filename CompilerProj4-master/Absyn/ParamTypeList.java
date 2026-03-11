package Absyn;

public class ParamTypeList extends Dec{
    public Type tail;
    public ParamTypeList head;

    // single type
    public ParamTypeList(Type t) {
        this.tail = t;
        this.head = null;
    }

    // linked list
    public ParamTypeList(ParamTypeList h, Type t) {
        this.head = h;
        this.tail = t;
    }
}
