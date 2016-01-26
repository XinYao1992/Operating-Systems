package cs131.pa1.filter.concurrent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cs131.pa1.filter.Message;

public class ConcurrentCommandBuilder {
	
	// commands that requires piped input
	private final static Set<String> PIPES_IN = new HashSet<String>(Arrays.asList(new String[] {"Grep","Uniq","Wc","Fileprinter","OutPrinter"}));
	// commands that produces piped output
	private final static Set<String> PIPES_OUT = new HashSet<String>(Arrays.asList(new String[] {"Cat","Ls","Pwd","Grep","Wc","Uniq"}));
	
	// Returns a list of concurrentFilters, one for each command and one for the output type
	public static List<ConcurrentFilter> createFiltersFromCommand(String command){	
		
		// Determining whether to print to console or file and removing redirection from command
		ConcurrentFilter finalFilter = determineFinalFilter(command);
		command = adjustCommandToRemoveFinalFilter(command);		
		
		//parse the input command based on pipes and create a list of filters
		List<String> subCommands = Arrays.asList(command.split("\\|"));
		List<ConcurrentFilter> filterPipeline = new ArrayList<ConcurrentFilter>();
		
		for (String subCommand : subCommands){
			ConcurrentFilter subFilter = constructFilterFromSubCommand(subCommand.trim());
			ConcurrentFilter prevFilter = !filterPipeline.isEmpty() ? filterPipeline.get(filterPipeline.size()-1) : null;
			
			// subCommandError is true if: command not found, invalid argument, file/directory not found
			boolean subCommandError = (subFilter instanceof OutPrinter) && ((OutPrinter)subFilter).isStandardError();
			if (subCommandError) {
				filterPipeline.clear();
				filterPipeline.add(subFilter);
				return filterPipeline;
			}
			// invalidPipe is true if the piping order is invalid
			ConcurrentFilter invalidPipe = ioInvalid(prevFilter, subFilter, subCommand);
			if (invalidPipe != null) {
				filterPipeline.clear();
				filterPipeline.add(invalidPipe);
				return filterPipeline;
			}
			filterPipeline.add(subFilter);
		}
		
		filterPipeline.add(finalFilter);
		
		linkFilters(filterPipeline);
		return filterPipeline;
	}

	// Returns an output filter with error message if there is a piping error
	private static ConcurrentFilter ioInvalid(ConcurrentFilter fromFilter, ConcurrentFilter toFilter, String subCommand) {
		boolean providesOutput = (fromFilter!=null) && PIPES_OUT.contains(fromFilter.getClass().getSimpleName());
		boolean requiresInput = PIPES_IN.contains(toFilter.getClass().getSimpleName());
		if (providesOutput && !requiresInput) {
			return new OutPrinter(Message.NO_INPUT.with_parameter(subCommand));
		} else if (!providesOutput && requiresInput) {
			return new OutPrinter(Message.REQUIRES_INPUT.with_parameter(subCommand));
		}
		return null;
	}
	
	//link the filters' input and output queue
	private static void linkFilters(List<ConcurrentFilter> filters){	
		for (int i = 0; i < filters.size() - 1; i++){
			filters.get(i).setNextFilter(filters.get(i+1));
		}
	}

	// parses subcommands and returns a concurrent filter
	private static ConcurrentFilter constructFilterFromSubCommand(String subCommand){
		String[] parsed = subCommand.split(" ",2);
		String cmd = parsed[0].toLowerCase();
		String param = (parsed.length>1)? parsed[1].trim():null;
			
		switch(cmd) {
		case "cd":
			try {
				return new Cd(param);
			} catch (IOException e) {
				return new OutPrinter(Message.DIRECTORY_NOT_FOUND.with_parameter(subCommand));
			} catch (MissingArgumentException e) {
				return new OutPrinter(Message.MISSING_ARGUMENT.with_parameter(subCommand));
			} catch (TooManyArgumentsException e) {
				return new OutPrinter(Message.TOO_MANY_ARGUMENTS.with_parameter(subCommand));
			}
		case "cat":
			try {
				return new Cat(param);
			} catch (FileNotFoundException e) {
				return new OutPrinter(Message.FILE_NOT_FOUND.with_parameter(subCommand));
			} catch (MissingArgumentException e) {
				return new OutPrinter(Message.MISSING_ARGUMENT.with_parameter(subCommand));
			}
		case "grep":
			try {
				return new Grep(param);
			} catch (MissingArgumentException e) {
				return new OutPrinter(Message.MISSING_ARGUMENT.with_parameter(subCommand));
			} catch (TooManyArgumentsException e) {
				return new OutPrinter(Message.TOO_MANY_ARGUMENTS.with_parameter(subCommand));
			}
		case "pwd":
			return new Pwd();
		case "ls":
			return new Ls();
		case "wc":
			return new Wc();
		case "uniq":
			return new Uniq();
		default:
			return new OutPrinter(Message.COMMAND_NOT_FOUND.with_parameter(subCommand));
		}	
	}
	
	private static String adjustCommandToRemoveFinalFilter(String command){
		if (command.contains(">")){
			return command.substring(0, command.lastIndexOf(">"));
		} else {
			return command;
		}
	}
	
	private static ConcurrentFilter determineFinalFilter(String command){
		if (command.contains(">")){
			String fileName = command.substring(command.lastIndexOf(">") + 1).trim();
			return new FilePrinter(fileName);
		}
		return new OutPrinter();
	}
}
