package exec;

public class ID {
	String name;
	boolean assigned;
	int value;
	
	//default to unassigned
	public ID(String name){
		this.name = name;
		this.assigned = false;
		
	}
	//assign value, set assigned to true
	public void assign(int value){
		assigned = true;
		this.value = value;
	}
	//return current val of ID
	//throws error if unassigned
	public int getValue(){
		if(!assigned){
			throw new IllegalStateException(name+" used before assignment");
		}
		return value;
	}
	//check equality of ID's
	public boolean equals(ID that){
		return this.name.equals(that.name);
		
	}
}
