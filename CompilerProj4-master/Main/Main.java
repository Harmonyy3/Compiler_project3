package Main;

import Parse.Parse;
import RegAlloc.RegAlloc;
import Semant.Semant;
import Temp.Temp;
import Temp.TempList;
import Temp.TempMap;
import Temp.Label;

import java.io.PrintWriter;

import Canon.*;
import Mips.*;
import FindEscape.FindEscape;
import Types.Type;
import Translate.Exp;
import Translate.Frag;
import Translate.ProcFrag;

import Temp.CombineMap;
import Temp.DefaultMap;


public class Main {
    static Assem.InstrList co0degen(Frame.Frame f, Tree.StmList stms) {
            Assem.InstrList first = null, last = null;
            for (Tree.StmList s = stms; s != null; s = s.tail) {
            Assem.InstrList i = f.codegen(s.head);
            if (last == null) {
            if (first != null)
            throw new Error("Main.codegen");
            first = last = i;
            } else {
            while (last.tail != null)
            last = last.tail;
            last = last.tail = i;
            }
            }

            if(first == null) {
                System.out.println("first is nulkl");
            }
            return first;
        }

    public static void main(String[] argv) throws java.io.IOException{
        
        
        Frame.Frame frame = new Mips.MipsFrame();

        for (int i = 0; i < argv.length; i++) {
            String src = argv[i];
            
            
            
            String dst = src.substring(0, src.lastIndexOf(".tig")) + ".s";
            java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileOutputStream(dst));
            java.io.PrintWriter debug = out;
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

            // 4) PRINT PROGRAM-LEVEL IR (optional)
            PrintWriter pw = new PrintWriter(System.out, true);
            Tree.Print print = new Tree.Print(pw);
            try {
                Tree.Stm programStm = result.unNx();
                System.out.println("=== IR for " + filename + " ===");
                print.prStm(programStm);
                System.out.println("\n=== End IR ===");
            } catch (Throwable t) {
                // ignore non-representable top-level results
            }

            // 5) Get fragments
            Frag frags = semant.translate.getResult();

            // 6) Emit assembly file
            try (PrintWriter writer = new PrintWriter("test.s")) {

                for (Frag f = frags; f != null; f = f.next) {

                    if (f instanceof ProcFrag) {
                        Assem.InstrList instrs = null;
                        ProcFrag pf = (ProcFrag) f;
                        TempMap tempmap = new CombineMap(pf.frame, new DefaultMap());
                        
                        // Canonicalize -> linearize -> basic blocks -> trace schedule
                        Tree.StmList linear = Canon.linearize(pf.body);
                        BasicBlocks blocks = new BasicBlocks(linear);
                        Tree.StmList traced = (new TraceSchedule(blocks)).stms;
                        TraceSchedule trace = new TraceSchedule(blocks);

                        instrs = co0degen(pf.frame, traced);
                        
                        Assem.InstrList after = pf.frame.procEntryExit2(instrs);
                        int idx = 0;
                        for (Assem.InstrList p = after; p != null; p = p.tail) {
    System.out.print("Instr " + idx++ + ": " + p.head.getClass().getSimpleName());
    if (p.head instanceof Assem.OPER) {
        Assem.OPER op = (Assem.OPER) p.head;

        System.out.print(" dst=");
        for (TempList t = op.dst; t != null; t = t.tail) {
            System.out.print("t" + t.head.num + " ");
        }

        System.out.print(" src=");
        for (TempList t = op.src; t != null; t = t.tail) {
            System.out.print("t" + t.head.num + " ");
        }
    } else if(p.head instanceof Assem.MOVE) {
        Assem.MOVE op = (Assem.MOVE) p.head;
        System.out.print(" dst=");
        Temp dt = op.dst;
        System.out.print("t" + dt.num + " ");

        System.out.print(" src=");
        Temp st = op.src;
        System.out.print("t" + st.num + " ");
        
    }
    System.out.println();
}



                        RegAlloc alloc = new RegAlloc(pf.frame, instrs);
                        //alloc.allocateRegister();
                        System.out.println("Starting alloc Show" );
                        //PrintWriter dbug = new PrintWriter("debug.s");
                        try (PrintWriter dbug = new PrintWriter("debug.s")) {
                            TempMap fakeAlloc = new TempMap() {
                                @Override
                                public String tempMap(Temp t) {
                                    int idx = t.num % 10; // simple round-robin allocation
                                    return "$t" + idx;
                                }
                            };

                            alloc.show(dbug, tempmap);
                            tempmap = new CombineMap(alloc, new DefaultMap());
                            dbug.write("After temp Realloc\n");
                            alloc.show(dbug, tempmap);
                        }
                        
                        Frame.Proc proc = pf.frame.procEntryExit3(alloc.instrs());
                        writer.println(proc.prologue);
                        for (Assem.InstrList p = proc.body; p != null; p = p.tail)
                            writer.println(p.head.format(tempmap));
                        writer.println(proc.epilogue);
                        
                    //     // Codegen
                    //     Assem.InstrList il = null;
                    //     Codegen cg = new Codegen((MipsFrame) pf.frame);
                    //     for (Tree.StmList s = trace.stms; s != null; s = s.tail) {
                    //         Assem.InstrList chunk = cg.codegen(s.head);
                    //         if (il == null)
                    //             il = chunk;
                    //         else {
                    //             Assem.InstrList last = il;
                    //             while (last.tail != null)
                    //                 last = last.tail;
                    //             last.tail = chunk;
                    //         }
                    //     }

                    //     // Add return sink / prologue/epilogue (procEntryExit2)
                    //     //il = pf.frame.procEntryExit2(il);

                    //     // Register allocation
                    //     if (il == null) {
                    //         System.err.println("ERROR: instruction list is null!");
                    //     }
                    //    // System.out.println("=== Before RegAlloc ===");
                    //     for (Assem.InstrList p = il; p != null; p = p.tail) {
                    //         System.out.println(p.head);
                    //     }

                    //         // After generating il via codegen and adding procEntryExit2
                    //         //RegAlloc alloc = new RegAlloc(pf.frame, il);
                    //         il = alloc.instrs(); // updated instructions with register allocation

                    //         //System.out.println("=== After RegAlloc ===");
                    //         // Use the allocator's tempMap to print the final code
                    //         TempMap tempMap = new TempMap() {
                    //             @Override
                    //             public String tempMap(Temp t) {
                    //                 //System.out.println("The Temp is = " + t.num );
                    //                 return alloc.tempMap(t); // <-- THIS ensures allocated registers are shown
                    //             }
                    //         };
                    //         for (Assem.InstrList p = il; p != null; p = p.tail) {
                    //             System.out.println(p.head.format(tempMap));
                    //         }

                    //         // Generate final proc with prologue/epilogue
                    //         Frame.Proc proc = pf.frame.procEntryExit3(il);

                    //         // Emit code
                    //         writer.println(proc.prologue);
                    //         for (Assem.InstrList p = il; p != null; p = p.tail)
                    //             writer.println(p.head.format(tempMap));
                    //         writer.println(proc.epilogue);
                    //         writer.println();


                    } else {
                        // Unknown fragment or data fragment
                        writer.println("# Unknown frag type: " + f.getClass().getName());
                    }
                }

                System.out.println("MIPS code written to test.s");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
