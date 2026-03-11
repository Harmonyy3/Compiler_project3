/*Important notes
Functions rn do not handle arrays
for example the below code works 
because it has now way to check
if returnArr is not an array or not when returning*/

var int globalInt = 2;

fun int test() {
    
    var int a = 1;
    var int b = 2;
    var int[] returnArr = {a, b};
    globalInt = 4;
    return returnArr;
}

fun int main() {
    var const int c = 50;


    var int x = 100; 
    /* Array Tests*/
    /* var char[] arr0; fails cuz must be initialized with a list */
    var char[3] arr1 = {'5', '5', '5'}; 
    var char[3] arr = {'5', 5, 5};
    var char[3] arr = {5, '5', 5};
    var char[3] arr = {5, 5, '5'};
    var char[3] arr = {'5', '5', 5};
    var char[3] arr = {5, '5', '5'};
    var int[3] arr = {1, 2, '2'};
    var int[1][2][3][4] arr2 = {
  {
    {
      { x, x, x, x },
      { x, x, x, x },
      { x, x, x, x }
    },
    {
      { x, x, x, x },
      { x, x, x, x },
      { x, x, x, x }
    }
  }
};

    var int[9] arr3 = {1, 1, 3, 2, x, x, x, 0, 1893};
    var int[][][] arr4 = {{{1, 2}, 2, 3}, {4, 5, {5, 6}}};
    
    var char**** p1 = 89;
    var int** p2 = 'c';
    var char*******[] p = {2, 2, 3, 4, 5}; /*needs to include int and char?*/
    var int[3] arr5 = {1, 2, 3}; 
    /*var char[] string = "help were trapped in a semant test!";*/
    
    x = arr2[0][1][2][3]; /*needs to check if the pos is int or not and if the pos exists*/

    x = x + 6;

    

    return test();
}


