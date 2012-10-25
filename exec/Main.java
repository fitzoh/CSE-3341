package exec;
import java.io.IOException;

import parser.ParseTree;
import parser.Parser;

import scanner.ProgramScanner;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		try {
			//create scanner
			ProgramScanner s = new ProgramScanner(args[0]);
			//parse program from scanner
			ParseTree p = Parser.parseProgram(s);
			//pretty print program from parse tree
			System.out.println(p.programStr());
			//init executor
			Executor e = new Executor(args[1], p);
			//execute and report output
			System.out.println(e.executeProgram());
		} catch (IOException e) {
			//check for error, print message if found
			System.out.println("ERROR: "+e.getMessage());
		}

	}


}
