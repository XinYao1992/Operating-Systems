package cs131.pa1.filter.concurrent;

public class Wc extends ConcurrentFilter {
	
	int nLines;
	int nWords;
	int nChars;
	
	public Wc(){
		nLines = 0;
		nWords = 0;
		nChars = 0;
	}
	
	@Override
	protected String processLine(String line) {
		nLines += 1;
		
		for (String s : line.split(" ")){
			if (s.trim().length() > 0){
				nWords += 1;
			}
		}
		
		nChars += line.length();
		
		if (prev.isDone() && this.isDone()){
			return nLines + " " + nWords + " " + nChars;
		} else {
			return null;
		}
	}
}
