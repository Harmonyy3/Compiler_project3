package Mips;

import Frame.Access;
import Tree.*;

public class InFrame extends Access {
    public int offset;

    public InFrame(int offset) {
      super(null);
        this.offset = offset;
    }

    @Override
    public Exp exp(Exp fp) {
        // MEM(fp + offset)
        return new MEM(new BINOP(BINOP.PLUS, fp, new CONST(offset)));
    }

    @Override
    public String toString() {
        return "InFrame(" + offset + ")";
    }
}
