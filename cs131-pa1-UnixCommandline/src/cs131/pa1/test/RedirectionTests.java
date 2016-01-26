package cs131.pa1.test;


import cs131.pa1.filter.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import cs131.pa1.filter.concurrent.ConcurrentREPL;


public class RedirectionTests {

	@Test
	public void testCatRedirected(){
		testInput("cat hello-world.txt > new-hello-world.txt\nexit");
		ConcurrentREPL.main(null);
		assertFileContentsEquals("new-hello-world.txt", "hello\nworld\n");
		assertOutput(Message.NEWCOMMAND.toString());
		if (!AllConcurrentTests.DEBUGGING_MODE){
			destroyFile("new-hello-world.txt");
		}
	}
	
	@Test
	public void testComplexRedirection(){
		testInput("cat fizz-buzz-10000.txt | uniq | grep F > trial-file.txt\nexit");
		ConcurrentREPL.main(null);
		assertFileContentsEquals("trial-file.txt", "FizzBuzz\nFizz\n");
		assertOutput(Message.NEWCOMMAND.toString());
		if (!AllConcurrentTests.DEBUGGING_MODE){
			destroyFile("trial-file.txt");
		}
	}
	
	@Test
	public void testDirectoryShiftedRedirection(){
		testInput("cd dir1\nls > folder-contents.txt\nexit");
		ConcurrentREPL.main(null);
		assertFileContentsEquals("dir1/folder-contents.txt", "dir2\nf1.txt\nfolder-contents.txt\n");
		assertOutput(Message.NEWCOMMAND.toString() + Message.NEWCOMMAND.toString());
		if (!AllConcurrentTests.DEBUGGING_MODE){
			destroyFile("dir1/folder-contents.txt");
		}
	}
	
	
	
	private static void assertFileContentsEquals(String fileName, String expected){
		File f = new File(fileName);
		try {
			Scanner scan = new Scanner(f);
			String result = "";
			while (scan.hasNextLine()){
				result += scan.nextLine() + "\n";
			}
			scan.close();
			assertEquals(expected, result);
		} catch (FileNotFoundException e) {
			assertTrue(false);
		}
	}
	
	// Cleanup message
	private static void destroyFile(String fileName){
		File f = new File(fileName);
		if (f.exists()){
			f.delete();
		}
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
