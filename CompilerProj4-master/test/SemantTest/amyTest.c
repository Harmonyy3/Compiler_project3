var int* gp;

fun void takesPtr(int* p) {
    return;
}

fun void setOut(int** out, int* value) {
    out = value;
    return;
}

fun int retAddr() {
    var int a = 1;
    return &a;     
}

fun void demo() {
    var int x = 10;
    var int y = 20;

    gp = &x;        
    takesPtr(&y);   
}