package exec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import parser.NodeTypes;
import parser.ParseTree;

public class Executor {
	Scanner s;
	ParseTree program;
	List<ID> IDList;
	
	//pass in input file and ParseTree containing program
	//opens scanner for input from file
	//creates list of ID's used in program
	public Executor(String fileName, ParseTree p) throws IOException{
		s = new Scanner(new BufferedReader(new FileReader(fileName)));
		program = p;
		IDList = new ArrayList<ID>();
		ParseTree declSeq = program.children.get(0);
		getIdsDeclSeq(declSeq);
	}
	
	//first call to get Id's
	private void getIdsDeclSeq(ParseTree declSeq) {
		assert(declSeq.type==NodeTypes.DECLSEQ);
		getIdsDecl(declSeq.children.get(0));
		if(declSeq.children.size()>1){
			getIdsDeclSeq(declSeq.children.get(1));
		}
	}
	
	//deeper id calls
	private void getIdsDecl(ParseTree decl){
		assert(decl.type==NodeTypes.DECL);
		getIdsIdList(decl.children.get(0));
		if(decl.children.size()>1){
			getIdsDecl(decl.children.get(1));
		}
	}
	
	//deeper id calls
	private void getIdsIdList(ParseTree idList){
		assert(idList.type==NodeTypes.IDLIST);
		getIds(idList.children.get(0));
		if(idList.children.size()>1){
			getIdsIdList(idList.children.get(1));
		}
	}

	//final id call
	private void getIds(ParseTree id) {
		assert(id.type==NodeTypes.ID);
		String newID = id.value;
		for (ID x : IDList){
			if(x.name.equals(newID))
				throw new IllegalStateException(newID+" declared twice");
		}
		IDList.add(new ID(newID));


	}
	
	//initial call to execute program. Returns a string of program output
	public String executeProgram(){
		StringBuilder s = new StringBuilder();
		s.append(executeStmtSeq(program.children.get(1)));
		return s.toString();
	}
	
	//call to execute stmt-seq, returns string of output
	private String executeStmtSeq(ParseTree stmtSeq) {	
		assert(stmtSeq.type==NodeTypes.STMTSEQ);	
		StringBuilder s = new StringBuilder();
		s.append(executeStmt(stmtSeq.children.get(0)));
		if(stmtSeq.children.size()>1){
			s.append(executeStmtSeq(stmtSeq.children.get(1)));
		}
		return s.toString();
	}
	
	//execute a single statement. switch to select statement type
	private String executeStmt(ParseTree stmt) {
		StringBuilder s = new StringBuilder();
		switch(stmt.type){
		case ASSIGN: executeAssign(stmt);break;
		case IF: s.append(executeIf(stmt));break;
		case LOOP: s.append(executeLoop(stmt));break;
		case IN: executeIn(stmt.children.get(0));break;
		case OUT: s.append(executeOut(stmt.children.get(0)));break;
		case CASE: executeCase(stmt);break;
		}
		return s.toString();
	}
	
	//Case. checks ID is valid, then checks for matches.
	//If no matches found use default
	private void executeCase(ParseTree caseTree) {
		assert(caseTree.type==NodeTypes.CASE);
		ID id = null;
		boolean valid = false;
		for(ID x : IDList){
			if(x.name.equals(caseTree.children.get(0).value)){
				valid = true;
				id = x;
			}
		}
		if(!valid){
			throw new IllegalStateException(caseTree.children.get(0).value+" not declared");
		}
		boolean match = false;
		match = executeCaseList(caseTree.children.get(1), id);
		if(!match){
			id.assign(executeExpr(caseTree.children.get(2)));
		}

	}
	
	// call to check case list
	private boolean executeCaseList(ParseTree caseList, ID id) {
		assert(caseList.type==NodeTypes.CASELIST);
		boolean match = false;
		match = executeConstList(caseList.children.get(0), id);
		if(match){
			id.assign(executeExpr(caseList.children.get(1)));
		}
		if(!match && caseList.children.size()>2){
			match = executeCaseList(caseList.children.get(2), id);
		}
		return match;
	}
	
	//checks list of const's within case entry
	private boolean executeConstList(ParseTree constTree, ID id) {
		assert(constTree.type==NodeTypes.CONSTLIST);
		boolean match = false;
		if(Integer.parseInt(constTree.children.get(0).value) == id.getValue()){
			match = true;
		}
		if(!match && constTree.children.size()>1){
			match = executeConstList(constTree.children.get(1), id);
		}
		return match;
	}
	
	//only function that writes to out
	//makes recursive calls to get all id's in list
	private String executeOut(ParseTree outTree) {
		assert(outTree.type==NodeTypes.IDLIST);
		StringBuilder s = new StringBuilder();
		boolean valid = false;
		for(ID x : IDList){	
			if(x.name.equals(outTree.children.get(0).value)){
				valid = true;
				s.append(x.getValue()+"\n");
			}
		}
		if(!valid){
			throw new IllegalStateException(outTree.children.get(0).value+" not declared");
		}
		if(outTree.children.size()>1){
			s.append(executeOut(outTree.children.get(1)));
		}
		return s.toString();
	}

	//read from input, check for valid ID's
	private void executeIn(ParseTree idList) {
		assert(idList.type==NodeTypes.IDLIST);
		if(!s.hasNext()){
			throw new IllegalStateException("no input found");
		}
		String legal = "-?[0-9]+";
		String input = s.next();
		if(!input.matches(legal)){
			throw new IllegalStateException("invalid input");
		}
		boolean valid = false;
		for(ID x : IDList){
			if(x.name.equals(idList.children.get(0).value)){
				valid = true;
				x.assign(Integer.parseInt(input));
			}
		}
		if(!valid){
			throw new IllegalStateException("invalid ID");
		}
		if(idList.children.size()>1){
			executeIn(idList.children.get(1));
		}

	}
	
	//execute loop, return string if any output statements present
	private Object executeLoop(ParseTree loop) {
		assert(loop.type == NodeTypes.LOOP);
		StringBuilder s = new StringBuilder();
		do{
			s.append(executeStmtSeq(loop.children.get(0)));
		} while(executeCond(loop.children.get(1)));
		return s.toString();
	}
	
	//execute if/ifthen. return string if output is executed
	private String executeIf(ParseTree ifTree) {
		assert(ifTree.type==NodeTypes.IF);
		StringBuilder s = new StringBuilder();
		if(executeCond(ifTree.children.get(0))){
			s.append(executeStmtSeq(ifTree.children.get(1)));
		}else if(ifTree.children.size()>2){
			s.append(executeStmtSeq(ifTree.children.get(2)));
		}
		return s.toString();
	}
	
	//evaluate condition, return boolean
	private boolean executeCond(ParseTree cond) {
		assert(cond.type==NodeTypes.COND);
		boolean result = false;
		if(cond.children.get(0).type == NodeTypes.CMPR){
			result = executeCmpr(cond.children.get(0));
		}else if(cond.children.get(0).type == NodeTypes.NOT){
			result = ! executeCond(cond.children.get(1));
		}else{
			if(cond.children.get(1).type==NodeTypes.AND){
				result = executeCond(cond.children.get(0)) && executeCond(cond.children.get(2));
			}else{
				result = executeCond(cond.children.get(0)) || executeCond(cond.children.get(2));
			}
		}

		return result;
	}
	
	//evaluate comparison, return boolean
	private boolean executeCmpr(ParseTree cmpr) {
		assert(cmpr.type==NodeTypes.CMPR);
		boolean result = false;
		String cmprop = cmpr.children.get(1).value;
		int first = executeExpr(cmpr.children.get(0));
		int second = executeExpr(cmpr.children.get(2));
		if(cmprop.equals("<")){
			result = first < second;
		}else if(cmprop.equals("=")){
			result = first == second;
		}else if(cmprop.equals("!=")){
			result = first != second;
		}else if(cmprop.equals(">")){
			result = first > second;
		}else if(cmprop.equals(">=")){
			result = first >= second;
		}else{
			result = first <= second;
		}

		return result;
	}
	
	//complete assignment. Checks for valid id's
	private void executeAssign(ParseTree assign) {
		assert(assign.type==NodeTypes.ASSIGN);
		String id = assign.children.get(0).value;
		boolean exists = false;
		for (ID x : IDList){
			if (id.equals(x.name)){
				exists = true;
				x.assign(executeExpr(assign.children.get(1)));
			}
		}
		if(!exists){
			throw new IllegalStateException(id =" was not declared");
		}
	}
	
	//execute expr, returning an int
	private int executeExpr(ParseTree expr) {
		assert(expr.type==NodeTypes.EXPR);
		int result = 0;
		if(expr.children.size()==0){
			result = executeTerm(expr);
		}else{
			result = executeTerm(expr.children.get(0));
			if(expr.children.size()>1){
				if (expr.children.get(1).type==NodeTypes.PLUS){
					result += executeExpr(expr.children.get(2));
				}else{
					result -= executeExpr(expr.children.get(2));
				}
			}
		}
		return result;
	}
	
	//eval term, returning int
	private int executeTerm(ParseTree term) {
		assert(term.type==NodeTypes.TERM);
		int result = 0;
		if(term.children.size()==0){
			result = executeFactor(term);
		}else{
			result = executeFactor(term.children.get(0));
			if (term.children.size()>1){
				result *= executeTerm(term.children.get(2));
			}
		}
		return result;
	}
	
	//eval factor, return int
	private int executeFactor(ParseTree factor) {
		assert(factor.type==NodeTypes.FACTOR);
		int result = 0;
		if(factor.children.size() == 1){
			if(factor.children.get(0).type==NodeTypes.CONST){
				result = Integer.parseInt(factor.children.get(0).value);
			}else if (factor.children.get(0).type==NodeTypes.ID){
				boolean valid = false;
				for (ID x : IDList){
					if (x.name.equals(factor.children.get(0).value)){
						valid = true;
						result = x.value;
					}
				}
				if(!valid){
					throw new IllegalStateException(factor.children.get(0).value+" not found");
				}
			} else{
				result = executeExpr(factor.children.get(0));
			}
		} else{
			result = -1*executeFactor(factor.children.get(1));	
		}
		return result;
	}
}
