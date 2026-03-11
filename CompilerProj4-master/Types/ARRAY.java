package Types;

import java.util.List;

import Absyn.ExpArrList;
import Absyn.ExpArr;
import Absyn.IntConstExp;

public class ARRAY extends Type {
    public Type element;
    public List<Integer> dims;
    public int emptyArrayDimSize = 0;

    public ARRAY(Type element, List<Integer> dims) {
        this.element = element;
        this.dims = dims;
    }

    public ARRAY(Type element, int eADS) {
        this.element = element;
        this.emptyArrayDimSize = eADS;
    }

    public ExpArrList toExpArrList() {
        ExpArrList result = null;
        // reverse order because head of ExpArrList should be innermost dimension
        for (int i = dims.size() - 1; i >= 0; i--) {
            IntConstExp c = new IntConstExp(0, dims.get(i)); // 0 = dummy position
            result = new ExpArrList(new ExpArr(c), result);
        }

        return result;
    }
}

