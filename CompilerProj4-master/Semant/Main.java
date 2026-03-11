package Semant;

import java.io.PrintWriter;
import Parse.Parse;
//import Absyn.*;
import FindEscape.FindEscape;

public class Main {

  public static void main(String argv[]) {
    for (int i = 0; i < argv.length; ++i) {

      String filename = argv[i];
      if (argv.length > 1)
        System.out.println("***Processing: " + filename);

 
      // PARSE INPUT
      Parse parse = new Parse(filename);
      Object ast = parse.absyn;

      // DEBUG PRINT DELETE LATER 
      // System.out.println("=== DEBUG AST TYPE ===");
      // System.out.println(ast);
      // System.out.println("Class = " + ast.getClass());
      
      // RUN ESCAPE ANALYSIS
      FindEscape FE = new FindEscape();

      if (ast instanceof Absyn.Exp) {
          FE.findEscapeExp((Absyn.Exp) ast);
      } 
      else if (ast instanceof Absyn.DecList) {
          FE.findEscapeDecList((Absyn.DecList) ast);
      } 
      else if (ast instanceof Absyn.Stm) {
          FE.findEscapeStm((Absyn.Stm) ast);
      } 
      else {
          System.err.println("ERROR: Unknown AST root type " + ast.getClass());
      }

      // SEMANTIC ANALYSIS
      Semant semant = new Semant(parse.errorMsg);

      semant.beginScope();

      if (parse.absyn instanceof Absyn.Exp) {
          semant.transProgExp((Absyn.Exp) parse.absyn);
      } else if (parse.absyn instanceof Absyn.DecList) {
          semant.transProgDecList((Absyn.DecList) parse.absyn);
      } else if (parse.absyn instanceof Absyn.Stm) {
          semant.transProgStm((Absyn.Stm) parse.absyn);
      } else {
          System.err.println("Top-level AST is not an expression!" + parse.absyn.getClass().getName());
      }

      semant.endScope();
      
      if (parse.errorMsg.anyErrors) {
          System.out.println("Compilation failed");
      } else {
          System.out.println("Compilation successful");
      }
    }
  }
}
