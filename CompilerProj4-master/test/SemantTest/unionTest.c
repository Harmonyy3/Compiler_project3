struct myStruct1 { /* PASS */
    int x; 
    char y;
}

struct myStruct2 { /* PASS */
    int x; 
    int y;
}

union myUnion1 { /* PASS */
    int x;
    int y;
    int z;
}

union myUnion2 { /* PASS */
    int[2][4] x;
    char y;
}

union myUnion3 { /* PASS */
    char x;
    int y;
}

union myUnion4 {
    int[][] sarr;
    int[] sarr2;
    myStruct1 ms;
}


fun int main() {
    var int[][] Uarr = {{1, 2, 3, 4}, {1, 2, 3, 4}}; /* PASS */ 
    var int x = 5; /* PASS */
    var int y = 7; /* PASS */
    var myUnion1 s1; /* PASS */
    var myUnion2 s2 = Uarr; /* FAIL */
    var myUnion2 s22; /* PASS */
    var myUnion3 s33 = s22; /* FAIL */
    var myUnion3 s333 = s33; /* should fail? */
    var myUnion1 s11 = {5}; /*PASS*/
    var myUnion1 s11 = {5, 6}; /*Fail*/
    var myUnion3 s3  = { s3.x = '5' };  /* FAIL */
    var myUnion3 s3  = 5;  /* FAIL */
    var int[2][4] arr = {{1, 2, 3, 4}, {1, 2, 3, 4}}; /* PASS */
    var char[] chArr = {'h', 'e', 'l', 'p'};

    var myUnion4 u4 ={Uarr};
    var myStruct1 ss1 = {1, 'c'};
    var myStruct2 ss2 = {1, 1};

    u4.sarr = Uarr;

    u4.sarr2 = Uarr;
    u4.sarr2 = chArr;

    u4.ms = ss1;
    u4.ms = ss2;

    s2.x = arr; /* PASS */
    s2.y = 'x'; /* PASS */
    s1.x; 
    s1.y = 5; /* PASS */
    s2 = s22; /* should fail? */

    s1.y = s2.x[1][3]; /* PASS */

    return 0;
}