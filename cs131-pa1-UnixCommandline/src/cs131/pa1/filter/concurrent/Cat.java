package cs131.pa1.filter.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import cs131.pa1.filter.concurrent.ConcurrentREPL;

public class Cat extends ConcurrentFilter {

	private List<Scanner> scanners;
	private int scannerNumber;
	
	public Cat(String fileNames) throws FileNotFoundException, MissingArgumentException{
		
		if (fileNames==null) {
			throw new MissingArgumentException();
		}
		
		String cwd = ConcurrentREPL.currentWorkingDirectory;
		scanners = new ArrayList<Scanner>();
		scannerNumber = 0;
		// parses the string of filenames by whitespace
		for (String fileName : Arrays.asList(fileNames.split("\\s+"))){
			Scanner fileScanner = new Scanner(new File(cwd + FILE_SEPARATOR + fileName));
			scanners.add(fileScanner);
		}
	}

	@Override
	public void process(){
		while (!isDone()){
			Scanner scan = scanners.get(scannerNumber);
			while (scan.hasNextLine()){
				output.add(scan.nextLine());
			}
			scannerNumber++;
		}
	}
	
	@Override
	protected String processLine(String line) {
		// This method should never be called, as we are overriding the
		// process method itself.
		return null;
	}

	@Override
	public boolean isDone() {
		return scanners.size() <= scannerNumber;
	}
}
