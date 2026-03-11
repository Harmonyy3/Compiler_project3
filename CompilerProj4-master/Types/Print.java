package Types;

import java.util.Map;
import Symbol.Symbol;

public class Print {
  java.io.PrintWriter out;

  public Print(java.io.PrintWriter o) { out = o; }

  void indent(int d) {
    for(int i=0; i<d; i++) 
      out.print(' ');
  }

  void say(String s) {
    out.print(s);
  }

  void say(int i) {
    out.print(i);
  }

  void say(boolean b) {
    out.print(b);
  }

  void sayln(String s) {
    out.println(s);
  }

  public void prType(Type t, int d) {
    if (t == null) {
      say("()");
      return;
    }
    if      (t instanceof INT)    prType((INT)   t, d);
    else if (t instanceof STRING) prType((STRING)t, d);
    else if (t instanceof FUNC) prType((FUNC)t, d);
    else if (t instanceof STRUCT) prType((STRUCT)t, d);
    else if (t instanceof UNION) prType((UNION)t, d);
    else if (t instanceof ARRAY)  prType((ARRAY) t, d);
    else if (t instanceof NIL)    prType((NIL)   t, d);
    else if (t instanceof VOID)   prType((VOID)  t, d);
    else if (t instanceof NAME)   prType((NAME)  t, d);
    else throw new Error("Print.prType");
  }

  void prType(INT t, int d) {
    say("INT");
  }

  void prType(STRING t, int d) {
    say("STRING");
  }

  void prType(FUNC t, int d) {
    say("FUNC(");
    if (t != null) {
      sayln(""); indent(d+1); sayln(t.fieldName.toString());
      indent(d+1); say(":"); prType(t.fieldType, d+2); sayln(",");
      indent(d+1); prType(t.tail, d+1);
    }
    say(")");
  }

    void prType(STRUCT t, int d) {
    say("STRUCT(");
    if (t != null) {
        sayln("");
        indent(d + 1);
        say("Name: ");
        sayln(t.name.toString());

        for (Map.Entry<Symbol, Type> entry : t.fields.entrySet()) {
            indent(d + 1);
            say(entry.getKey().toString());
            say(" : ");
            prType(entry.getValue(), d + 2);
            sayln("");
        }
    }
    indent(d);
    say(")");
}


  void prType(ARRAY t, int d) {
    sayln("ARRAY("); indent(d+1); prType(t.element, d+1); say(")");
  }

  void prType(NIL t, int d) {
    say("NIL");
  }

  void prType(VOID t, int d) {
    say("VOID");
  }

  void prType(NAME t, int d) {
    Type b = t.binding;
    say("NAME("); say(t.name.toString()); say(")");
    if (b == null)
      return;
    t.binding = null;
    sayln(""); indent(d); say("="); prType(b, d+1);
    t.binding = b;
  }
}
