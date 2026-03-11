fun int bubbleSort(int[] arr, int n) {
    var int i;
    var int j;
    var int temp;

    i = "hello";         /* ERROR: assigning string to int */

    while (i < n - 1)
    {
        j = arr;         /* ERROR: assigning array to int */

        while (j < n - i - 1)
        {
            if (arr[j] > arr[j + "x"])   /* ERROR: index not int */
            {
                temp = arr[j] + arr;     /* ERROR: adding int + array */

                /*arr[j] = 3.14;           /* ERROR: float assigned to int */

                arr[j + 1] = temp;
            }
            j = j + arr;                 /* ERROR: adding int + array */
        }
        i = i + true;                    /* ERROR: adding int + bool */
    }

    return arr;                           /* ERROR: returning array to int */
}

fun int main() {
    var int[5] arr = {5, "x", 4, 2, 8};    /* ERROR: string inside int array */
    var int i;

    bubbleSort(3, arr);                   /* ERROR: parameter types reversed */
    bubbleSort(arr, "5");                 /* ERROR: string instead of int */

    i = 0;
    while (i < "5")                       /* ERROR: comparing int < string */
    {
        arr[i] = arr;                     /* ERROR: assigning array to int */
        i = i + 1;
    }

    return arr;                           /* ERROR: wrong return type */
}
