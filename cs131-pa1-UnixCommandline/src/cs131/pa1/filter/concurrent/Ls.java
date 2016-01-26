package cs131.pa1.filter.concurrent;

import java.io.File;

public class Ls extends ConcurrentFilter {
	
	@Override
	public void process(){
		String currentWorkingDirectory = ConcurrentREPL.currentWorkingDirectory;
		File directoryObject = new File(currentWorkingDirectory);
		String[] files = directoryObject.list();
		for (String file : files){
			this.output.add(file);
		}
	}
	
	@Override
	protected String processLine(String line) {
		// Should never be called, as we have overridden process.
		return null;
	}
}
