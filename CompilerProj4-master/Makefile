JFLAGS=-g

Full: Parse/Grm.java 
	javac ${JFLAGS} */*.java

# Generate the parser from Grm.cup
Parse/Grm.java: Parse/Yylex.java
	cd Parse; java java_cup.Main -parser Grm -expect 3 -nonterms -dump_grammar -dump_states -compact_red <Grm.cup >Grm.out 2>Grm.err

# Generate the lexer from tiger.lex (JFlex)
Parse/Yylex.java: 
	cd Parse; java JLex.Main Tiger.lex; mv Tiger.lex.java Yylex.java

# Clean up generated files
clean:
	find . -type f -name "*.class" ! -path "./RegAlloc/*" -delete
	rm -f Parse/Grm.java Parse/Grm.err Parse/Grm.out Parse/Yylex.java