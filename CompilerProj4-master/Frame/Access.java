package Frame;

import Temp.Temp;
import Tree.*;

public abstract class Access {
    public Frame home;

    public Access(Frame h) {
        this.home = h;
    }

    // In Access.java
        public Temp accessTemp() { throw new Error("accessTemp not implemented"); }

        // In InReg.java
        

        // In InFrame.java
        


    // Return IR Exp that accesses this variable using the frame pointer
    public abstract Exp exp(Exp framePtr);

    public abstract String toString();
}
