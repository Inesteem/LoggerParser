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
	static String append_file;//needs to be sorted
	static String to_log;

	public static void main(String[] args) {
	 
		
	  	int option = iom.askTwoOptions("What do you want to parse?", "file", "folder", "Decide!");
	
		if(option == -1){
			System.exit(-1);
		}
		


			
		String tz = iom.getAfricanTimezone();
		Parser parser = new Parser(tz);
		
		if(option == 0){ //file	
			
			try { 
				to_log = iom.getInputFileName("DEFAULT_LOG_PATH", "Select a log file to parse", IOManager.InputType.FILE);
				if(to_log.length() == 0){
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
			//parser.parse(iom.getInputFilePath());
			parser.parse(to_log, iom);
			
			
			
			//parser.print_log_info();
			
			
			//System.exit(0);

			
			
		} else {
			parser.setLogFormat((LogFormat)parser.new ImpulsFormat());
			int cnt = 0;
			
			
			try { 
				to_log = iom.getInputFileName("DEFAULT_LOG_PATH", "Select a log file to parse", IOManager.InputType.DIR);
				if(to_log.length() == 0){
					iom.asError("No valid logfile specified!");
					System.exit(-1);
				} 
			} catch (java.lang.NoClassDefFoundError e){
				iom.asError("jar file missing: make sure there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
				System.exit(-1);
			}
			
			File log_dir = new File(to_log);
			
			
			for (final File fileEntry : log_dir.listFiles()) {
				if (fileEntry.isDirectory()) {
					//listFilesForFolder(fileEntry);
				} else {
					System.out.println("parsing file " + fileEntry.getName());
					if(!parser.parse(fileEntry.getAbsolutePath(), iom)) {
						System.out.println("failed to parse file; no Impuls Format");
					} else { ++cnt; }
				}
			}
			
			//parser.print_log_info();
			System.out.println("parsed " + cnt + " files");
		
		}
  		if(!iom.setOutputFileName()){
			parser.print_log_info();
		} else {
			parser.write_log_info( iom.getOutputFilePath(), iom);
			iom.getOutputFile();
		}
	  System.exit(0);
	  
  }
}
