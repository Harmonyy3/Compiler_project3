struct test {
    char[] string;
}

union testU {
    char[] string;
}

fun int main() {
    var char[] myString = "hello";
    var char[5] otherTest;

    var test ms = {"help"};
    var testU mu = {"help"};

    ms.string = "hi";

    otherTest = "hi there";

    

    return 0;
}