package Translate;

import Parse.Parse;
import Semant.Semant;

import java.io.PrintWriter;

import FindEscape.FindEscape;
import Types.Type;
import Translate.Exp;


public class Main {

  public static void main(String[] argv) {
    for (int i = 0; i < argv.length; i++) {

      String filename = argv[i];
      if (argv.length > 1)
        System.out.println("*** Processing: " + filename);

      // 1) PARSE
      Parse parse = new Parse(filename);
      Object ast = parse.absyn;

      // 2) ESCAPE ANALYSIS
      FindEscape FE = new FindEscape();

      if (ast instanceof Absyn.Exp)
        FE.findEscapeExp((Absyn.Exp) ast);
      else if (ast instanceof Absyn.DecList)
        FE.findEscapeDecList((Absyn.DecList) ast);
      else if (ast instanceof Absyn.Stm)
        FE.findEscapeStm((Absyn.Stm) ast);
      else {
        System.err.println("ERROR: Unknown AST root: " + ast.getClass());
        continue;
      }

      // 3) SEMANTIC + TRANSLATION
      Semant semant = new Semant(parse.errorMsg);
      semant.beginScope();

      Exp result = null;

      if (ast instanceof Absyn.Exp)
        result = semant.transProgExp((Absyn.Exp) ast);
      else if (ast instanceof Absyn.DecList)
        result = semant.transProgDecList((Absyn.DecList) ast);
      else if (ast instanceof Absyn.Stm)
        result = semant.transProgStm((Absyn.Stm) ast);

      semant.endScope();

      if (parse.errorMsg.anyErrors) {
        System.out.println("Compilation failed");
        continue;
      }

      if (result == null) {
        System.out.println("No IR produced.");
        continue;
      }

      // 4) PRINT THE IR TREE
      PrintWriter pw = new PrintWriter(System.out, true);  // auto-flush = true
	  Tree.Print print = new Tree.Print(pw);
      System.out.println("=== IR for " + filename + " ===");

      // Convert Translate.Exp → Tree.Stm
      Tree.Stm stm = result.unNx();
      print.prStm(stm);

      System.out.println("\n=== End IR ===");
    }
  }
}
