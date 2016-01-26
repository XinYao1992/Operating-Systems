package cs131.pa1.filter.concurrent;

import java.io.File;
import java.io.IOException;

public class Cd extends ConcurrentFilter{
	// hasBeenSwapped: true if working directory has changed
	// swappingTolocation: the path that will be the new working directory
	boolean hasBeenSwapped;
	String swappingToLocation;
	
	// Constructs this Cd filter with a path
	public Cd (String path) throws IOException, MissingArgumentException, TooManyArgumentsException {
		
		// Cd must have a path to change the directory
		if (path==null) {
			throw new MissingArgumentException();
		}
		
		// Cd can only have one path
		if (path.contains(" ")) {
			throw new TooManyArgumentsException();
		}
		
		// hasBeenSwapped keeps track of when the path has been changed (true when process is done)
		hasBeenSwapped = false;
		String currentWorkingDirectory = ConcurrentREPL.currentWorkingDirectory;
		File newPath = new File(path);
		if (newPath.isAbsolute()){
			// If the entered path is absolute, then set working directory to it
			swappingToLocation = newPath.getCanonicalPath();
		} else {
			/* If relative, then append entered path onto current working directory, convert
			 * the resulting path to an absolute path, and change the working directory */
			swappingToLocation = new File(currentWorkingDirectory+FILE_SEPARATOR+newPath.getPath()).getCanonicalPath();
		}	
		
		File f = new File (swappingToLocation);
		if (!f.exists() || !f.isDirectory()){
			throw new IOException();
		}
	}
	
	// Executes the CD command, which has not occurred before
	// this method is called.
	public void process(){
		ConcurrentREPL.currentWorkingDirectory = swappingToLocation;
		hasBeenSwapped = true;
	}
	
	@Override
	protected String processLine(String line) {
		// Should never be called, as we have overridden process.
		return null;
	}
}
