package Absyn;

public class EnumList extends Dec{
    public Dec head;         // current enumerator
    public EnumList tail;     // remaining enumerators
    // Constructor for single enumerator
    public EnumList(Dec h) {
        this.head = h;
        this.tail = null;
    }

    // Constructor for enumerator list with a comma
    public EnumList(EnumList l, Dec e) {
        this.head = e;
        this.tail = l;
    }
}