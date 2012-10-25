package scanner;

//tuple of current token and the string representation of it.
public class TokenTuple {
	public Tokens token;
	public String content;
	//explicit constructor, takes token and content
	public TokenTuple(Tokens token, String content){
		this.token=token;
		this.content=content;
	}
	//generates token and content from string
	public TokenTuple(String strtoken) {
		//legal ID possibilties
		String id = "[a-zA-Z][a-zA-Z0-9]*";
		//legal constants
		String constant = "-?[0-9]+";
		this.content = strtoken;
		//Can't perform switch statement on strings with version < 1.7
		if (strtoken.equals("program")){
			this.token = Tokens.PROGRAM;
		}else if (strtoken.equals("begin")){
			this.token = Tokens.BEGIN;
		}else if (strtoken.equals("end")){
			this.token = Tokens.END;
		}else if (strtoken.equals("int")){
			this.token = Tokens.INT;
		}else if (strtoken.equals("input")){
			this.token = Tokens.INPUT;
		}else if (strtoken.equals("output")){
			this.token = Tokens.OUTPUT;
		}else if (strtoken.equals("if")){
			this.token = Tokens.IF;
		}else if (strtoken.equals("then")){
			this.token = Tokens.THEN;
		}else if (strtoken.equals("else")){
			this.token = Tokens.ELSE;
		}else if (strtoken.equals("endif")){
			this.token = Tokens.ENDIF;
		}else if (strtoken.equals("do")){
			this.token = Tokens.DO;
		}else if (strtoken.equals("while")){
			this.token = Tokens.WHILE;
		}else if (strtoken.equals("enddo")){
			this.token = Tokens.ENDDO;
		}else if (strtoken.equals("AND")){
			this.token = Tokens.AND;
		}else if (strtoken.equals("OR")){
			this.token = Tokens.OR;
		}else if (strtoken.equals("begin")){
			this.token = Tokens.BEGIN;
		}else if (strtoken.equals("case")){
			this.token = Tokens.CASE;
		}else if (strtoken.equals("of")){
			this.token = Tokens.OF;
		}else if (strtoken.matches(id)){
			this.token = Tokens.ID;
		}else if (strtoken.matches(constant)){
			this.token = Tokens.CONST;
		}else{
			throw new IllegalStateException("Invalid token");
		}
	}
	public String toString(){
		return this.token.toString() + " " + this.content;
	}
}
