
/*Pass*/

struct myStruct { /*Pass*/
   int x;
   int y;
   int[2][2] z;
}

struct myStruct1 { /*Pass*/
   char x;
   int y;
}


fun int func(char* x, int y, myStruct ms, int[3][2] zarr) {
   var myStruct ms = {{1},{2},{{1,2},{1,2}}};
   var int[3][2] arrz = {{1, 2}, {1, 2}, {1, 2}};
   /*var char* = func(1, 2, ms, arrz); --- No Recursion*/  
   return arrz[2][1];
}

fun int main() {
   var int x;
   var int y;
   var int[3][2] arrz = {{1, 2}, {1, 2}, {1, 2}};
   var myStruct ms = {{1},{2},{{1,2},{1,2}}};
   var myStruct1 ms1 = {'\n', 2};
   var myStruct1 m2 = ms; /*FAIL*/
   var char* c = func(x, y, ms, arrz);
   /*var int pointerNum = c;*/
   return 0;
}


