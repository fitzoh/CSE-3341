package scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class ProgramScanner {
	BufferedReader in;
	TokenTuple currentToken;
	String endToken = ";|,|:|!|(|)|\\[|\\]|=|<|>|\\+|-|\\*|\\|";

	public ProgramScanner(String fileName) throws IOException{
		in = new BufferedReader(new FileReader(fileName));
		nextToken();
	}
	//advances past white space until next token is found
	public void nextToken() throws IOException{
		int next = in.read();
		while (Character.isWhitespace(next) && next != -1)
		{
			next = in.read();
		}
		if (next == -1)
		{
			currentToken =  new TokenTuple(Tokens.EOF, "");
		}else{

			switch((char) next){
			//one character tokens
			case ';': currentToken = new TokenTuple(Tokens.SEMICOLON, ";"); break;
			case ',': currentToken = new TokenTuple(Tokens.COMMA, ","); break;
			case '[': currentToken = new TokenTuple(Tokens.LBRACKET, "["); break;
			case ']': currentToken = new TokenTuple(Tokens.RBRACKET, "]"); break;
			case '(': currentToken = new TokenTuple(Tokens.LPAREN, "("); break;
			case ')': currentToken = new TokenTuple(Tokens.RPAREN, ")"); break;
			case '+': currentToken = new TokenTuple(Tokens.PLUS, "+"); break;
			case '-': currentToken = new TokenTuple(Tokens.MINUS, "-"); break;
			case '*': currentToken = new TokenTuple(Tokens.TIMES, "*"); break;
			case '=': currentToken = new TokenTuple(Tokens.EQ, "="); break;
			case '|': currentToken = new TokenTuple(Tokens.BAR, "|"); break;
			//possibly 2 character tokens
			case ':':{
				in.mark(1);
				next = in.read();
				if ((char) next == '='){
					currentToken = new TokenTuple(Tokens.ASSIGNMENT, ":=");
				}else{
					
					in.reset();
					currentToken = new TokenTuple(Tokens.COLON, ":");
				}
				 break;
			}
			case '!':{
				in.mark(1);
				next = in.read();
				if ((char) next == '='){
					currentToken = new TokenTuple(Tokens.NOTEQ, "!=");
				}else{
					in.reset();
					currentToken = new TokenTuple(Tokens.NOT, "!");
				}
				 break;
			}
			case '<':{
				in.mark(1);
				next = in.read();
				if ((char) next == '='){
					currentToken = new TokenTuple(Tokens.LTEQ, "<=");
				}else{
					in.reset();
					currentToken = new TokenTuple(Tokens.LT, "<");
				}
				 break;
			}
			case '>':{
				in.mark(1);
				next = in.read();
				if ((char) next == '='){
					currentToken = new TokenTuple(Tokens.GTEQ, ">=");
				}else{
					in.reset();
					currentToken = new TokenTuple(Tokens.GT, ">");
				}
				 break;
			}
			//either const/id/invalid
			default:{
				boolean continued = true;
				StringBuilder buffer = new StringBuilder();
				while(continued){
					buffer.append((char) next);
					in.mark(1);
					next = in.read();
					continued &= next != -1;
					continued &= ! Character.isWhitespace(next);
					continued &= !Character.toString((char)next).matches(endToken);
					if(!continued){
						in.reset();
					}
				}
				currentToken = new TokenTuple(buffer.toString());
			}
			 break;
			}
		}
	}
	//returns current token tuple
	public TokenTuple currentToken(){
		return currentToken;
	}
}
