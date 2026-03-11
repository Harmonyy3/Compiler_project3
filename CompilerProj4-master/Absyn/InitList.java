package Absyn;

public class InitList extends Exp {
    public Init head;          // first initializer
    public InitList tail;      // rest of the list
    public int commaPos;       // position of comma (optional, only for second form)

    // single initializer
    public InitList(Init h) {
        this.head = h;
        this.tail = null;
    }

    // initializer_list , initializer
    public InitList(InitList l, Init i) {
        this.head = i;
        this.tail = l;
    }
}
