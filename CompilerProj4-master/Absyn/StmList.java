package Absyn;

public class StmList {
    public Stm head;
    public StmList tail;

    public StmList(Stm head, StmList tail) {
        this.head = head;
        this.tail = tail;
    }
}