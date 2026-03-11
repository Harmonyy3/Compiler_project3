package Absyn;

// List of bracketed array types (EmptyArrayTypeList or ExpArrList)
public class BrackList extends Dec{

    public Dec empty;     // could be EmptyArrayTypeList or ExpArrList
    public ExpArrList expArrList;;

    public BrackList(Dec e) {
        this.empty = e;
    }

    // convenience constructor for single element
    public BrackList(ExpArrList eal) {
        this.expArrList = eal;
    }
}
