package Temp;

import java.util.HashMap;

public class Temp  {
    private static int count;
    public int num;
    private static HashMap<Integer, Temp> allTemps = new HashMap<>();

    public Temp() {
        num = count++;
        allTemps.put(num, this);
    }

    public static Temp temp(int n) {
        return allTemps.get(n);  // Return the existing Temp with this num
    }

    public String toString() { return "t" + num; }
    public int key() { return num; }
    public int hashCode() { return num; }
}
