package Parse;

import java.io.IOException;

public class Parse {

    public ErrorMsg.ErrorMsg errorMsg;
    public Object absyn; // store the raw parser result

    public Parse(String filename) {
        errorMsg = new ErrorMsg.ErrorMsg(filename);
        java.io.InputStream inp;
        try {
            inp = new java.io.FileInputStream(filename);
        } catch (java.io.FileNotFoundException e) {
            throw new Error("File not found: " + filename);
        }

        Grm parser = new Grm(new Yylex(inp, errorMsg), errorMsg);

        try {
            // Get the parser result without casting
            absyn = parser.parse().value;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Error(e.toString());
        } finally {
            try {
                inp.close();
            } catch (IOException e) {}
        }
    }
}
