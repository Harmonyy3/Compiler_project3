package Absyn;

import Symbol.Symbol;

public class LabelStm extends Stm {
    public Symbol label; 
    public Exp exp;      
    public Stm stm;

    public LabelStm(int pos, Symbol label, Stm stm) {
        super(pos);
        this.label = label;
        this.stm = stm;
    }
} 