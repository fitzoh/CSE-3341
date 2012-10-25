package parser;
import java.util.ArrayList;
import java.util.List;

//represents parse tree for program
//contains methods to pretty print program
public class ParseTree {
	public NodeTypes type;
	public List<ParseTree> children;
	public String value;
	int indentSize = 2;
	
	//most nodes don't require a value
	public ParseTree(NodeTypes type){
		this.type = type;
		this.children = new ArrayList<ParseTree>();
		this.value = "";
	}
	
	//value stores names of id's, values of constants
	public ParseTree(NodeTypes type, String value){
		this.type = type;
		this.children = new ArrayList<ParseTree>();
		this.value = value;
	}
	//add tree as child
	public void append(ParseTree child){
		children.add(child);
	}
	//main call to get string rep. prints program, begin, end, and calls to decl-seq and stmt-seq
	public String programStr(){
		StringBuilder s = new StringBuilder();
		s.append("program\n");
		s.append(children.get(0).declSeqStr(indentSize));
		s.append("begin\n");
		s.append(children.get(1).stmtSeqStr(indentSize));
		s.append("end");
		return s.toString();
	}
	//string rep of stmt-seq
	private String stmtSeqStr(int indent) {
		StringBuilder s = new StringBuilder();
		switch(children.get(0).type){
		case ASSIGN: s.append(children.get(0).assignStr(indent));break;
		case IF: s.append(children.get(0).ifStr(indent));break;
		case LOOP: s.append(children.get(0).loopStr(indent));break;
		case IN: s.append(children.get(0).inStr(indent));break;
		case OUT: s.append(children.get(0).outStr(indent));break;
		case CASE: s.append(children.get(0).caseStr(indent));break;
		}
		if(children.size()>1){
			s.append(children.get(1).stmtSeqStr(indent));
		}
		return s.toString();
	}
	//string rep of case
	private String caseStr(int indent) {
		StringBuilder s = new StringBuilder();
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("case ");
		s.append(children.get(0).value);
		s.append(" of\n");
		s.append(children.get(1).firstCaseListStr(indent+indentSize));
		for (int i=0;i<indent+indentSize*2+1;i++){
			s.append(" ");
		}
		s.append("else ");
		s.append(children.get(2).exprStr());
		s.append("\n");
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("end\n");
		return s.toString();
	}
	//used for first item in case list since it lacks '|'
	private String firstCaseListStr(int indent) {
		StringBuilder s = new StringBuilder();
		for(int i=0;i<indent+indentSize+1;i++){
			s.append(" ");
		}
		s.append(children.get(0).constListStr());
		s.append(":");
		s.append(children.get(1).exprStr());
		s.append("\n");
		if (children.size()>2){
			s.append(children.get(2).caseListStr(indent));
		}
				
		return s.toString();
	}
	//string rep of case item
	private String caseListStr(int indent) {
		StringBuilder s = new StringBuilder();
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("|");
		for (int i=0;i<indentSize;i++){
			s.append(" ");
		}
		s.append(children.get(0).constListStr());
		s.append(":");
		s.append(children.get(1).exprStr());
		if (children.size()>2){
			s.append(children.get(2).caseListStr(indent));
		}
		s.append("\n");
		return s.toString();
	}
	//string rep of const
	private String constListStr() {
		StringBuilder s = new StringBuilder();
		s.append(children.get(0).value);
		if(children.size()>1){
			s.append(",");
			s.append(children.get(1).constListStr());
		}
		return s.toString();
	}
	//string rep of in
	private String inStr(int indent) {
		StringBuilder s = new StringBuilder();
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("input ");
		s.append(children.get(0).idListStr());
		s.append(";\n");
		return s.toString();
	}
	//string rep of out
	private String outStr(int indent) {
		StringBuilder s = new StringBuilder();
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("output ");
		s.append(children.get(0).idListStr());
		s.append(";\n");
		return s.toString();
	}
	//string rep of loop
	private String loopStr(int indent) {
		StringBuilder s = new StringBuilder();
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("do\n");
		s.append(children.get(0).stmtSeqStr(indent+indentSize));
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("while");
		s.append(children.get(1).condStr());
		s.append("enddo;\n");
		
		return s.toString();
	}
	//string rep of if
	private String ifStr(int indent) {
		StringBuilder s = new StringBuilder();
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("if");
		s.append(children.get(0).condStr());
		s.append("then\n");
		s.append(children.get(1).stmtSeqStr(indent+indentSize));
		if(children.size()>2){
			for (int i=0;i<indent;i++){
				s.append(" ");
			}
			s.append("else\n");
			s.append(children.get(2).stmtSeqStr(indent+indentSize));
		}
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("endif;\n");
		return s.toString();
	}
	//string rep of cond
	private String condStr() {
		StringBuilder s = new StringBuilder();
		switch(children.size()){
		case 1:	s.append(children.get(0).cmprStr()); break;
		case 2:{
			s.append("!");
			s.append(children.get(1).condStr());
			break;
		}
		case 3:{
			s.append("(");
			s.append(children.get(0).condStr());
			if(children.get(1).type==NodeTypes.AND){
				s.append("AND");
			}else{
				s.append("OR");
			}
			s.append(children.get(2).condStr());
			s.append(")");
			break;
		}
		}
		return s.toString();
	}
	//string rep of cmpr
	private String cmprStr() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		s.append(children.get(0).exprStr());
		s.append(children.get(1).value);
		s.append(children.get(2).exprStr());
		s.append("]");
		return s.toString();
	}
	//string rep of assign
	private String assignStr(int indent) {
		StringBuilder s = new StringBuilder();
		for (int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append(children.get(0).value);
		s.append(":=");
		s.append(children.get(1).exprStr());
		s.append(";\n");
		return s.toString();
	}
	//string rep of expr
	private String exprStr() {
		StringBuilder s = new StringBuilder();
		s.append(children.get(0).termStr());
		if(children.size()>1){
			if(children.get(1).type==NodeTypes.MINUS){
				s.append("-");
			}else{
				s.append("+");
			}
			s.append(children.get(2).exprStr());
		}
		return s.toString();
	}
	//string rep of term
	private String termStr() {
		StringBuilder s = new StringBuilder();
		s.append(children.get(0).factorStr());
		if(children.size()>1){
			s.append("*");
			s.append(children.get(2).termStr());
		}
		return s.toString();
	}
	//string rep of factor
	private String factorStr() {
		StringBuilder s = new StringBuilder();
		switch(children.get(0).type){
		case CONST: s.append(children.get(0).value); break;
		case ID: s.append(children.get(0).value); break;
		case MINUS:{
			s.append("-");
			s.append(children.get(1).factorStr());
			break;
		}
		case EXPR:{
			s.append("(");
			s.append(children.get(0).exprStr());
			s.append(")");
		}
		}
		return s.toString();
	}
	//string rep of decl-seq
	private String declSeqStr(int indent) {
		StringBuilder s = new StringBuilder();
		s.append(children.get(0).declStr(indent));
		if(children.size()>1){
			s.append(children.get(1).declSeqStr(indent));
		}
		return s.toString();
	}
	
	//string rep of decl
	private String declStr(int indent) {
		StringBuilder s = new StringBuilder();
		for(int i=0;i<indent;i++){
			s.append(" ");
		}
		s.append("int ");
		s.append(children.get(0).idListStr());
		s.append(";\n");
		return s.toString();
	}
	
	//string rep of id-list
	private String idListStr() {
		StringBuilder s = new StringBuilder();
		if (children.size()>0){
			s.append(children.get(0).value);
		}
		for(int i=1;i<children.size();i++){
			s.append(",");
			s.append(children.get(1).idListStr());
		}
		return s.toString();
	}

}
