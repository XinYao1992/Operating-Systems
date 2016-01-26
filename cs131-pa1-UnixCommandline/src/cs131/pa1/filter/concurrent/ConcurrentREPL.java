package cs131.pa1.filter.concurrent;

import java.util.List;
import java.util.Scanner;
import cs131.pa1.filter.Message;
import cs131.pa1.filter.concurrent.ConcurrentCommandBuilder;
import cs131.pa1.filter.concurrent.ConcurrentFilter;

public class ConcurrentREPL {

	static String currentWorkingDirectory;
	
	public static void main(String[] args){
		
		currentWorkingDirectory = System.getProperty("user.dir");
		System.out.print(Message.WELCOME);
		Scanner console = new Scanner(System.in);
		System.out.print(Message.NEWCOMMAND);
		while (console.hasNextLine()){
			String command = console.nextLine();
			if (command.equals("exit")){
				console.close();
				System.out.print(Message.GOODBYE);
				return;
			} else {
				List<ConcurrentFilter> filters = ConcurrentCommandBuilder.createFiltersFromCommand(command);
				startFilters(filters);
			}
			System.out.print(Message.NEWCOMMAND);
		}
		console.close();
	}
	
	public static void startFilters(List<ConcurrentFilter> filters){
		for (ConcurrentFilter filter : filters){
			filter.process();
		}
	}
}
