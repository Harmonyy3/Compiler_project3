// package Mips;

// import Temp.*;
// import Frame.*;
// import Util.*;
// import Symbol.Symbol;
// //test for MipsFrame
// public class MipsFrameTest {
//         public static void main(String[] args) {
//         Symbol funcName = Symbol.symbol("testFunc");
//         BoolList formals = new BoolList(true, new BoolList(false, null)); 

//         MipsFrame frame = new MipsFrame(funcName, formals);

//         System.out.println("Frame created for: " + funcName);
//         System.out.println("Formals:");
//         AccessList fl = frame.formals;
//         int i = 1;
//         while (fl != null) {
//             System.out.println("  formal " + i + " → " + fl.head);
//             fl = fl.tail;
//             i++;
//         }

//         InFrame f1 = (InFrame) frame.formals.head;
//         System.out.println("Formal 1 offset = " + f1.offset);


//         // Allocate locals
//         Access local1 = frame.allocLocal(true);  
//         Access local2 = frame.allocLocal(false); 

//         System.out.println("\nLocals:");
//         System.out.println("  local1 (escapes): " + local1);
//         System.out.println("  local2 (non-escaping): " + local2);
//     }

// }
