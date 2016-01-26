package cs131.pa1.filter.concurrent;

import java.util.LinkedList;

public class OutPrinter extends ConcurrentFilter {
	
	private boolean standardError = false;
	
	public OutPrinter() {
		super();
	}
	
	public OutPrinter(String errorMessage) {
		standardError = true;
		input = new LinkedList<String>();
		input.add(errorMessage);
	}
	
	public boolean isStandardError() {
		return standardError;
	}
	
	@Override
	protected String processLine(String line) {
		System.out.println(line);
		return null;
	}	
}
