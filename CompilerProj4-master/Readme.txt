Divisian of work:
    Konor Trosclair:
        Worked on semant.java particularly working with arrays, unions, structs, function declarations, function calls,
        cast expressions, var declarations, 

    Stella Levy:
        Worked on the other half of Semant.java which included majority of statements, TypeDef declarations and expressions, 
        EnumDec, and other opperation expressions.

    Amy Granados:
        Worked on escape analysis primarily working on the file FindEscape.java which navigates through the AST tree and determines which nodes are leaves.
        Also added stuff to Semant.Main.java, to navigate the AST tree before semantic analysis, as well as adding bolean escape variables whithin, 
        Absyn.bflist, Absyn.VarDec, and Absyn.FuncDec.

    Huarong Teng:
        Worked on Mips.MipsFrame.java which sorts variables by formals and locals while also deciding which locals escape.


Notable issues and behaviors With Semant.java:
    SizeofExp and SizeofTypeExp:
        This issue primarily shows up when working with defined types declared through unions/structs or typedef.
        when calling sizeof(int) this works and properly calls the SizeofTypeExp grammars. However if we have a (typedef int myInt;) and then call
        sizeof(myInt) this calls the sizeofExp and not sizeofTypeExp this results from the fact that both typeName and expression hold ID but since sizeofExp comes first
        it reduces it reading "(myInt)" as an expression. Not sure if this was something we had to worry about.

    Union Initialization:
        Unions can be initialized with a list but only accept one input (will return an error if more than one item in the list or if there are nested lists)
        if a union is properly initialized with a list it will match the items type with the first item in the union and set that to the active parameter.
        Was not sure if it should have been done this way or if we should have just rejected init lists as a whole.
        Because Unions do not accept nested lists it will return an error if you try to assign an array parameter with a init list.
        However assigning an already declared array whithin a init list will properly type check it if the first param is an array of same type and size.

    Pointer Assignments:
        If any variable is declared as a pointer semant allows interger assignments but if for example
        a variable is declared char* x = 'c'; will return an error because pointers are only set up to accept integers.
        This sounds to be the right behavior from your instructions from instructions.txt and an email that was sent.

    Strings:
        In this assignment strings are recognized as an array of char.
        For example the declaration (var char[] myString = "hello";) creates an array my string of size 5 and type char.
        It was does this way since in c strings are arrays of char. 

Other issues:
    FindEscape.java:
        FindEscape.java runs before type checking and should properly change the escape boolean within the proper absyn files.
        However, nothing prints to show what variables escape but there are commented out debug prints if that helps.
        We also were not entirely sure how to encorporate FindEscapes with the rest of the project.
    
    MipsFrame.java:
        Smilar to FindEscape.java MipsFrame does add and  distinguish between formals and locals but we were not entirely sure how to 
        encorporate it with the rest of the project. There is a call to MipsFrame in the function transFunDec whithin semant.java that adds all the 
        params as formals but there is nothing that prints to show which variables are formals and which ones are locals.

    
