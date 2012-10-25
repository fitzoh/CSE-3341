package parser;
import java.io.IOException;
import scanner.ProgramScanner;
import scanner.Tokens;

//recursive descent parser for core programming language
//based off ProgramScanner s
public class Parser {

	//initial publicly available call
	//takes a fresh scanner s, returns full parse tree.
	//all trees from other calls are appended directly or indirectly to this tree
	public static ParseTree parseProgram(ProgramScanner s) throws IOException{
		ParseTree p = new ParseTree(NodeTypes.PROG);
		if (s.currentToken().token != Tokens.PROGRAM){
			throw new IllegalStateException("\"program\" expected");
		}
		s.nextToken();
		p.append(parseDeclSeq(s));
		if (s.currentToken().token != Tokens.BEGIN){
			throw new IllegalStateException("\"begin\" expected");
		}
		s.nextToken();
		p.append(parseStmtSeq(s));
		if (s.currentToken().token != Tokens.END){
			throw new IllegalStateException("\"end\" expected");
		}
		s.nextToken();
		if (s.currentToken().token != Tokens.EOF){
			throw new IllegalStateException("eof expected");
		}

		return p;
	}

	//call for parseDeclSeq, returns ParseTree of type DECLSEQ
	private static ParseTree parseDeclSeq(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.DECLSEQ);
		if (s.currentToken().token != Tokens.INT){
			throw new IllegalStateException("\"int\" expected");
		}
		p.append(parseDecl(s));
		while(s.currentToken().token!= Tokens.BEGIN){
			p.append(parseDeclSeq(s));
		}
		return p;
	}

	//call for single decl
	private static ParseTree parseDecl(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.DECL);
		if (s.currentToken().token != Tokens.INT){
			throw new IllegalStateException("\"int\" expected");
		}
		s.nextToken();
		p.append(parseIdList(s));
		if (s.currentToken().token != Tokens.SEMICOLON){
			throw new IllegalStateException("\";\" expected");
		}
		s.nextToken();
		return p;
	}
	//call for id-list
	private static ParseTree parseIdList(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.IDLIST);
		if (s.currentToken().token != Tokens.ID){
			throw new IllegalStateException("ID expected");
		}
		p.append(new ParseTree(NodeTypes.ID, s.currentToken().content));
		s.nextToken();
		if (s.currentToken().token == Tokens.COMMA){
			s.nextToken();
			p.append(parseIdList(s));
		}
		return p;
	}
	//call for stmt-seq
	private static ParseTree parseStmtSeq(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.STMTSEQ);
		if (s.currentToken().token==Tokens.ID){
			p.append(parseAssign(s));
		}else if(s.currentToken().token==Tokens.IF){
			p.append(parseIf(s));
		}else if(s.currentToken().token==Tokens.DO){
			p.append(parseWhile(s));
		}else if(s.currentToken().token==Tokens.INPUT){
			p.append(parseInput(s));
		}else if(s.currentToken().token==Tokens.OUTPUT){
			p.append(parseOutput(s));
		}else if(s.currentToken().token==Tokens.CASE){
			p.append(parseCase(s));
		}else{
			throw new IllegalStateException("statement expected");
		}
		boolean more = false;
		more |= s.currentToken().token==Tokens.ID;
		more |= s.currentToken().token==Tokens.DO;
		more |= s.currentToken().token==Tokens.INPUT;
		more |= s.currentToken().token==Tokens.OUTPUT;
		more |= s.currentToken().token==Tokens.CASE;
		if(more){
			p.append(parseStmtSeq(s));
		}
		return p;
	}
	//base call for case
	private static ParseTree parseCase(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.CASE);
		if (s.currentToken().token != Tokens.CASE){
			throw new IllegalStateException("\"case\" expected");
		}
		s.nextToken();
		p.append(new ParseTree(NodeTypes.CONST, s.currentToken().content));
		s.nextToken();
		if (s.currentToken().token != Tokens.OF){
			throw new IllegalStateException("\"of\" expected");
		}
		s.nextToken();
		p.append(parseCaseList(s));
		if (s.currentToken().token != Tokens.ELSE){
			throw new IllegalStateException("\"else\" expected");
		}
		s.nextToken();
		p.append(parseExpr(s));
		if (s.currentToken().token != Tokens.END){
			throw new IllegalStateException("\"end\" expected");
		}
		s.nextToken();
		return p;
	}
	//call for list of case options
	private static ParseTree parseCaseList(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.CASELIST);
		p.append(parseConstList(s));
		if (s.currentToken().token != Tokens.COLON){
			throw new IllegalStateException("\":\" expected");
		}
		s.nextToken();
		p.append(parseExpr(s));
		if(s.currentToken().token== Tokens.BAR){
			s.nextToken();
			p.append(parseCaseList(s));
		}
		return p;
	}
	//call for list of constants in case
	private static ParseTree parseConstList(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.CONSTLIST);
		if (s.currentToken().token != Tokens.CONST){
			throw new IllegalStateException("CONST expected");
		}
		p.append(new ParseTree(NodeTypes.CONST, s.currentToken().content));
		s.nextToken();
		if (s.currentToken().token == Tokens.COMMA){
			s.nextToken();
			p.append(parseConstList(s));
		}
		return p;
	}
	//call for output
	private static ParseTree parseOutput(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.OUT);
		if (s.currentToken().token != Tokens.OUTPUT){
			throw new IllegalStateException("\"output\" expected");
		}
		s.nextToken();
		p.append(parseIdList(s));
		if (s.currentToken().token != Tokens.SEMICOLON){
			throw new IllegalStateException("\";\" expected");
		}
		s.nextToken();
		return p;
	}
	//call for input
	private static ParseTree parseInput(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.IN);
		if (s.currentToken().token != Tokens.INPUT){
			throw new IllegalStateException("\"input\" expected");
		}
		s.nextToken();
		p.append(parseIdList(s));
		if (s.currentToken().token != Tokens.SEMICOLON){
			throw new IllegalStateException("\";\" expected");
		}
		s.nextToken();
		return p;
	}
	//call for while
	private static ParseTree parseWhile(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.LOOP);
		if (s.currentToken().token != Tokens.DO){
			throw new IllegalStateException("\"do\" expected");
		}
		s.nextToken();
		p.append(parseStmtSeq(s));
		if (s.currentToken().token != Tokens.WHILE){
			throw new IllegalStateException("\"while\" expected");
		}
		s.nextToken();
		p.append(parseCond(s));
		if (s.currentToken().token != Tokens.ENDDO){
			throw new IllegalStateException("\"enddo\" expected");
		}
		s.nextToken();
		if (s.currentToken().token != Tokens.SEMICOLON){
			throw new IllegalStateException("\";\" expected");
		}
		s.nextToken();
		return p;
	}
	//call for if
	private static ParseTree parseIf(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.IF);
		if (s.currentToken().token != Tokens.IF){
			throw new IllegalStateException("\"if\" expected");
		}
		s.nextToken();
		p.append(parseCond(s));
		if (s.currentToken().token != Tokens.THEN){
			throw new IllegalStateException("\"then\" expected");
		}
		s.nextToken();
		p.append(parseStmtSeq(s));
		if (s.currentToken().token == Tokens.ELSE){
			s.nextToken();
			p.append(parseStmtSeq(s));
		}
		if (s.currentToken().token != Tokens.ENDIF){
			throw new IllegalStateException("\"endif\" expected");
		}
		s.nextToken();
		if (s.currentToken().token != Tokens.SEMICOLON){
			throw new IllegalStateException("\";\" expected");
		}
		s.nextToken();
		return p;
	}
	//call for cond
	private static ParseTree parseCond(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.COND);
		if(s.currentToken().token == Tokens.NOT){
			p.append(new ParseTree(NodeTypes.NOT));
			s.nextToken();
			p.append(parseCond(s));
		}else if(s.currentToken().token == Tokens.LPAREN){
			s.nextToken();
			p.append(parseCond(s));
			if(s.currentToken().token == Tokens.AND){
				p.append(new ParseTree(NodeTypes.AND));
			} else if(s.currentToken().token == Tokens.OR){
				p.append(new ParseTree(NodeTypes.OR));
			} else{
				throw new IllegalStateException("\"AND\" or \"OR\"expected");
			}
			s.nextToken();
			p.append(parseCond(s));
			if (s.currentToken().token != Tokens.RPAREN){
				throw new IllegalStateException("\")\" expected");
			}
			s.nextToken();
		} else{
			p.append(parseCmpr(s));
		}
		return p;
	}

	//call for cmpr
	private static ParseTree parseCmpr(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.CMPR);			
		if (s.currentToken().token != Tokens.LBRACKET){
			throw new IllegalStateException("\"[\" expected");
		}
		s.nextToken();
		p.append(parseExpr(s));
		boolean valid = false;
		valid |= s.currentToken().token == Tokens.LT;
		valid |= s.currentToken().token == Tokens.LTEQ;
		valid |= s.currentToken().token == Tokens.EQ;
		valid |= s.currentToken().token == Tokens.GT;
		valid |= s.currentToken().token == Tokens.GTEQ;
		valid |= s.currentToken().token == Tokens.NOTEQ;
		if (!valid){
			throw new IllegalStateException("Comparison operator expected");
		}
		p.append(new ParseTree(NodeTypes.CMPROP, s.currentToken().content));
		s.nextToken();
		p.append(parseExpr(s));		
		if (s.currentToken().token != Tokens.RBRACKET){
			throw new IllegalStateException("\"]\" expected");
		}
		s.nextToken();
		return p;
	}
	//call for assign
	private static ParseTree parseAssign(ProgramScanner s) throws IOException{
		ParseTree p = new ParseTree(NodeTypes.ASSIGN);
		if (s.currentToken().token != Tokens.ID){
			throw new IllegalStateException("ID expected");
		}
		p.append(new ParseTree(NodeTypes.ID, s.currentToken().content) );
		s.nextToken();
		if (s.currentToken().token != Tokens.ASSIGNMENT){
			throw new IllegalStateException("\":=\" expected");
		}
		s.nextToken();
		p.append(parseExpr(s));
		if (s.currentToken().token != Tokens.SEMICOLON){
			throw new IllegalStateException("\";\" expected");
		}
		s.nextToken();
		return p;
	}
	//call for expr
	private static ParseTree parseExpr(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.EXPR);
		p.append(parseTerm(s));
		if (s.currentToken().token==Tokens.PLUS){
			p.append(new ParseTree(NodeTypes.PLUS));
			s.nextToken();
			p.append(parseExpr(s));
		}else if (s.currentToken().token==Tokens.MINUS){
			p.append(new ParseTree(NodeTypes.MINUS));
			s.nextToken();
			p.append(parseExpr(s));
		}
		return p;
	}

	//call for term
	private static ParseTree parseTerm(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.TERM);
		p.append(parseFactor(s));
		if (s.currentToken().token == Tokens.TIMES){
			p.append(new ParseTree(NodeTypes.TIMES));
			s.nextToken();
			p.append(parseTerm(s));
		}
		return p;
	}

	//call for factor
	private static ParseTree parseFactor(ProgramScanner s) throws IOException {
		ParseTree p = new ParseTree(NodeTypes.FACTOR);
		if(s.currentToken().token==Tokens.CONST){
			p.append(new ParseTree(NodeTypes.CONST, s.currentToken().content));
			s.nextToken();
		}else if(s.currentToken().token==Tokens.ID){
			p.append(new ParseTree(NodeTypes.ID, s.currentToken().content));
			s.nextToken();
		}else if(s.currentToken().token==Tokens.MINUS){
			p.append(new ParseTree(NodeTypes.MINUS));
			s.nextToken();
			p.append(parseFactor(s));
		}else if(s.currentToken().token==Tokens.LPAREN){
			s.nextToken();
			p.append(parseExpr(s));
			if(s.currentToken().token!= Tokens.RPAREN){
				throw new IllegalStateException("\")\" expected");
			}
			s.nextToken();
		}else{
			throw new IllegalStateException("factor expected");
		}
		return p;
	}

}
