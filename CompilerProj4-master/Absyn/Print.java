package Absyn;

import java.io.PrintWriter;

public class Print {
    private PrintWriter out;

    public Print(PrintWriter w) {
        this.out = w;
    }

    private void indent(int d) {
        for (int i = 0; i < d; i++)
            out.print("  ");
    }

    public void prExp(Exp e, int d) {
        if (e == null) {
            indent(d);
            //out.println("null");
            return;
        }

        if (e instanceof VarExp) prExp((VarExp)e, d);
        else if (e instanceof CharConstExp) prExp((CharConstExp)e, d);
        else if (e instanceof IntConstExp) prExp((IntConstExp)e, d);
        else if (e instanceof StringExp) prExp((StringExp)e, d);
        else if (e instanceof BinOpExp) prExp((BinOpExp)e, d);
        else if (e instanceof UnaryOpExp) prExp((UnaryOpExp)e, d);
        else if (e instanceof SizeofExp) prExp((SizeofExp)e, d);
        else if (e instanceof SizeofTypeExp) prExp((SizeofTypeExp)e, d);
        else if (e instanceof CastExp) prExp((CastExp)e, d);
        else if (e instanceof ArrayAccessExp) prExp((ArrayAccessExp)e, d);
        else if (e instanceof CallExp) prExp((CallExp)e, d);
        else if (e instanceof FieldAccessExp) prExp((FieldAccessExp)e, d);
        else if (e instanceof PointerAccessExp) prExp((PointerAccessExp)e, d);
        else if (e instanceof SeqExp) prExp((SeqExp)e, d);
        else if (e instanceof OpExpA) prExp((OpExpA)e, d);
        else if (e instanceof AndExpA) prExp((AndExpA)e, d);
        else if (e instanceof OrExpA) prExp((OrExpA)e, d);
        else if (e instanceof CondExpA) prExp((CondExpA)e, d);
        else if (e instanceof AssignExpA) prExp((AssignExpA)e, d);
        else if (e instanceof Init) prDec((Init)e, d);
        else {
            indent(d);
            out.println("Unknown Exp: " + e.getClass().getSimpleName());
        }
    }

    private void prExp(VarExp e, int d) {
        indent(d);
        out.println("VarExp:");
        prVar(e.var, d + 1);
    }

    private void prVar(Var v, int d) {
        if (v == null) {
            indent(d);
            //out.println("null");
            return;
        }
        if (v instanceof SimpleVar) {
            indent(d);
            out.println("SimpleVar: " + ((SimpleVar)v).name);
        } else {
            indent(d);
            out.println("Unknown Var: " + v.getClass().getSimpleName());
        }
    }

    // private void prExp(ConstExp e, int d) {
    //     indent(d);
    //     if (e.value instanceof Integer)
    //         System.out.println("ConstExp: " + (Integer)e.value);
    //     else if (e.value instanceof Character)
    //         System.out.println("ConstExp: '" + (Character)e.value + "'");
    //     else
    //         System.out.println("ConstExp: " + e.value);
    // }

    private void prExp(IntConstExp e, int d) {
        indent(d);
        out.println("IntConstExp: " + e.value);
    }

    private void prExp(CharConstExp e, int d) {
        indent(d);
        out.println("CharConstExp: '" + e.value + "'");
    }

    private void prExp(StringExp e, int d) {
        indent(d);
        out.println("StringExp: \"" + e.value + "\"");
    }

    private void prExp(BinOpExp e, int d) {
        indent(d);
        out.println("BinOpExp: " + opToString(e.op));
        prExp(e.left, d + 1);
        prExp(e.right, d + 1);
    }

    private void prExp(UnaryOpExp e, int d) {
        indent(d);
        out.println("UnaryOpExp: " + unaryOpToString(e.op));
        prExp(e.exp, d + 1);
    }

    private void prExp(SizeofExp e, int d) {
        indent(d);
        out.println("SizeofExp: sizeof");
        prExp(e.expr, d + 1);  // fixed the field name
    }

    private void prExp(SizeofTypeExp e, int d) {
        indent(d);
        out.println("SizeofTypeExp: " + e.type.name);
    }

    private void prExp(CastExp e, int d) {
        indent(d);
        out.println("CastExp to " + e.type.name);
        prExp(e.exp, d + 1);
    }

    private void prExp(ArrayAccessExp e, int d) {
        indent(d);
        out.println("ArrayAccessExp:");
        prExp(e.array, d + 1);
        prExp(e.index, d + 1);
    }

    private void prExp(CallExp e, int d) {
    indent(d);
    out.println("CallExp:");
    indent(d + 1);
    out.println("Function:");
    prExp(e.func, d + 2);

    if (e.args != null) {
        indent(d + 1);
        out.println("Arguments:");
        prExpList(e.args, d + 2);
    } else {
        indent(d + 1);
        out.println("Arguments: none");
    }
}


    private void prExp(FieldAccessExp e, int d) {
        indent(d);
        out.println("FieldAccessExp: ." + e.field);
        prExp(e.record, d + 1);
    }

    private void prExp(PointerAccessExp e, int d) {
        indent(d);
        out.println("PointerAccessExp: ->" + e.field);
        prExp(e.pointer, d + 1);
    }

    private void prExp(SeqExp e, int d) {
        indent(d);
        out.println("SeqExp:");
        prExpList(e.list, d + 1);
    }

    private void prExp(OpExpA e, int d) {
        indent(d);
        String opStr = "?";

        switch (e.oper) {
            case OpExpA.PLUS:   opStr = "+"; break;
            case OpExpA.MINUS:  opStr = "-"; break;
            case OpExpA.MUL:    opStr = "*"; break;
            case OpExpA.DIV:    opStr = "/"; break;
            case OpExpA.EQ:     opStr = "=="; break;
            case OpExpA.NE:     opStr = "!="; break;
            case OpExpA.LT:     opStr = "<"; break;
            case OpExpA.LE:     opStr = "<="; break;
            case OpExpA.GT:     opStr = ">"; break;
            case OpExpA.GE:     opStr = ">="; break;
            case OpExpA.BITAND: opStr = "&"; break;
            case OpExpA.BITXOR: opStr = "^"; break;
            case OpExpA.BITOR:  opStr = "|"; break;
            case OpExpA.PREINC: opStr = "++"; break;
            case OpExpA.PREDEC: opStr = "--"; break;
        }

        out.println("OpExp: " + opStr);

        if (e.left != null) {
            prExp(e.left, d + 1);
        }
        if (e.right != null) {
            prExp(e.right, d + 1);
        }
    }

    private void prExp(AndExpA e, int d) {
        indent(d);
        out.println("AndExp: &&");  // print the operator

        if (e.left != null) {
            prExp(e.left, d + 1);   // recursively print left expression
        }
        if (e.right != null) {
            prExp(e.right, d + 1);  // recursively print right expression
        }
    }

    private void prExp(OrExpA e, int d) {
        indent(d);
        out.println("AndExp: ||");  // print the operator

        if (e.left != null) {
            prExp(e.left, d + 1);   // recursively print left expression
        }
        if (e.right != null) {
            prExp(e.right, d + 1);  // recursively print right expression
        }
    }


    private void prExp(CondExpA e, int d) {
        indent(d);
        out.println("CondExp: ? :");

        indent(d + 1);
        out.println("Condition:");
        prExp(e.condition, d + 2);

        indent(d + 1);
        out.println("Then:");
        prExp(e.thenExp, d + 2);

        indent(d + 1);
        out.println("Else:");
        prExp(e.elsExp, d + 2);
    }

    private void prExp(AssignExpA e, int d) {
        indent(d);

        String opStr;
        switch (e.oper) {
            case AssignExpA.ASSIGN:     opStr = "="; break;
            case AssignExpA.PASSIGN:    opStr = "+="; break;
            case AssignExpA.MASSIGN:    opStr = "-="; break;
            case AssignExpA.MULASSIGN:  opStr = "*="; break;
            case AssignExpA.DIVASSIGN:  opStr = "/="; break;
            case AssignExpA.MODASSIGN:  opStr = "%="; break;
            case AssignExpA.BANDASSIGN: opStr = "&="; break;
            case AssignExpA.XORASSIGN:  opStr = "^="; break;
            case AssignExpA.LSASSIGN:   opStr = "<<="; break;
            case AssignExpA.RSASSIGN:   opStr = ">>="; break;
            case AssignExpA.ORASSIGN:   opStr = "|="; break;
            default:                    opStr = "?="; break;
        }

        out.println("AssignExp: " + opStr);

        if (e.left != null) {
            prExp(e.left, d + 1);
        }

        if (e.right != null) {
            prExp(e.right, d + 1);
        }
    }



    private void prExpList(ExpList list, int d) {
        while (list != null) {
            prExp(list.head, d + 1);
            list = list.tail;
        }
    }


    

    private String opToString(int op) {
        switch (op) {
            case BinOpExp.PLUS: return "+";
            case BinOpExp.MINUS: return "-";
            case BinOpExp.TIMES: return "*";
            case BinOpExp.DIVIDE: return "/";
            case BinOpExp.MODULUS: return "%";
            case BinOpExp.LT: return "<";
            case BinOpExp.LEQ: return "<=";
            case BinOpExp.GT: return ">";
            case BinOpExp.GEQ: return ">=";
            case BinOpExp.EQ: return "==";
            case BinOpExp.NEQ: return "!=";
            case BinOpExp.LSHIFT: return "<<";
            case BinOpExp.RSHIFT: return ">>";
            default: return "op(" + op + ")";
        }
    }

    private String unaryOpToString(int op) {
        switch (op) {
            case UnaryOpExp.PLUS: return "+";
            case UnaryOpExp.MINUS: return "-";
            case UnaryOpExp.NOT: return "!";
            case UnaryOpExp.TILDE: return "~";
            case UnaryOpExp.BITWISEAND: return "&";
            case UnaryOpExp.TIMES: return "*";
            case UnaryOpExp.POSTINC: return "post++";
            case UnaryOpExp.POSTDEC: return "post--";
            case UnaryOpExp.PREINC: return "++pre";
            case UnaryOpExp.PREDEC: return "--pre";
            default: return "unop(" + op + ")";
        }
    }


        /* ===========================
       STATEMENT PRINT FUNCTIONS
       =========================== */

    public void prStm(Stm s, int d) {
        if (s == null) {
            indent(d);
            //out.println("null");
            return;
        }

        if (s instanceof Expstm) prStm((Expstm) s, d);
        else if (s instanceof CompoundStm) prStm((CompoundStm) s, d);
        else if (s instanceof SelectStm) prStm((SelectStm) s, d);
        else if (s instanceof WhileStm) prStm((WhileStm) s, d);
        else if (s instanceof DoWhileStm) prStm((DoWhileStm) s, d);
        else if (s instanceof ForStm) prStm((ForStm) s, d);
        else if (s instanceof BreakStm) prStm((BreakStm) s, d);
        else if (s instanceof ContinueStm) prStm((ContinueStm) s, d);
        else if (s instanceof ReturnStm) prStm((ReturnStm) s, d);
        else if (s instanceof LabelStm) prStm((LabelStm) s, d);
        else if (s instanceof CaseStm) prStm((CaseStm) s, d);
        else if (s instanceof DefaultStm) prStm((DefaultStm) s, d);
        else if (s instanceof SwitchStm) prStm((SwitchStm) s, d);
        else if (s instanceof GotoStm) prStm((GotoStm) s, d);
        else {
            indent(d);
            out.println("Unknown Stm: " + s.getClass().getSimpleName());
        }
    }

    private void prStm(Expstm s, int d) {
        indent(d);
        out.println("ExpStm:");
        if (s.expression != null)
            prExp(s.expression, d + 1);
        else {
            indent(d + 1);
            out.println("(empty)");
        }
    }

    private void prStm(CompoundStm s, int d) {
        indent(d);
        out.println("CompoundStm:");
        if (s.decls != null) {
            indent(d + 1);
            out.println("Declarations:");
            prDecList(s.decls, d + 2);
        }
        if (s.stms != null) {
            indent(d + 1);
            out.println("Statements:");
            prStmList(s.stms, d + 2);
        }
    }

    private void prStm(SelectStm s, int d) {
        indent(d);
        out.println("IfStm:");
        indent(d + 1);
        out.println("Condition:");
        prExp(s.expression, d + 2);
        indent(d + 1);
        out.println("Then:");
        prStm(s.Stm1, d + 2);
        if (s.Stm2 != null) {
            indent(d + 1);
            out.println("Else:");
            prStm(s.Stm2, d + 2);
        }
    }


    private void prStm(WhileStm s, int d) {
        indent(d);
        out.println("WhileStm:");
        indent(d + 1);
        out.println("Condition:");
        prExp(s.test, d + 2);
        // indent(d + 1);
        // out.println("Body:");
        prStm(s.body, d + 2);
    }

    private void prStm(DoWhileStm s, int d) {
        indent(d);
        out.println("DoWhileStm:");
        // indent(d + 1);
        // out.println("Body:");
        prStm(s.body, d + 2);
        indent(d + 1);
        out.println("Condition:");
        prExp(s.condition, d + 2);
    }

    private void prStm(ForStm s, int d) {
        indent(d);
        out.println("ForStm:");
        indent(d + 1);
        out.println("Init:");
        prExp(s.init, d + 2);
        indent(d + 1);
        out.println("Cond:");
        prExp(s.condition, d + 2);
        indent(d + 1);
        out.println("Update:");
        prExp(s.increment, d + 2);
        // indent(d + 1);
        // out.println("Body:");
        prStm(s.body, d + 2);
    }

    private void prStm(ReturnStm s, int d) {
        indent(d);
        out.println("ReturnStm:");
        if (s.exp != null)
            prExp(s.exp, d + 1);
        else {
            indent(d + 1);
            out.println("(void)");
        }
    }

    private void prStm(BreakStm s, int d) {
        indent(d);
        out.println("BreakStm;");
    }

    private void prStm(ContinueStm s, int d) {
        indent(d);
        out.println("ContinueStm;");
    }

    private void prStm(GotoStm s, int d) {
        indent(d);
        out.println("GotoStm: " + s.label);
    }

    private void prStm(LabelStm s, int d) {
        indent(d);
        out.println("LabelStm: " + s.label);
        prStm(s.stm, d + 1);
    }

    private void prStm(CaseStm s, int d) {
        indent(d);
        out.println("CaseStm:");
        indent(d + 1);
        out.println("Expression:");
        prExp(s.exp, d + 2);
        indent(d + 1);
        out.println("Statement:");
        prStm(s.body, d + 2);
    }

    private void prStm(DefaultStm s, int d) {
        indent(d);
        out.println("DefaultStm:");
        prStm(s.stm, d + 1);
    }

    private void prStm(SwitchStm s, int d) {
        indent(d);
        out.println("SwitchStm:");
        indent(d + 1);
        out.println("Expression:");
        prExp(s.exp, d + 2);
        // indent(d + 1);
        // out.println("Body:");
        prStm(s.body, d + 2);
    }

    private void prStmList(StmList list, int d) {
        while (list != null) {
            prStm(list.head, d);
            list = list.tail;
        }
    }

    public void prDecList(DecList list, int d) {
        while (list != null) {
            prDec(list.head, d);  // call prDec instead of printing the object
            list = list.tail;
        }
    }


    /* ===========================
   DECLARATION PRINT FUNCTIONS
   =========================== */
    public void prDec(Dec d, int depth) {
        if (d == null) {
            indent(depth);
            //out.println("null");
            return;
        }

        if (d instanceof FunctionDec) prDec((FunctionDec)d, depth);
        else if (d instanceof VarDec) prDec((VarDec)d, depth);
        else if (d instanceof TypeDec) prDec((TypeDec)d, depth);
        else if (d instanceof EnumDec) prDec((EnumDec)d, depth);
        else if (d instanceof StructOrUnionDec) prDec((StructOrUnionDec)d, depth);
        else if (d instanceof TypeParam) prDec((TypeParam)d, depth);
        else if (d instanceof Param) prDec((Param)d, depth);
        else if (d instanceof bflist) prDec((bflist)d, depth);
        //else if (d instanceof bfval) prDec((bfval)d, depth);
        else {
            indent(depth);
            out.println("Unknown Dec: " + d.getClass().getSimpleName());
        }
    }


    private void prDec(FunctionDec d, int depth) {
        indent(depth);
        out.println("FunctionDec: " + d.name);

        if (d.params != null) {
            indent(depth + 1);
            out.println("Parameters:");
            prBfList(d.bflist, depth + 2);
        }

        if(d.result != null) {
            indent(depth + 1);
            out.println("Return Type:");
            prDec(d.result, depth + 2);
        }

        
        if (d.params != null) {
            prDec(d.params, depth + 1);  
        }

        // Return expression
        if (d.body != null) {
            prStm(d.body, depth + 1);  // directly print
        }

        // Next declaration (e.g., in a declaration list)
        // if (d.next != null) {
        //     prDec(d.next, depth + 1);
        // }
    }

    private void prDec(VarDec d, int depth) {
        indent(depth);
        out.println("VarDec: " + d.name);

        if (d.params != null) {
            indent(depth + 1);
            out.println("Parameters:");
            prBfList(d.params, depth + 2);
        }
        if (d.typ != null) {
            indent(depth + 1);
            out.println("TypeDec:");
            prDec(d.typ, depth + 2);
        }

        if (d.init != null) {
            indent(depth + 1);
            out.println("Initialization:");
            prExp(d.init, depth + 2);
        }
    }

    private void prDec(Type d, int depth) {
        indent(depth);
        out.println("Type: " + d.typeName);
    }

    private void prDec(Init d, int depth) {
        indent(depth);
        out.println("Init:");
        if (d.exp != null) {
            prExp(d.exp, depth + 1);
        }
    }

    private void prDec(Param d, int depth) {
        // indent(depth);
        // out.println("Param: " + d.name);
        // if (d.typ != null) {
        //     indent(depth + 1);
        //     out.println("Type:");
        //     prDec(d.typ, depth + 2);
        // }
    }

    private void prDec(TypeDec d, int depth) {
        indent(depth);
        out.println("TypeDec (typedef): " + d.name);

        if (d.ty != null) {
            indent(depth + 1);
            out.println("Aliased Type:");
            prDec(d.ty, depth + 2);
        }
    }

    private void prDec(EnumDec d, int depth) {
        indent(depth);
        out.println("EnumDec: " + d.name);

        if (d.enumerators != null) {
            indent(depth + 1);
            out.println("Enumerators:");
            prEnumList(d.enumerators, depth + 2);
        }
    }

    private void prEnumList(EnumList list, int depth) {
        while (list != null) {
            if (list.head != null) {
                indent(depth);
                if (list.head instanceof Enum) {
                    Enum e = (Enum) list.head;
                    out.print("Enum: " + e.name);
                    if (e.value != null) {
                        out.print(" = ");
                        prExp(e.value, depth + 1);
                    } else {
                        out.println();
                    }
                    
                } else {
                    out.println("Unknown Enum type: " + list.head.getClass().getSimpleName());
                }
            }
            list = list.tail;
        }
    }


    private void prDec(StructOrUnionDec d, int depth) {
        indent(depth);

        // Determine whether this is a struct or union
        String kind = "StructOrUnionDec";
        if (d.strun != null) {
            String strunStr = d.strun.toString();
            if (strunStr.contains("union")) {
                kind = "UnionDec";
            } else if (strunStr.contains("struct")) {
                kind = "StructDec";
            }
        }

        out.println(kind + ": " + (d.name != null ? d.name.toString() : "anonymous"));

        // Print bitfields (if present)
        if (d.bitfields != null) {
            indent(depth + 1);
            out.println("Bitfields:");
            prBfList(d.bitfields, depth + 2);
        }

        // Print struct or union member declarations
        if (d.structOrUnion != null) {
            indent(depth + 1);
            out.println("Fields:");
            prStructDecList(d.structOrUnion, depth + 2);
        }
    }




    private void prStructDecList(StructDecList list, int depth) {
        if (list == null) return;

        // recursively print previous fields first
        prStructDecList(list.prev, depth);

        indent(depth);
        out.println("Field: " + list.name + ", Type: " + 
                    (list.type != null ? list.type.typeName : "null"));
    }




    private void prDec(TypeParam d, int depth) {
        indent(depth);
        // out.println("TypeParam:");

        if (d.paramList != null) {
            indent(depth + 1);
            out.println("Parameter Type List:");
            prParamTypeList(d.paramList, depth + 2);
        } else {
            // indent(depth + 1);
            // out.println("(empty)");
        }
    }

    private void prParamTypeList(ParamTypeList list, int depth) {
        while (list != null) {
            indent(depth);
            out.println("ParamType: " + list.head);
            list = list.head;
        }
    }




    private void prDec(bflist d, int depth) {
        if (d == null) {
            indent(depth);
            out.println("bflist: null");
            return;
        }

        indent(depth);
        out.println("bflist element:");

        // Print type (if any)
        if (d.typ != null) {
            indent(depth + 1);
            out.println("Type:");
            prDec(d.typ, depth + 2);
        }

        // Print name (if present)
        if (d.name != null) {
            indent(depth + 1);
            out.println("Name: " + d.name.toString());
        }

        // Print escape flag (optional, but useful for debugging)
        indent(depth + 1);
        out.println("Escape: " + d.escape);

        // Recurse on tail (linked list)
        if (d.tail != null) {
            indent(depth + 1);
            out.println("Next bflist:");
            prDec(d.tail, depth + 2);
        }
    }

    private void prBfList(bflist list, int depth) {
        if (list == null) return;

        // recursively print previous values first
        prBfList(list.tail, depth);

        indent(depth);
        out.println("Bitfield: " + (list.typ != null ? list.typ.value : "null"));
    }


    private void prBfVal(bfval d, int depth) {
        
    }






}
