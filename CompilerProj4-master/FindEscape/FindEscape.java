package FindEscape;

import Absyn.*;
import Symbol.*;

/**
 * Escape analysis for the C-like AST.
 *
 * - Tracks lexical depth.
 * - Marks VarDec.escape = true when a variable "escapes".
 * - Marks FunctionDec.leaf = false when a CallExp is seen inside that function.
 */
public class FindEscape {

    /** Entry stored in the escape environment for each variable. */
    private static class EscapeEntry {
        final int depth;
        final VarDec var;

        EscapeEntry(int depth, VarDec var) {
            this.depth = depth;
            this.var   = var;
        }

        void markEscape() {
            if (!var.escape) {  // avoid duplicate prints
                var.escape = true;
                // System.out.println("ESCAPE: variable " + var.name);
            }
        }
    }

    /** Symbol table mapping variable names -> EscapeEntry. */
    private final Table env = new Table();

    /** Current function we are inside (for leaf / non-leaf). */
    private FunctionDec currentFunction = null;

    /* =========================================================
     * Public entry points used by Semant.Main
     * ========================================================= */

    public void findEscapeDecList(DecList list) {
        traverseDecList(0, list);
    }

    public void findEscapeExp(Exp e) {
        traverseExp(0, e);
    }

    public void findEscapeStm(Stm s) {
        traverseStm(0, s);
    }

    /* =========================================================
     * Declarations
     * ========================================================= */

    private void traverseDecList(int depth, DecList d) {
        if(d != null){
            //System.out.println("DecList head = " + d.head.getClass());

        }

        for (DecList cur = d; cur != null; cur = cur.tail) {
            traverseDec(depth, cur.head);
        }
    }

    private void traverseDec(int depth, Dec d) {
        if (d == null) return;
        // System.out.println("Dec = " + d.getClass());

        // Local / global variable
        if (d instanceof VarDec) {

            VarDec v = (VarDec) d;
            v.escape = false; // default
            // System.out.println("Found VarDec: " + v.name);

            // Insert into environment at this depth
            env.put(v.name, new EscapeEntry(depth, v));

            // Analyze initializer
            traverseExp(depth, v.init);
        }

        // Function declaration
        else if (d instanceof FunctionDec) {
            FunctionDec f = (FunctionDec) d;

            // Assume leaf until we see a CallExp.
            f.leaf = true;

            FunctionDec saved = currentFunction;
            currentFunction   = f;

            env.beginScope();
            int bodyDepth = depth + 1;

            // NOTE: You could also traverse formals here if your Param/ParamList
            // classes have names / escape flags. We only handle locals + body.
            traverseStm(bodyDepth, f.body);

            env.endScope();
            currentFunction = saved;
        }

        // Other declaration kinds (typedef, struct, enum, etc.) don't affect escape.
    }

    /* =========================================================
     * Statements
     * ========================================================= */

    private void traverseStmList(int depth, StmList sl) {
        for (StmList cur = sl; cur != null; cur = cur.tail) {
            traverseStm(depth, cur.head);
        }
    }

    private void traverseStm(int depth, Stm s) {
        if (s == null) return;

        if (s instanceof Expstm) {
            traverseExp(depth, ((Expstm) s).expression);
        }

        else if (s instanceof CompoundStm) {
            CompoundStm c = (CompoundStm) s;
            env.beginScope();
            traverseDecList(depth, c.decls);
            traverseStmList(depth, c.stms);
            env.endScope();
        }

        else if (s instanceof WhileStm) {
            WhileStm w = (WhileStm) s;
            traverseExp(depth, w.test);
            traverseStm(depth, w.body);
        }

        else if (s instanceof ForStm) {
            ForStm f = (ForStm) s;
            traverseExp(depth, f.init);
            traverseExp(depth, f.condition);
            traverseExp(depth, f.increment);
            traverseStm(depth, f.body);
        }

        else if (s instanceof DoWhileStm) {
            DoWhileStm d = (DoWhileStm) s;
            traverseStm(depth, d.body);
            traverseExp(depth, d.condition);
        }

        else if (s instanceof SelectStm) {
            SelectStm sel = (SelectStm) s;
            traverseExp(depth, sel.expression);
            traverseStm(depth, sel.Stm1);
            traverseStm(depth, sel.Stm2);
        }

        else if (s instanceof ReturnStm) {
            ReturnStm r = (ReturnStm) s;
            traverseExp(depth, r.exp);
        }

        // Goto, break, continue, label, etc. don't contain expressions here,
        // so we don't need anything extra.
    }

    /* =========================================================
     * Expressions
     * ========================================================= */

    private void traverseExp(int depth, Exp e) {
        if (e == null) return;

        // Variable expression
        if (e instanceof VarExp) {
            traverseVar(depth, ((VarExp) e).var);
        }

        // Function call: arguments may escape; function is non-leaf.
        else if (e instanceof CallExp) {
            CallExp c = (CallExp) e;
            //System.out.println("Found CallExp: calling function");

            if (currentFunction != null) {
                
                currentFunction.leaf = false;
            }

            // Mark variable arguments as escaping
            for (ExpList a = c.args; a != null; a = a.tail) {
                markArgEscape(depth, a.head);
                traverseExp(depth, a.head);
            }

            // If the "func" is itself an expression (e.g. function pointer),
            // traverse it too.
            if (c.func != null) {
                traverseExp(depth, c.func);
            }
        }

        // Unary operator: handle address-of specially, then recurse.
        else if (e instanceof UnaryOpExp) {
            UnaryOpExp u = (UnaryOpExp) e;

            // &x  => x escapes because its address can be stored/used elsewhere.
            if (u.op == UnaryOpExp.BITWISEAND && u.exp instanceof VarExp) {
                Var v = ((VarExp) u.exp).var;
                if (v instanceof SimpleVar) {
                    SimpleVar sv = (SimpleVar) v;
                    EscapeEntry entry = (EscapeEntry) env.get(sv.name);
                    if (entry != null) entry.markEscape();
                }
            }

            traverseExp(depth, u.exp);
        }

        // Binary operator
        else if (e instanceof BinOpExp) {
            BinOpExp b = (BinOpExp) e;
            traverseExp(depth, b.left);
            traverseExp(depth, b.right);
        }

        // Assignment like x = e, x += e, etc.
        else if (e instanceof AssignExpA) {
            AssignExpA a = (AssignExpA) e;
            traverseExp(depth, a.left);
            traverseExp(depth, a.right);
        }

        // Other binary expressions (bitwise, +, -, etc.)
        else if (e instanceof OpExpA) {
            OpExpA o = (OpExpA) e;
            traverseExp(depth, o.left);
            traverseExp(depth, o.right);
        }

        else if (e instanceof AndExpA) {
            AndExpA a = (AndExpA) e;
            traverseExp(depth, a.left);
            traverseExp(depth, a.right);
        }

        // Ternary ?: expression
        else if (e instanceof CondExpA) {
            CondExpA c = (CondExpA) e;
            traverseExp(depth, c.condition);
            traverseExp(depth, c.thenExp);
            traverseExp(depth, c.elsExp);
        }

        // Comma expression list
        else if (e instanceof SeqExp) {
            SeqExp s = (SeqExp) e;
            for (ExpList l = s.list; l != null; l = l.tail) {
                traverseExp(depth, l.head);
            }
        }

        // Array element access: arr[i]
        else if (e instanceof ArrayAccessExp) {
            ArrayAccessExp a = (ArrayAccessExp) e;
            traverseExp(depth, a.array);
            traverseExp(depth, a.index);
        }

        // Struct field: s.x
        else if (e instanceof FieldAccessExp) {
            FieldAccessExp f = (FieldAccessExp) e;
            traverseExp(depth, f.record);
        }

        // Pointer field: p->x
        else if (e instanceof PointerAccessExp) {
            PointerAccessExp p = (PointerAccessExp) e;
            traverseExp(depth, p.pointer);
        }

        // Constants (int, char, string, etc.) have no subexpressions, so nothing to do.
    }

    /** If expression is a VarExp, mark that variable as escaping (used as argument). */
    private void markArgEscape(int depth, Exp e) {
        if (e instanceof VarExp) {
            Var v = ((VarExp) e).var;
            if (v instanceof SimpleVar) {
                SimpleVar sv = (SimpleVar) v;
                EscapeEntry entry = (EscapeEntry) env.get(sv.name);
                if (entry != null) {
                    entry.markEscape();
                }
            }
        }
    }

    /* =========================================================
     * Variables
     * ========================================================= */

    private void traverseVar(int depth, Var v) {
        if (v == null) return;

        if (v instanceof SimpleVar) {
            SimpleVar sv = (SimpleVar) v;
            EscapeEntry entry = (EscapeEntry) env.get(sv.name);
            if (entry != null && entry.depth < depth) {
                // Used from a deeper lexical depth than declared
                entry.markEscape();
            }
        }

        // No other Var subclasses are produced by your grammar; array/field
        // access are modeled as expressions, not Var nodes.
    }
}
