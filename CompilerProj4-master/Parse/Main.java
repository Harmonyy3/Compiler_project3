package Parse;

import java.io.PrintWriter;

public class Main {

    public static void main(String argv[]) throws java.io.IOException {
        for (int i = 0; i < argv.length; ++i) {
            String filename = argv[i];
            if (argv.length > 1)
                System.out.println("***Processing: " + filename);

            Parse parse = new Parse(filename);
            PrintWriter writer = new PrintWriter(System.out);
            Absyn.Print printer = new Absyn.Print(writer);

            Object ast = parse.absyn;

            if (ast instanceof Absyn.Exp) {
                printer.prExp((Absyn.Exp) ast, 0);
            } else if (ast instanceof Absyn.Stm) {
                printer.prStm((Absyn.Stm) ast, 0);
            } else if (ast instanceof Absyn.Dec) {
                printer.prDec((Absyn.Dec) ast, 0);
            } else if (ast instanceof Absyn.DecList) {
                printer.prDecList((Absyn.DecList) ast, 0);
            } else {
                System.out.println("Unknown AST node type: " + ast.getClass().getName());
            }

            writer.println();
            writer.flush();
        }
    }
}
