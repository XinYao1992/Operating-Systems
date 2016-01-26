package cs131.pa1.test;



import cs131.pa1.filter.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs131.pa1.filter.concurrent.ConcurrentREPL;

public class TextProcessingTests {
	
	// Tests for cat command
	
	@Test
	public void testCat(){
		testInput("cat hello-world.txt\nexit");
		ConcurrentREPL.main(null);
		assertOutput(Message.NEWCOMMAND + "hello\nworld\n");
	}
	
	@Test
	public void testMultiCat(){
		testInput("cat hello-world.txt world.txt hello.txt\nexit");
		ConcurrentREPL.main(null);
		assertOutput(Message.NEWCOMMAND + "hello\nworld\nWORLD\nHELLO\n");
	}
	
	@Test
	public void testCatFileNotFound(){
		testInput("cat doesnt-exist.txt\nexit");
		ConcurrentREPL.main(null);
		assertOutput(Message.NEWCOMMAND + "At least one of the files in the command [cat doesnt-exist.txt] was not found.\n");
	}
	
	@Test
	public void testMultiCatFileNotFound(){
		testInput("cat world.txt hello.txt doesnt-exist.txt\nexit");
		ConcurrentREPL.main(null);
		assertOutput(Message.NEWCOMMAND + "At least one of the files in the command [cat world.txt hello.txt doesnt-exist.txt] was not found.\n");
	}
	
    @Test
	public void testReadPreviousFile(){
		testInput("cat hello.txt hello.txt > hello2.txt\ncat hello2.txt\nexit");
		ConcurrentREPL.main(null);
		assertOutput(Message.NEWCOMMAND.toString() + Message.NEWCOMMAND.toString() + "HELLO\nHELLO\n");
	}
	
	// Basic test for grep command
	
	@Test
	public void testGrep(){
		testInput("cat fizz-buzz-10000.txt | grep 111\nexit");
		ConcurrentREPL.main(null);
		assertOutput(Message.NEWCOMMAND + "1111\n1112\n1114\n1117\n1118\n2111\n4111\n5111\n7111\n8111\n");
	}
	
	// Best test for uniq
	
	@Test
	public void testUniq(){
		testInput("cat fizz-buzz-10000.txt | grep Fizz | uniq\nexit");
		ConcurrentREPL.main(null);
		assertOutput(Message.NEWCOMMAND + "FizzBuzz\nFizz\n");
	}
	
	// Basic test for wc (word count)
	
	@Test
	public void testWc(){
		testInput("cat fizz-buzz-10000.txt | wc\nexit");
		ConcurrentREPL.main(null);
		assertOutput(Message.NEWCOMMAND + "10001 10001 42081\n");
	}
	
	
	
	
	
	
	// Boilerplate, standard across test case files.
	
	private ByteArrayInputStream inContent;
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	
	public void testInput(String s){
		inContent = new ByteArrayInputStream(s.getBytes());
		System.setIn(inContent);
	}
	
	public void assertOutput(String expected){
		AllConcurrentTests.assertOutput(expected, outContent);
	}
	
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@After
	public void cleanUpStreams() {
		System.setIn(null);
	    System.setOut(null);
	    System.setErr(null);
	}
}
