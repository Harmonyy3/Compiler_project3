fun int bubbleSort(int[] arr, int n) {
    var int i;
    var int j;
    var int temp;

    i = 0;
    while (i < n - 1)
    {
        j = 0;
        while (j < n - i - 1)
        {
            if (arr[j] > arr[j + 1])
            {
                temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
            j = j + 1;
        }
        i = i + 1;
    }

    return 0;
}

fun int main() {
    var int[] array = {5, 1, 4, 2, 8};
    var int i;

    bubbleSort(array, 5);
    

    i = 0;
    while (i < 5)
    {
        array[i] = array[i];   /* print, but valid statement*/
        i = i + 1;
    }

    return 0;
}