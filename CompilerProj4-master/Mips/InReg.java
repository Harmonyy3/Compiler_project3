package Mips;

import Frame.Access;
import Temp.Temp;
import Tree.*;

public class InReg extends Access {
    public Temp temp;

    public InReg(Temp t) {
      super(null);
        temp = t;
    }

    @Override
    public Exp exp(Exp fp) {
        // For registers, fp is ignored
        return new TEMP(temp);
    }

    @Override
    public String toString() {
        return "InReg(" + temp + ")";
    }
}
