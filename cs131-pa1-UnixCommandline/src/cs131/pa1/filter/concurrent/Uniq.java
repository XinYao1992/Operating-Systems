package cs131.pa1.filter.concurrent;

import java.util.HashSet;
import java.util.Set;

public class Uniq extends ConcurrentFilter {

	private Set<String> seen;
	
	public Uniq(){
		seen = new HashSet<String>();
	}
	
	@Override
	protected String processLine(String line) {
		if (!seen.contains(line)){
			seen.add(line);
			return line;
		}
		return null;
	}
}
