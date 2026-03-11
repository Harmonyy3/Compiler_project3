struct S { int a; int b; } 

fun void foo() {
        /* do nothing */
    }

fun int main() {
    /*Declarations */
    var int x = 0;

    var char c = 'c';

    var int[3] arr = {1,2,3};

    var S s = {1,2};

    var int i = 0;

    var int y = 0;

    var int z;

    var int t = 0;

    var int b = 0;

    var int m = 0;

    var int n = 0;

    var int p = 0;
    var int q = 0;

    var int[2] arr2 = {1,2};
    var int k = 0;

    var int r = 0;

    var int u = 0;
    var int[2] arr3 = {1,2};
    var int v;
    /**********************
     * BASIC PASS CASE
     **********************/
    
    while (x < 50) {
        x++;
    }


    /**********************
     * 1. NON-INT CONDITIONS (FAIL)
     **********************/
    
    while (c) { }                   /* FAIL */

    
    while (arr) { }                 /* FAIL */

    
    while (s) { }                   /* FAIL */


    /**********************
     * 2. EMPTY BODY (PASS depending on language)
     **********************/
    
    while (i < 3) ;                   /* PASS or WARN */
    while (i < 3) { }                 /* PASS */


    /**********************
     * 3. ASSIGNMENT IN CONDITION (FAIL unless allowed)
     **********************/
    
    /* while ((y = y + 1) < 10) { }    /* FAIL or PASS depending on rules */


    /**********************
     * 4. UNINITIALIZED VARIABLE IN CONDITION (FAIL)
     **********************/
    
    while (z < 10) { z++; }         /* FAIL */


    /**********************
     * 5. INFINITE LOOP (PASS)
     **********************/
    
    while (1) {
        t = t + 1;
        if (t == 5) break;
    }


    /**********************
     * 6. BREAK / CONTINUE CHECKS
     **********************/
    
    while (b < 10) {
        if (b == 3) break;
        if (b == 2) continue;
        b++;
    }

    break;                          /* FAIL */
    continue;                       /* FAIL */


    /**********************
     * 7. NESTED LOOPS (PASS)
     **********************/
    
    while (m < 3) {
        n = 0;
        while (n < 2) {
            n++;
        }
        m++;
    }


    /**********************
     * 8. INVALID USE OF LOGICAL OPS (FAIL)
     **********************/
    
    while ((p < 10) && (q = 5)) { } /* FAIL */


    /**********************
     * 9. BAD TYPES IN BODY (FAIL)
     **********************/
    
    while (k < 2) {
        /* k = arr2;                   /* FAIL */
        k++;
    }


    /**********************
     * 10. BAD FUNCTION CALL IN BODY (FAIL)
     **********************/
    

    
    while (r < 2) {
        var int invalid = foo();    /* FAIL */
        r++;
    }


    /**********************
     * 11. COMPLEX STRESS TEST (MULTIPLE FAILS)
     **********************/
    
    while (u < arr3[0]) {
        if (u == 1) break;
        if (arr3) u++;              /* FAIL */
        while (v) { v = v + 1; }    /* FAIL: v is uninitialized */
        u = u + 1;
    }

    while (u = 100) { }             /* FAIL - assignment used as condition */


    return 0;
}
