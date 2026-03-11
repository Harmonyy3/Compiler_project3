package Frame;

import Temp.Temp;
import Tree.*;

public class InReg extends Access {
    public Temp temp;

    public InReg(Frame h, Temp t) {
        super(h);
        this.temp = t;
    }

    @Override
        public Temp accessTemp() { return this.temp; }

    @Override
    public Exp exp(Exp framePtr) {
        return new TEMP(temp);
    }

    @Override
    public String toString() {
        return "InReg(" + temp + ")";
    }
}
