fun int testStuff(int x, int y, char c, char d) {
    var int temp;

    temp = x;
    x = y;
    y = temp;


    return x + y;
}




fun int main() {
    var int a = 2;
    var int b = 1;
    var int c;
    var char c1 = 'c';
    var char c2 = 'a';
    c = testStuff(a, b, c1, c2);
    return 0;
}