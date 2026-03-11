package Frame;

import Temp.Temp;
import Tree.*;

public class InFrame extends Access {
    public int offset;
    public Frame frame;

    public InFrame(Frame h, int off) {
        super(h);
        frame = h;
        this.offset = off;
    }

    @Override
        public Temp accessTemp() { 
            // allocate a temporary for frame access (sp + offset)
            Temp t = new Temp();
            // generate assembly code to load/store later
            return t;
        }

    @Override
    public Exp exp(Exp framePtr) {
        return new MEM(new BINOP(BINOP.PLUS, framePtr, new CONST(offset)));
    }

    @Override
    public String toString() {
        return "InFrame(" + offset + ")";
    }
}
