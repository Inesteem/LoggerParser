// javac -cp "\.;lib\*;" LoggerParser.java
//	java -cp "\.;lib\*;" LoggerParser
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\lib\*
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\parser\*.class .\lib\*


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.io.File;

public class LoggerParser {
									
	static IOManager iom = new IOManager();		
	static String log_file;
	static String append_file;//needs to be sorted

	public static void main(String[] args) {
	 
		
	  	int option = iom.askTwoOptions("What do you want to parse?", "file", "folder", "Decide!");
	
		if(option == -1){
			System.exit(-1);
		}
		if(option == 0){ //file
			
			try { 
				log_file = iom.getInputFileName("DEFAULT_LOG_PATH", "Select a log file to parse");
				if(log_file.length() == 0){
					iom.asError("No valid logfile specified!");
					System.exit(-1);
				} 
			//	append_file = iom.getInputFileName("DEFAULT_APPEND_PATH", "Select the file to append to");
			//	if(append_file.length() == 0){
			//		iom.asError("No valid appendfile specified!");
			//		System.exit(-1);
			//	} 
			} catch (java.lang.NoClassDefFoundError e){
				iom.asError("jar file missing: make sure there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
				System.exit(-1);
			}

			
			String tz = iom.getAfricanTimezone();
			Parser parser = new Parser(tz);
			parser.setLogFormat((LogFormat)parser.new ImpulsFormat());
			//parser.parse(iom.getInputFilePath());
			parser.parse(log_file, iom);
			
			
			
			parser.print_log_info();
			if(true){ System.exit(0);}
			
			
			
			if(!iom.setOutputFileName()){
		//		parser.print_log_info();
			} else {
		//		parser.write_log_info( iom.getOutputFilePath(), iom);
				iom.getOutputFile();
			}
			
			
		} else {
			System.out.println("not implemented yet!");
		}
	  
	  
	  
  }
}
