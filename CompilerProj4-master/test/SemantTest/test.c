typedef int MyInt;

enum Color { RED, GREEN = 5, BLUE }

typedef int Index;

/* a struct typedef example (simple record) */
struct Point {
    int x;
    int y;
}

union Data {
    int i;
    float f;
    char c;
}

/* simple function: add two ints */
fun int add(int a, int b) {
    var int sum;
    sum = a + b;
    return sum;
}

/* function demonstrating pointer/array/initializer/cast/sizeof/ternary/etc. */
fun const int main() {
    /* local declarations: MUST BE AT TOP SINCE ANSI C89 ONLY ACCEPTS DECLARATIONS AT THE TOP */
    /* THIS IS A RESULT OF THE CompoundStatement -> DeclarationListOpt Grammar*/
    var const volatile int y = 10;
    var extern int z;
    var static char ch = 'A';
    var char[] str = "Test";

    var auto int *p;

    var register int w;

    var int mask = (y & 0xFF) ^ (z | 3);
    var int cond = ( (y > 0) && (z < 100) ) || (w == 0);

    var int i;
    /*var char s[] = "hello";        /* string literal exercise (STRING_LITERAL token) */

    /* array with brace initializer and nested usage */
    var int[3] arr = {1, 2, 3};
    var int[4] big = { 1, 2, (3 + 4), 5 };

    /* pointer usage */
    
    var int len;var int len;
    p = &arr[0];               /* address-of + assignment */
    *(p + 1) = 42;             /* deref + pointer arithmetic + assignment */

    /* compound assignment and pre/post inc/dec */
    arr[1] += 2;
    ++y;
    z = y++;
    z--;

    /* cast and sizeof */
    
    len = (int) sizeof(arr);   /* sizeof applied to expression (array) */
    len = (int) sizeof(int);   /* sizeof applied to type (if your grammar supports it) */

    /* conditional operator and comma operator */
    
    w = (y > 0) ? y : -y;
    w = ( (x = add(1,2)), x + 7 );  /* comma expression: call then compute */

    /* logical and bitwise ops */
    

    /* if/else */
    if (cond) {
        var int tmp = add(arr[0], arr[1]);
        tmp = tmp * 2;
    } else {
        tmp = add(1, 1); /* use of tmp here may be purposefully ill-scoped for parser testing */
    }

    /* for loop (uses expression grammar for init/cond/increment) */
    
    for (i = 0; i < 3; i = i + 1) {
        arr[i] = arr[i] * 2;
    }

    /* while loop */
    while (i > 0) {
        i = i - 1;
    }

    /* do-while loop */
    do {
        x = x + 1;
    } while (x < 5);

    /* function call and return */
    z = add(arr[0], arr[1]);

end_label:
    return z;
}

/* another function showing pointer to struct, field access and arrow (pointer) access */
fun int use_point(Point p, Point* pp) {
    var int sum;
    sum = p.x + pp->y;   /* field access and pointer->field */
    return sum;
}

/* function with ellipses (if grammar supports) and no params */
fun int varargs_fun() {
    return 0;
}

/* function declared with no body (prototype style) */
fun int proto_fun() ;

fun int max(int a, int b) {
    if (a > b) return a;
    else return b;
}
