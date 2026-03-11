package Absyn;

public class Param extends Dec {
    public Object elipses;
    public ParamList paramList;


    // ( parameter_list )
    public Param(ParamList p) {
        this.paramList = p;
    }

    // ( parameter_list , ... )
    public Param(ParamList p, Object e) {
        this.paramList = p;
        this.elipses = e;
    }

    // ( )
    public Param() {
        this.paramList = null;
    }
}
