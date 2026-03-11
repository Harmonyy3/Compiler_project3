fun int main() {
    var int x = 100; /*PASS*/
    var char c = 'c'; /*PASS*/
    var int[][] intArr1 = {{1, 2, 2, 2}, {2, 2,  2, 3}, {2, 2,  2, 3}, {2, 2, 2, 2}}; /*PASS*/
    var int[][] intArr1 = {{1, 2, 2, 2}, {{2, 2},  2, 3}, {2, 2,  {2, 3}}, {{2, 2}, {2, 2}}}; /*FAIL 5 - 8*/
    var int[][] intArr2 = {{{2, 2}, {2, 2}}, {1, 2, 2, 2}, {{2, 2},  2, 3}, {2, 2,  {2, 3}}};   
    var int[][] intArr3 = {{2, 2,  {2, 3}}, {{2, 2}, {2, 2}}, {1, 2, 2, 2}, {{2, 2},  2, 3}};   
    var int[][] intArr4 = {{{2, 2},  2, 3}, {2, 2,  {2, 3}}, {{2, 2}, {2, 2}}, {1, 2, 2, 2}};   
    var char[3] arr1 = {'5', '5', '5'};  /*PASS*/
    var char[3] badarr1 = {'5', 5, 5}; /*FAIL*/
    var char[3] badarr2 = {5, '5', 5}; /*FAIL*/
    var char[3] badarr3 = {5, 5, '5'}; /*FAIL*/
    var char[3] badarr4 = {'5', '5', 5}; /*FAIL*/
    var char[3] badarr5 = {5, '5', '5'}; /*FAIL*/
    var int[3] badarr6 = {1, 2, '2'}; /*FAIL*/
    var int[2][3] arr2D = {{c, '2', 3}, {1, 2, '3'}};  /*FAIL 3x*/
    var int[2][2][3][4] arr2 = { /*FAIL 10x*/ 
        {
            {
            { x,'c', x, x},
            { x, x, c, x },
            { x, x, x, x }
            },
            {
            { x, c, x, x },
            { x, x, x, c },
            { x, c, x, x }
            }
        }, 
        {
            {
            { x,'c', x, x},
            { x, x, c, x },
            { x, x, x, x }
            },
            {
            { x, c, x, x },
            { x, x, x, c },
            { x, c, x, x }
            }
        }
    };

    var int[9] arr3 = {1, 1, 3, 2, x, x, x, 0, 1893}; /*PASS*/
    var int[][][] arr4 = {{{1, 2}, 2, 3}, {{4, 5}, 5, 6}}; /*should FAIL since iner mos tlist are not all the same */
    var int[][][] test = {{{1, 2}, {2}, {3}}, {{1}, {2}, {3}}, {{1}, {2}, {3}}}; /*FAIL*/
    var int[][] array2 = {{5, 1}, {2, 8, 7}};/*FAIL*/
    var int[][] array3 = {{5, 1, 2}, {2, 8, 7}};/*PASS*/

    var int[3] arr5 = {1, 2, 3}; /*PASS*/
    
    x = arr2[0][1][2][3]; /*PASS needs to check if the pos is int or not and if the pos exists*/
    x = array3[2][3]; /*FAIL*/
    x = array3[1][2]; /*PASS*/
    x = arr2[2][2][3][4]; /*FAIL*/

    arr1[0] = 'c';  /*PASS*/
    badarr1[1] = 1;/*FAIL*/
    badarr2[2] = 1; /*FAIL*/
    badarr3[1] = 1; /*FAIL*/
    badarr4[0] = 1; /*FAIL*/
    badarr5[1] = 1; /*FAIL*/
    badarr6[2] = 1; /*PASS*/

    return 0;
}