// javac -cp "\.;lib\*;" parser/*.java LoggerParser.java
//	java -cp "\.;lib\*;" LoggerParser
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\lib\*
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\parser\*.class .\lib\*
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import parser.*;

public class LoggerParser {
									
	static IOManager iom = IOManager.getInstance();
//
//	public void runPython() { //need to call myscript.py and also pass arg1 as its arguments.
//		//and also myscript.py path is in C:\Demo\myscript.py
//
//		String[] cmd = {
//			"python",
//			"C:\Users\Saphira\Documents\Andis Programm\PythonParser\parser.py",
//			"test",
//		};
//		Runtime.getRuntime().exec(cmd);
//	}

	static String append_file;//needs to be sorted
	static String to_log;

	static	void parse_dir(int option, File log_dir, Parser parser, String output_path){
		if(option == 1){
			iom.createDir( output_path);
		}

		int cnt = 0;

		for (final File fileEntry : log_dir.listFiles()) {
			if (fileEntry.isDirectory()) {
				parse_dir(option,fileEntry,parser, output_path + "\\" + fileEntry.getName());
				//listFilesForFolder(fileEntry);
			} else {
				if(option == 1){
					 iom.setOutputFileName( output_path + "\\" + fileEntry.getName() );
					 iom.createFile( output_path + "\\" + fileEntry.getName() );

				}
				System.out.println("parsing file " + fileEntry.getName());
				if(!parser.parse(fileEntry.getAbsolutePath())) {
					System.out.println("failed to parse file");
				} else { ++cnt; }
			}
		}

		System.out.println("parsed " + cnt + " files in dir " + log_dir.getName());
		System.exit(0);
	}



	public static void main(String[] args) {
	 
		
	  	int option = iom.askTwoOptions("What do you want to parse?", "file", "folder", "","DEFAULT_OPT_FILE_FOLDER");
	
		if(option == -1){
			System.exit(-1);
		}
		


			
		String tz = iom.getAfricanTimezone();
		Parser parser = new Parser(tz);
		

		if(option == 0){ //file	
				
			
			try { 
				to_log = iom.getInputFileName("DEFAULT_LOG_PATH", "Select a log file to parse", IOManager.FileType.FILE);
				if(to_log.length() == 0){
					iom.asError("No valid logfile specified!");
					System.exit(-1);
				} 

				if(iom.getOutputFileName("DEFAULT_OUT_PATH","Select an output file",IOManager.FileType.FILE).length() == 0){
				iom.asError("No valid output file specified!");
				System.exit(0);
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
			parser.parse(to_log);
			
			
			
			parser.print_log_info();
			
			
			//System.exit(0);

			
		} else {
		//	parser.setLogFormat((LogFormat)parser.new ImpulsFormat());


			
			try { 
				to_log = iom.getInputFileName("DEFAULT_LOG_PATH_1", "Select a log dir to parse", IOManager.FileType.DIR);
				if(to_log.length() == 0){
					iom.asError("No valid logfile specified!");
					System.exit(-1);
				} 
			} catch (java.lang.NoClassDefFoundError e){
				iom.asError("jar file missing: make sure there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
				System.exit(-1);
			}

			option = iom.askTwoOptions("How to handle the files?", "combined", "individually", "","DEFAULT_OPT_COMB_INDIV");
		
			if (option == 0) {//combined
				if(iom.getOutputFileName("DEFAULT_OUT_PATH_1","Select an output file",IOManager.FileType.FILE).length() == 0){
					iom.asError("No valid output file specified!");
					System.exit(0);
				}
			} else if (option==1) {
				if(iom.getOutputFileName("DEFAULT_OUT_PATH_2","Select an output dir",IOManager.FileType.DIR).length() == 0){
					iom.asError("No valid output path specified!");
					System.exit(0);
				}
			} else {
				System.exit(-1);
			}

			
			File log_dir = new File(to_log);
		        parse_dir(option,log_dir,parser,iom.getOutputFilePath());
			
			//parser.print_log_info();

		}
  	//	if(!iom.getOutputFileName("DEFAULT_OUT_PATH")){
	//		parser.print_log_info();
	//	} else {
			parser.write_log_info();
			iom.getOutputFile();
	//	}
	  System.exit(0);
	  
  }
}
