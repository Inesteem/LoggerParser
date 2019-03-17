
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Desktop;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.Action;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;



import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.charset.Charset;

import java.io.IOException;
import java.io.File;

import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;


public class IOManager {

    static class SelectAll extends TextAction
    {
        public SelectAll()
        {
            super("Select All");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
        }

        public void actionPerformed(ActionEvent e)
        {
            JTextComponent component = getFocusedComponent();
            component.selectAll();
            component.requestFocusInWindow();
        }
    }

	private String inputFile; 
	private String inputFileName; 
	private String outputFile;
	private String website;
	private boolean append;
	public final Color ACTIVE_COLOUR = Color.BLACK;
	public final Color INACTIVE_COLOUR = Color.GRAY;
	public IOManager(){
		inputFile = null;
		outputFile = null;
		append = false;
	}
	
	//Getter Methods
	public String getInputFilePath(){
		return inputFile;
	}
	public String getInputFileName(boolean withExt){
		if(withExt){return inputFileName;}
		
		try { 
			return FilenameUtils.removeExtension(inputFileName);
		} catch (java.lang.NoClassDefFoundError e){
			asError("jar file missing: make sure there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
			System.exit(-1);
		}
		return "";
	}
	public String getOutputFilePath(){
		return outputFile;
	}
	
	public String getWebsiteStr(){
		return website;
	}

	
	public String getWebsite(){
		String strD = null;
		Object[] options1 = { "Parse website", "Exit"};
		String web_default = "http://www.hydrosciences.fr/sierem/";
        JPanel panel = new JPanel();
        panel.add(new JLabel("Website:"));
  //     JTextField textField = new JTextField("http://www.hydrosciences.fr/sierem/v2/graphSC.asp?SCID=TZPR114", 100);//VERSION7
 //      JTextField textField = new JTextField("http://www.hydrosciences.fr/sierem/v2/graphSC.asp?SCID=DZPR017", 100); //VERSION6
       JTextField textField = new JTextField("http://www.hydrosciences.fr/sierem/v2/graphSC.asp?SCID=TZPR575", 100); //int exception

		textField.setForeground(INACTIVE_COLOUR);
		textField.addFocusListener(new FocusListener() {
			
			boolean def_val = true;
			@Override
			public void focusGained(FocusEvent e) {
				if(def_val){textField.setText("");}
				def_val = false;
				textField.setForeground(ACTIVE_COLOUR);
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				textField.setForeground(INACTIVE_COLOUR);
				// nothing
			}
		});
        panel.add(textField);
        
		//Popup
		JPopupMenu menu = new JPopupMenu();
		
        Action paste = new DefaultEditorKit.PasteAction();
        paste.putValue(Action.NAME, "Paste");
        paste.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
        menu.add( paste );
        
        Action cut = new DefaultEditorKit.CutAction();
        cut.putValue(Action.NAME, "Cut");
        cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
        menu.add( cut );

        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, "Copy");
        copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        menu.add( copy );


        Action selectAll = new SelectAll();
        menu.add( selectAll );

        textField.setComponentPopupMenu( menu );		

		//popup end
        
		while(website == null){
				
	        int result = JOptionPane.showOptionDialog(null, panel, "Enter the charts weblink",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options1, options1[0]);
                
				website = textField.getText();		
                
			if (result != JOptionPane.YES_OPTION || website == null){
					return null;
			}
            //JOptionPane.showMessageDialog(null, textField.getText());
			if (!website.startsWith(web_default)){
				Object[] options = {"Let's try it!","Redo","I'm out!"};
				//int confirmed = JOptionPane.showConfirmDialog(null, 
				//"The file " + FilenameUtils.getName(outputFile) + " already exists; are you sure you want to override it?", "Decide wisely...",
				//	JOptionPane.YES_NO_OPTION);
				int confirmed = JOptionPane.showOptionDialog(null, "The website " + website + 
				  " seems weard to me. Sure its the link to a chart?", 
				  "Decide wisely...", 
				  JOptionPane.YES_NO_CANCEL_OPTION, 
				  JOptionPane.WARNING_MESSAGE, 
				  null, options, options[0]);
				if (confirmed == JOptionPane.NO_OPTION) {
					website = null;
				} else if (confirmed == JOptionPane.CANCEL_OPTION) {
					return null;
				} else if (confirmed == JOptionPane.CLOSED_OPTION) {
					return null;
				}
			
			} 
		}
		return website; 	 
	}
	
	String getInputFileName(String default_path, String message) throws java.lang.NoClassDefFoundError {
		 Preferences pref = Preferences.userRoot();
		// Retrieve the selected path or use
		// an empty string if no path has
		// previously been selected
		String pref_path = pref.get(default_path, "");


	//	JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		JFileChooser jfc = new JFileChooser();
		if(pref_path != null) jfc.setCurrentDirectory(new File(pref_path));
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle(message);
		
		//jfc.setAcceptAllFileFilterUsed(false);
		//FileNameExtensionFilter filter = new FileNameExtensionFilter("*.kml", "kml");
		//jfc.addChoosableFileFilter(filter);

		int returnValue = jfc.showOpenDialog(null);
		if(returnValue != JFileChooser.APPROVE_OPTION) { return "";} //CANCEL_OPTION

		File selectedFile = jfc.getSelectedFile();
		if (selectedFile == null){
			JOptionPane.showMessageDialog(null, "No input file selected. Exiting.","IQ Error", JOptionPane.ERROR_MESSAGE);
			return "";
		} 
		inputFile = selectedFile.getAbsolutePath();
		pref.put(default_path, FilenameUtils.getFullPath(inputFile));
		
		inputFileName = inputFile.substring(inputFile.lastIndexOf(File.separator)+1);
		inputFileName = FilenameUtils.getName(inputFile);

		return selectedFile.getAbsolutePath();
	}
	

	public void createFile(File filepath){		
		try {
			filepath.createNewFile(); // if file already exists will do nothing 
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Could not create output file: " + e.getMessage() + "...exiting!", "IO Error", JOptionPane.ERROR_MESSAGE);
			 System.exit(-1);
		}
	}

	public boolean setOutputFileName () throws java.lang.NoClassDefFoundError {
		Preferences pref = Preferences.userRoot();
		// Retrieve the selected path or use
		// an empty string if no path has
		// previously been selected
		String pref_path = pref.get("DEFAULT_OUTPUT_PATH", "");
		//JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		JFileChooser jfc = new JFileChooser(FilenameUtils.getFullPath(inputFile));
		jfc.setDialogTitle("Choose a output file: ");
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(inputFileName != null){
			jfc.setSelectedFile(new File(inputFileName));
		} else { 
			jfc.setSelectedFile(new File("log.txt"));
		}
		
		boolean choosen = false;
		while(!choosen){

			int returnValue = jfc.showOpenDialog(null);
			if(returnValue != JFileChooser.APPROVE_OPTION) { return false;} //CANCEL_OPTION

			File selectedFile = jfc.getSelectedFile();
			if (selectedFile == null){
				JOptionPane.showMessageDialog(null, "No output file selected. Exiting.","IQ Error", JOptionPane.ERROR_MESSAGE);
				return false;
			} 
			
			outputFile = selectedFile.getAbsolutePath();
			
			if (selectedFile.exists()){
				Object[] options = {"Override",
									"Select again","42"};
				//int confirmed = JOptionPane.showConfirmDialog(null, 
				//"The file " + FilenameUtils.getName(outputFile) + " already exists; are you sure you want to override it?", "Decide wisely...",
				//	JOptionPane.YES_NO_OPTION);
				int confirmed = JOptionPane.showOptionDialog(null, "The file " + selectedFile.getName() + 
                  " already exists. Do you want to replace the existing file?", 
                  "Decide wisely...", 
                  JOptionPane.YES_NO_CANCEL_OPTION, 
                  JOptionPane.WARNING_MESSAGE, 
                  null, options, options[0]);
				if (confirmed == JOptionPane.YES_OPTION) {
					choosen = true;
				} else if (confirmed == JOptionPane.CANCEL_OPTION) {
					choosen = true;
					append = true;
				} else if (confirmed == JOptionPane.CLOSED_OPTION) {
					JOptionPane.showMessageDialog(null, "No output file selected. Exiting.","IQ Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
			} else {
				choosen = true;
				createFile(selectedFile);
			}
		}
		pref.put("DEFAULT_OUTPUT_PATH", FilenameUtils.getFullPath(outputFile));
		
		//System.out.println("You have chosen " + outputFile);		

		return true;
	}	

	public void setOutputFileName(String path){
		if(path != null){outputFile = path;}
	}
	public boolean writeData(List<String> lines, boolean app){
		if(outputFile == null){
			return false;
		}
		Path file = Paths.get(outputFile);
		
		try {
			if(app || append){  
				Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			} else { 
				Files.write(file, lines, Charset.forName("UTF-8"));
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Could not write to file: " + e.getMessage() + "...exiting!", "IO Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		return true;
	}

	
	
	public File getOutputFile(){	
		if(outputFile == null) new File("tmp.txt");
		File file = new File(outputFile);
		try {
			if(file.exists()){ 
				if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				  String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
				  Runtime.getRuntime().exec(cmd);
				} else {
					Desktop.getDesktop().edit(file);
				}
			
			} else {
				JOptionPane.showMessageDialog(null, "File does not exist ... but why?", "IO Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Could not open output file: " + e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
		}
		return file;
	}
	
	public static void asError(String msg){
		JOptionPane.showMessageDialog(null, msg , "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}
	public static void asMessage(String msg){
		JOptionPane.showMessageDialog(null, msg);
	}
	public static void asWarning(String msg){
		JOptionPane.showMessageDialog(null, msg,"Warning",JOptionPane.WARNING_MESSAGE);
	}
	
	public boolean createDir(File path){
		// Create a directory; all non-existent ancestor directories are
		// automatically created
		if (path.exists()) {
			return true;
		}
		boolean success = path.mkdirs();
		if(!success){asError("could not create save dir: " + path);}
		return success;	
	}
	public static String legalFileName(String filename){
		filename = filename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

		//trim left spaces:
		filename = filename.replaceAll("^\\s+", "");

		//trim right spaces:
		filename = filename.replaceAll("\\s+$", "");


		//Remove all other spaces
		filename = filename.replaceAll("\\s+","_");
		return filename;
	}
	
	public int askTwoOptions(String header, String op1, String op2, String dialogstr){
		Object[] options = {op1,op2};
		//int confirmed = JOptionPane.showConfirmDialog(null, 
		//"The file " + FilenameUtils.getName(outputFile) + " already exists; are you sure you want to override it?", "Decide wisely...",
		//	JOptionPane.YES_NO_OPTION);
		int confirmed = JOptionPane.showOptionDialog(null, dialogstr, 
		  header, 
		  JOptionPane.YES_NO_OPTION, 
		  JOptionPane.WARNING_MESSAGE, 
		  null, options, options[0]);
		if (confirmed == JOptionPane.YES_OPTION) {
			return 0;
		}
		if (confirmed == JOptionPane.NO_OPTION) {
			return 1;
		}
		return -1;
	}
	
	//public void copyFile(String file, String copy) 
	//	throws IOException {
  
    //Path copied = Paths.get("src/test/resources/copiedWithNio.txt");
    //Path originalPath = original.toPath();
    //Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
  
    //assertThat(copied).exists();
    //assertThat(Files.readAllLines(originalPath)
    //  .equals(Files.readAllLines(copied)));
	//}
	
	
}
