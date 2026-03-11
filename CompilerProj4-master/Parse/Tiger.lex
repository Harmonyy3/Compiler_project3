package Parse;
import ErrorMsg.ErrorMsg;

%% 

%implements Lexer
%function nextToken
%type java_cup.runtime.Symbol
%char

%{
private void newline() {
  errorMsg.newline(yychar);
}

private void err(int pos, String s) {
  errorMsg.error(pos,s);
}

private void err(String s) {
  err(yychar,s);
}

private java_cup.runtime.Symbol tok(int kind) {
    return tok(kind, null);
}

private java_cup.runtime.Symbol tok(int kind, Object value) {
    return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}

private ErrorMsg errorMsg;
private int nested = 0;

Yylex(java.io.InputStream s, ErrorMsg e) {
  this(s);
  errorMsg=e;
}

StringBuffer s= new StringBuffer();
java_cup.runtime.Symbol tok = null;

%}

%{
  private int comment_count = 0;
%} 

%{
    private StringBuilder sb = new StringBuilder();
    private char cb;

    private boolean isError = false;

    private int hexToDec;
    private int octToDec;
%}

%state CHAR, ESCAPE_CHAR, HEX_CHAR
%state STRING, ESCAPE_STRING, HEX
%state HEX_DEC, HEX_ERROR
%state OCT_DEC, OCT_ERROR
%state COMMENT

%eofval{
    if (yy_lexical_state == STRING || yy_lexical_state == ESCAPE_STRING) {           // currently inside string
        //System.out.println("END OF FILE");
        err("Unterminated string literal at EOF");
        yybegin(YYINITIAL);
        return tok(sym.error, null);
    }
    if (yy_lexical_state == CHAR || yy_lexical_state == ESCAPE_CHAR) {           // currently inside string
        //System.out.println("END OF FILE");
        err("Unterminated char literal at EOF");
        yybegin(YYINITIAL);
        return tok(sym.error, null);
    }
    return tok(sym.EOF, null);                  // normal EOF
%eofval}



DIGITS = [0-9]+
HEX = 0[xX][0-9a-fA-F]+
OCT = 0[0-7]+
ALPHA = [a-zA-Z_\$][a-zA-Z0-9_\$]*
CHAR = \'([^\"\\\n]|\\.)*\'
STRING = \"([^\"\\\n]|\\.)*\"
UNTERMINATED = \"([^\"\\\n]|\\.)*
BLOCK_COMMENT = "/*"([^*]|\*[^/])*"*/"
LINE_COMMENT = "//"[^\n]*

SINGLE_CHAR = [\+\-\*\/%=\.<>\(\)\{\}\[\];:,!\?\&\|\^\~]





%%
<YYINITIAL> " "|\t	{}
<YYINITIAL> \n	{newline();}
<YYINITIAL> ","	{return tok(sym.COMMA);}
<YYINITIAL> short {return tok(sym.SHORT);}

<YYINITIAL> "short"          {return tok(sym.SHORT);}

<YYINITIAL> "*"              { return tok(sym.TIMES); }
<YYINITIAL> ">"              { return tok(sym.GT); }
<YYINITIAL> "->"             { return tok(sym.ARROW); }
<YYINITIAL> "/="             { return tok(sym.DIVASSIGN); }
<YYINITIAL> "const"          { return tok(sym.CONST); }
<YYINITIAL> ">="             { return tok(sym.GE); }
<YYINITIAL> "register"       { return tok(sym.REGISTER); }
<YYINITIAL> "enum"           { return tok(sym.ENUM); }
<YYINITIAL> "]"              { return tok(sym.RBRACK); }
<YYINITIAL> "sizeof"         { return tok(sym.SIZEOF); }
<YYINITIAL> "}"              { return tok(sym.RBRACE); }
<YYINITIAL> ")"              { return tok(sym.RPAREN); }
<YYINITIAL> "["              { return tok(sym.LBRACK); }
<YYINITIAL> "<"              { return tok(sym.LT); }
<YYINITIAL> "++"             { return tok(sym.INCREMENT); }
<YYINITIAL> "-="             { return tok(sym.SUBASSIGN); }
<YYINITIAL> "double"         { return tok(sym.DOUBLE); }
<YYINITIAL> "&="             { return tok(sym.BWISEANDASSIGN); }
<YYINITIAL> "struct"         { return tok(sym.STRUCT); }
<YYINITIAL> "{"              { return tok(sym.LBRACE); }
<YYINITIAL> "("              { return tok(sym.LPAREN); }
<YYINITIAL> "~"              { return tok(sym.TILDE); }
<YYINITIAL> "<="             { return tok(sym.LE); }
<YYINITIAL> "var"            { return tok(sym.VAR); }
<YYINITIAL> "&"              { return tok(sym.BITWISEAND); }
<YYINITIAL> "float"          { return tok(sym.FLOAT); }
<YYINITIAL> "goto"           { return tok(sym.GOTO); }
<YYINITIAL> "=="             { return tok(sym.EQ); }
<YYINITIAL> "<<="            { return tok(sym.LSHIFTASSIGN); }
<YYINITIAL> "%"              { return tok(sym.MODULUS); }
<YYINITIAL> "long"           { return tok(sym.LONG); }
<YYINITIAL> "%="             { return tok(sym.MODASSIGN); }
<YYINITIAL> "+"              { return tok(sym.PLUS); }
<YYINITIAL> "/"              { return tok(sym.DIVIDE); }
<YYINITIAL> "while"          { return tok(sym.WHILE); }
<YYINITIAL> "union"          { return tok(sym.UNION); }
<YYINITIAL> "="              { return tok(sym.ASSIGN); }
<YYINITIAL> "char"           { return tok(sym.CHAR); }
<YYINITIAL> "+="             { return tok(sym.ADDASSIGN); }
<YYINITIAL> "do"             { return tok(sym.DO); }
<YYINITIAL> "for"            { return tok(sym.FOR); }
<YYINITIAL> "void"           { return tok(sym.VOID); }
<YYINITIAL> "extern"         { return tok(sym.EXTERN); }
<YYINITIAL> "return"         { return tok(sym.RETURN); }
<YYINITIAL> "else"           { return tok(sym.ELSE); }
<YYINITIAL> "break"          { return tok(sym.BREAK); }
<YYINITIAL> "fun"            { return tok(sym.FUN); }
<YYINITIAL> "int"            { return tok(sym.INT); }
<YYINITIAL> "decimal_literal" { return tok(sym.DECIMAL_LITERAL); }
<YYINITIAL> ";"              { return tok(sym.SEMICOLON); }
<YYINITIAL> "*="             { return tok(sym.MULASSIGN); }
<YYINITIAL> "..."            { return tok(sym.ELIPSES); }
<YYINITIAL> "--"             { return tok(sym.DECREMENT); }
<YYINITIAL> "-"              { return tok(sym.MINUS); }
<YYINITIAL> "||"             { return tok(sym.OR); }
<YYINITIAL> "continue"       { return tok(sym.CONTINUE); }
<YYINITIAL> "if"             { return tok(sym.IF); }

<YYINITIAL> "|"              { return tok(sym.BWISEOR); }
<YYINITIAL> ":"              { return tok(sym.COLON); }
<YYINITIAL> "^"              { return tok(sym.BWISEXOR); }
<YYINITIAL> "|="             { return tok(sym.BWISEORASSIGN); }
<YYINITIAL> ">>="            { return tok(sym.RSHIFTASSIGN); }
<YYINITIAL> "char_literal"   { return tok(sym.CHAR_LITERAL);}
<YYINITIAL> "volatile"       { return tok(sym.VOLATILE); }
<YYINITIAL> ">>"             { return tok(sym.RSHIFT); }
<YYINITIAL> "^="             { return tok(sym.BWISEXORASSIGN); }
<YYINITIAL> "!="             { return tok(sym.NEQ); }
<YYINITIAL> "&&"             { return tok(sym.AND); }
<YYINITIAL> "static"         { return tok(sym.STATIC); }
<YYINITIAL> "<<"             { return tok(sym.LSHIFT); }
<YYINITIAL> "typedef"        { return tok(sym.TYPEDEF); }
<YYINITIAL> "auto"           { return tok(sym.AUTO); }
<YYINITIAL> "--"             { return tok(sym.PREDEC); }
<YYINITIAL> "!"              { return tok(sym.NOT); }
<YYINITIAL> {ALPHA}          { return tok(sym.ID, yytext()); }
<YYINITIAL> "case"           { return tok(sym.CASE); }
<YYINITIAL> "?"              { return tok(sym.QUESTION); }
<YYINITIAL> "."              { return tok(sym.PERIOD); }







<YYINITIAL> 0[xX] {
  //CHATGBT, hex
  yybegin(HEX_DEC);
}

<HEX_DEC> [0-9a-zA-Z]+ {
    String hex_temp = yytext();
    int value = 0;

    for(int i = 0; i < hex_temp.length(); i++) {
        int digit;
        char curChar = hex_temp.charAt(i);
        if (curChar >= '0' && curChar <= '9') {
            digit = curChar - '0';
        } else if (curChar >= 'A' && curChar <= 'F') {
            digit = 10 + (curChar - 'A');
        } else if (curChar >= 'a' && curChar <= 'f') {
            digit = 10 + (curChar - 'a');
        } else {
            yybegin(YYINITIAL);
            err("Invalid Hexadecimal");
            return tok(sym.error, null);
        }

        value = value * 16 + digit;
    }

    hexToDec =  value;
    yybegin(YYINITIAL);
    return tok(sym.DECIMAL_LITERAL, hexToDec);   
}

<HEX_DEC> \n {
    err("Invalid Hexadecimal");
    yybegin(YYINITIAL);
    return tok(sym.error, null);
}
<HEX_DEC> " " {
    err("Invalid Hexadecimal");
    yybegin(YYINITIAL);
    return tok(sym.error, null);
}


<YYINITIAL> [0-9]+ { 
    if(yytext().charAt(0) == '0' && yytext().length() > 1) {
        //System.out.println(yytext());
        String oct_temp = yytext();
        int value = 0;
   

        for(int i = 0; i < oct_temp.length(); i++) {
            int digit;
            char curChar = oct_temp.charAt(i);
            if (curChar >= '0' && curChar <= '7') {
                digit = curChar - '0';
                value = value * 8 + (curChar - '0');
            } else {
                yybegin(YYINITIAL);
                err("Invalid Octal");
                return tok(sym.error, null);
            }
        
        }

        octToDec =  value;
        yybegin(YYINITIAL);
        return tok(sym.DECIMAL_LITERAL, octToDec);
    } else {
        //System.out.println(yytext());
        String dec_temp = yytext();
        int value = 0;
        for(int i = 0; i < dec_temp.length(); i++) {
            int digit;
            char curChar = dec_temp.charAt(i);
            if (curChar >= '0' && curChar <= '9') {

            } else {
                yybegin(YYINITIAL);
                err("Invalid Decimal");
                return tok(sym.error, null);
            }
        
        }
        return tok(sym.DECIMAL_LITERAL, Integer.valueOf(yytext()));
        }
}
    


<YYINITIAL> '[A-Za-z][0-9]' {
  return tok(sym.CHAR_LITERAL, yytext().charAt(1));
}

<YYINITIAL> \' {
    isError = false;
    yybegin(CHAR);
    cb = '\0';
}

<CHAR> \' { 
    if(isError) {
        yybegin(YYINITIAL);
    } else {
        yybegin(YYINITIAL); 
        return tok(sym.CHAR_LITERAL, cb);
    }
}
<CHAR> [^\'\\\n]+ {
    if(yytext().length() > 1) {
        err("Character literal too long: " + yytext());
        isError = true;
        return tok(sym.error, null);
    } else {
        cb = yytext().charAt(0);
    }
}
<CHAR>  \n { err("Unterminated char literal"); yybegin(YYINITIAL); return tok(sym.error, null); }
<CHAR> \\ { yybegin(ESCAPE_CHAR); }

<ESCAPE_CHAR> "n"                  { cb = '\n'; yybegin(CHAR); }
<ESCAPE_CHAR> "t"                  { cb = '\t'; yybegin(CHAR);}
<ESCAPE_CHAR> "b"                  { cb = '\b'; yybegin(CHAR);}
<ESCAPE_CHAR> "f"                  { cb = '\f'; yybegin(CHAR);}
<ESCAPE_CHAR> "r"                  { cb = '\r'; yybegin(CHAR);}
<ESCAPE_CHAR> \\                   { cb = '\\'; yybegin(CHAR);}
<ESCAPE_CHAR> "\""                 { cb = '"'; yybegin(CHAR);}
<ESCAPE_CHAR> "'"                  { cb = '\''; yybegin(CHAR);}
<ESCAPE_CHAR> "a"                  { cb = '\u0007'; yybegin(CHAR);}
<ESCAPE_CHAR> "v"                  { cb = '\u000B'; yybegin(CHAR);}
<ESCAPE_CHAR> [xX]                 { yybegin(HEX_CHAR); }
<ESCAPE_CHAR> [0-7]*               {
    int value = 0;
    int i = 0;
    String raw = yytext();
    //System.out.println(raw);

    for(i = 0; i < 3; i++) {
        char curChar = raw.charAt(i);

        int digit;
        if (curChar >= '0' && curChar <= '7') {
            
            value = value * 8 + (curChar - '0');
        }
    }

    cb = (char) value;
    yybegin(CHAR); // go back to string state
}
<ESCAPE_CHAR> . { 
    err("Invalid ESCAPE sequence: \\" + yytext()); 
    isError = true;
    yybegin(CHAR); 
    return tok(sym.error, null);
}

 
<HEX_CHAR> [0-9a-zA-Z][0-9a-zA-Z] {
    int value = 0;
    int i = 0;
    String raw = yytext();

    for(i = 0; i < 2; i++) {
        char curChar = raw.charAt(i);

        int digit;
        if (curChar >= '0' && curChar <= '9') {
            digit = curChar - '0';
        } else if (curChar >= 'A' && curChar <= 'F') {
            digit = 10 + (curChar - 'A');
        } else if (curChar >= 'a' && curChar <= 'f') {
            digit = 10 + (curChar - 'a');
        } else {
            err("Invalid hex escape");
            yybegin(CHAR);
            isError = true;
            return tok(sym.error, null);
        }

        value = value * 16 + digit;
    }

    cb = (char) value;
    yybegin(CHAR); // go back to string state
}




<YYINITIAL> \"           { 
    isError = false;
    yybegin(STRING); 
    sb.setLength(0);      // reset string builder
}
<STRING> \" {
    if(isError) {
        yybegin(YYINITIAL);
    } else {
        yybegin(YYINITIAL); 
        return tok(sym.STRING_LITERAL, sb.toString()); 
    } 
}
<STRING> \\                   { yybegin(ESCAPE_STRING); }
<STRING> [^\"\\\n]+           { sb.append(yytext()); }
<STRING>  \n                  { err("Unterminated string literal"); yybegin(YYINITIAL); return tok(sym.error, null); }
<ESCAPE_STRING> "n"                  { sb.append('\n'); yybegin(STRING); }
<ESCAPE_STRING> "t"                  { sb.append('\t'); yybegin(STRING); }
<ESCAPE_STRING> "b"                  { sb.append('\b'); yybegin(STRING); }
<ESCAPE_STRING> "f"                  { sb.append('\f'); yybegin(STRING); }
<ESCAPE_STRING> "r"                  { sb.append('\r'); yybegin(STRING); }
<ESCAPE_STRING> \\                   { sb.append('\\'); yybegin(STRING); }
<ESCAPE_STRING> "\""                 { sb.append('"'); yybegin(STRING); }
<ESCAPE_STRING> "'"                  { sb.append('\''); yybegin(STRING); }
<ESCAPE_STRING> "a"                  { sb.append('\u0007'); yybegin(STRING); }
<ESCAPE_STRING> "v"                  { sb.append('\u000B'); yybegin(STRING); }
<ESCAPE_STRING> [xX]                 { yybegin(HEX); }
<ESCAPE_STRING> [0-7]*               {
    int value = 0;
    int i = 0;
    String raw = yytext();
    //System.out.println(raw);

    for(i = 0; i < 3; i++) {
        char curChar = raw.charAt(i);

        int digit;
        if (curChar >= '0' && curChar <= '7') {
            
            value = value * 8 + (curChar - '0');
        }
    }

    sb.append((char) value);
    yybegin(STRING); // go back to string state
}
<ESCAPE_STRING> . { 
    err("Invalid ESCAPE sequence: \\" + yytext()); 
    isError = true; 
    yybegin(STRING); 
    return tok(sym.error, null);
}
<HEX> [0-9a-zA-F][0-9a-zA-F] {
    int value = 0;
    int i = 0;
    String raw = yytext();

    for(i = 0; i < 2; i++) {
        char curChar = raw.charAt(i);

        int digit;
        if (curChar >= '0' && curChar <= '9') {
            digit = curChar - '0';
        } else if (curChar >= 'A' && curChar <= 'F') {
            digit = 10 + (curChar - 'A');
        } else if (curChar >= 'a' && curChar <= 'f') {
            digit = 10 + (curChar - 'a');
        } else {
            err("Invalid hex escape");
            yybegin(STRING);
            isError = true;
            return tok(sym.error, null);
        }

        value = value * 16 + digit;
    }

    sb.append((char) value);
    yybegin(STRING); // go back to string state
}


<YYINITIAL> "/*"      { yybegin(COMMENT); }
<COMMENT>   "*/"      { yybegin(YYINITIAL); }
<COMMENT>   \n        { newline(); }
<COMMENT>   .         { }









