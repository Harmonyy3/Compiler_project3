package Temp;

import java.util.HashMap;
import java.util.Map;

public class DefaultMap implements TempMap {
    private int counter = 0;
    private Map<Temp, String> map = new HashMap<>();

    @Override
    public String tempMap(Temp t) {
        if (!map.containsKey(t)) {
            map.put(t, "$t" + (counter++));
        }
        return map.get(t);
    }
}

