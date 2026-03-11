package Absyn;
import Symbol.Symbol;

public class GotoStm extends Stm {
    public Symbol label;

    public GotoStm(int pos, Symbol label) {
        super(pos);
        this.label = label;
    }
}