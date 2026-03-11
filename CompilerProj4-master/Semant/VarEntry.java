package Semant;

import Frame.Access;  // Make sure Access is imported

public class VarEntry extends Entry {
    public Types.Type ty;   // type of the variable
    public boolean isPointer;
    public Access access;   // location in frame (optional)

    // Constructor for type only (old behavior)
    public VarEntry(Access a, Types.Type t, boolean ip) {
        this.access = a;
        this.ty = t;
        this.isPointer = ip;
        
    }

    // public VarEntry(Types.Type t, Access a) {
    //     this.ty = t;
    //     this.isPointer = false;
    //     this.access = a;
    // }

    // Constructor for type + frame access (new behavior)
    public VarEntry(Types.Type t) {
        this.ty = t;
        this.isPointer = false;
        this.access = null;
    }
}
