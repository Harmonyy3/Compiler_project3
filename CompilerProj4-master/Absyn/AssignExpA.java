package Absyn;

public class AssignExpA extends Exp {
    public Exp left;
    public Exp right;
    public int oper;

    public final static int ASSIGN = 10;
    public final static int PASSIGN = 12;
    public final static int MASSIGN = 14;
    public final static int MULASSIGN = 16;
    public final static int DIVASSIGN= 18;
    public final static int MODASSIGN = 20;
    public final static int BANDASSIGN = 22;
    public final static int XORASSIGN = 24;
    public final static int LSASSIGN = 26;
    public final static int RSASSIGN = 28;
    public final static int ORASSIGN = 30;

    public AssignExpA(int pos, int oper, Exp left, Exp right){
        this.pos = pos;
        this.oper = oper;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString(){
        String opStr;
        switch(oper) {
            case ASSIGN: opStr = "="; break;
            case PASSIGN: opStr = "+="; break;
            case MASSIGN: opStr = "-="; break;
            case MULASSIGN: opStr = "*="; break;
            case DIVASSIGN: opStr = "/="; break;
            case MODASSIGN: opStr = "%="; break;
            case BANDASSIGN: opStr = "&="; break;
            case XORASSIGN: opStr = "^="; break;
            case LSASSIGN: opStr = "<<="; break;
            case RSASSIGN: opStr = ">>="; break;
            case ORASSIGN: opStr = "|="; break;
            default: opStr = "?=" ; break;
        }
        return "("+ left+ " "+ opStr + " "+ right+ ")";
       
    }




}
