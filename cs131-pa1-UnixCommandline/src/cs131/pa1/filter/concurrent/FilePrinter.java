package cs131.pa1.filter.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class FilePrinter extends ConcurrentFilter {
	private PrintStream printWriter;
	
	public FilePrinter(String outputFileName){
		try {
			String cwd = ConcurrentREPL.currentWorkingDirectory;
			File f = new File(cwd + "/" + outputFileName);
			printWriter = new PrintStream(f);
		} catch (FileNotFoundException e) {
			// We should never get to this point, as we are creating a
			// new file for the purposes of printing, worst case we 
			// overwrite.
		}	
	}
	
	@Override
	protected String processLine(String line) {
		printWriter.println(line);
		isDone();
		return null;
	}
	
	public boolean isDone(){
		if (input.isEmpty()){
			printWriter.close();
			return true;
		}
		return false;
	}
}
