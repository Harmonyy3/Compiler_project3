

struct myStruct1 { /* PASS */
    int x; 
    char y;
}

struct myStruct2 { /* PASS */
    int x; 
    char y;
}

union myUnion1 {
    int x;
    char y;
}

struct myStruct1 { /* PASS */
    int x; 
    char y;
}

struct myStruct2 { /* PASS */
    int x; 
    char y;
}

union myUnion1 {
    int x;
    char y;
}

var int outside = 5;

typedef int myInt;



fun int spec(int x,  char* c, char**[][] cArr, int[] iArr, myStruct1 ms, myUnion1 mu) {
    var int SpecInt = 0;   
    var int[][] irr = {{5, 3, 2, 4}, {1, 2, 3, 4}};
    var char[][] crr = {{'5', '3', '2', '4'}, {'1', '2', '3', '4'}};
    var int x = 5; 
    typedef int myInt;
    typedef int myInt;
    x = 5;
    c = 5;
    cArr = 5;
    cArr = irr;
    

    return 0;
}

fun int main(){
    var myStruct1 ms1 = {1, 'c'};
    var int x = 0;
    var int outside = 0;
    x = 0;

    while(x == 0) {
        var int x;
        var int outside = 0;
    }
    


    return ms1.x;
}