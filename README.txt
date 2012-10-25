CSE 3341 Project 1
Fall 2012
Andrew Fitzgerald

Implementation of "Core" programming language



Description of files:
    exec
        Executor.java
            given input file and parsetree, creates ID list and executes code, returning an output string
        ID.java
            simple class to store name and values of ID's as well as if they have been assigned yet
        Main.java
    parser
        NodeTypes.java
            enum of different parse tree node types
        Parser.java
            given a scanner, returns a parse tree of the entire input program
        ParseTree.java
            parsed representation of the program. contains methods to print program by node type
    scanner
        ProgramScanner.java
            given a file name, returns input in tokenized format, one token at a time
        Tokens.java
            enum of different token types
        TokenTuple.java
            utility class which stores token type and content, also checks validity of id's/constants
    
        
Compilation:
    javac @core.src
Running:
    java exec.Main program.code program.data
    where program.code is the input program and program.data is the accopmanying input file.
