package src.parser;

public class LogInfo {
	
	String line;
	
    public LogInfo(){
		line = "";
	} 
	public void add_val(String line){
		this.line = line;
	}
	
	public String get_val(){
		return this.line;
	}
}
