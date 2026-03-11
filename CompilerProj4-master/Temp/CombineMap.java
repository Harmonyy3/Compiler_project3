// CombineMap.java
package Temp;

public class CombineMap implements TempMap {

    private final TempMap primary;   // e.g., RegAlloc
    private final TempMap secondary; // e.g., DefaultMap

    public CombineMap(TempMap primary, TempMap secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public String tempMap(Temp t) {
        String r = primary.tempMap(t);
        return (r != null) ? r : secondary.tempMap(t);
    }
}
