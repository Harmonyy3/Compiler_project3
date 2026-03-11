struct myStruct1 { /* PASS */
    int x; 
    char y;
}

struct myStruct2 { /* PASS */
    char[2][2] x;
    char y;
}

struct myStruct3 { /* PASS */
    int x;
    int y;
    int z;
}

struct myStruct4 {
    int[][] sarr;
    int[] sarr2;
    myStruct1 ms;
    
}


fun int main() {

    var myStruct2 s = {{{'a','b'},{'a','b'}}, s.y = 'c'}; /* IDK if we should reject assignment expressions the grammars say its ok (obv this should fail though since s has not been declared)*/
    var myStruct2 s = {{{'a','b'},{'a','b'}}, 'c'};  /* PASS */
    var myStruct1 s = {1, '2'}; /* PASS */
    var int z; /*Theese three should be set to something before but we can do math witht hem on line 36*/ /* PASS */
    var int y; /* PASS */
    var int x; /* PASS */
    var myStruct3 s33; /* PASS */
    var myStruct1 s1 = {1, 'a'}; /* PASS */
    var myStruct3 s3 = s1; /* FAIL */
    var myStruct3 st3 = {x + y, 3, x + y * z}; /*Doing math with x y and z before the are declared to something*/ /* FAIL (NOT A STRUCT ISSUE)*/

    var myStruct4 s4 = {{{1, 2, 3}, {1, 2, 3}}, {1, 2, 3, 4}, {1, 'c'}};
    
    s3 = s1;  /* FAIL */
    z = s.y; /* FAIL (Type check char top int)*/
   

    return 0;
}