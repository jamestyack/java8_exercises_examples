package net.tyack.java8for.chapter1;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

/**
 * @author jamestyack www.tyack.net
 * These are my answers to questions from the excellent Java SE 8  for the Really Impatient:-
 * 
 * See:- Horstmann, Cay S. (2014-01-10). Java SE 8 for the Really Impatient (Kindle Location 222). Pearson Education. Kindle Edition. 
 *
 * Chapter 1 sample answers
 *
 */
public class Chapter1Test {
	
	private static final String PATH_TO_FOLDER_ON_LOCAL_FILESYSTEM = "/Users";

	/**
	 * Filter a folder to only list sub-folders - use a lambda
	 * JUnit assertion is also in form of lambda
	 */
	@Test
	public void exercise2isDirectoryFilterLambda() {
		File[] listFiles = new File(PATH_TO_FOLDER_ON_LOCAL_FILESYSTEM).listFiles(f -> f.isDirectory());
		Arrays.asList(listFiles).forEach(f -> {
			assertTrue(f.isDirectory());
			System.out.println(f); // nice to have and shows lambda block
		});
	}

	/**
	 * Filter a folder to only list sub-folders - use a functional reference
	 */
	@Test
	public void exercise2isDirectoryFilterFunctionalReference() {
		File[] listFiles = new File(PATH_TO_FOLDER_ON_LOCAL_FILESYSTEM).listFiles(File::isDirectory);
		Arrays.asList(listFiles).forEach(f -> assertTrue(f.isDirectory()));
	}

}
