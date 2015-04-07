package net.tyack.java8for.chapter1;

import static org.junit.Assert.assertTrue;

import java.awt.Button;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class Chapter1LamdhasTest {
	
	private static final String PATH_TO_FOLDER_ON_LOCAL_FILESYSTEM = "/Users";

	@Test
	public void chapter1Sort1() {
		String[] words = { "catch", "catching", "cat", "cats", "dog", "eating"};
		Arrays.sort(words, (word1, word2) -> Integer.compare(word1.length(), word2.length()));
		Set<String> mySet = new LinkedHashSet<>();
		Arrays.asList(words).forEach((word)->mySet.add(word));
		System.out.println(mySet);
	}
	
	@Test
	public void chapter1Sort2() {
		String[] words = new String[] { "Catch", "Catching", "Cat", "cats", "dog", "eating"};
		Arrays.sort(words, String::compareToIgnoreCase);
		Set<String> mySet = new LinkedHashSet<>();
		Arrays.asList(words).forEach((word)->mySet.add(word));
		System.out.println(mySet);
	}
	/**
	 * Use constructor to create list of buttons using each label
	 */
	@Test
	public void chapter1ConstructorReferences() {
		String[] labels = {"title","address","tel"};
		List<String> labelList = new ArrayList<>(Arrays.asList(labels));
		Stream<Button> stream = labelList.stream().map(Button::new);
		List<Button> buttons = stream.collect(Collectors.toList());
		System.out.println(labelList);
	}
	
	@Test
	public void chapter1ConstructorRef2() {
		String[] labels = {"title","address","tel"};
		List<String> labelList = new ArrayList<>(Arrays.asList(labels));
		Stream<Button> stream = labelList.stream().map(Button::new);
		List<Button> buttons = stream.collect(Collectors.toList());
		System.out.println(labelList);
	}
	
	/**
	 * Filter a folder to only list sub-folders - use a lambda
	 * JUnit assertion is also in form of lambda
	 */
	@Test
	public void exercise2isDirectoryFilterLambda() {
		File[] listFiles = new File(PATH_TO_FOLDER_ON_LOCAL_FILESYSTEM).listFiles(f -> f.isDirectory());
		Arrays.asList(listFiles).forEach(f -> assertTrue(f.isDirectory()));
		// functionally the same but uses lambda block syntax...
		Arrays.asList(listFiles).forEach(f -> {
			assertTrue(f.isDirectory());
			System.out.println(f);
		});
	}

	/**
	 * Filter a folder to only list sub-folders ...functional reference
	 */
	@Test
	public void exercise2isDirectoryFilterFunctionalReference() {
		File[] listFiles = new File(PATH_TO_FOLDER_ON_LOCAL_FILESYSTEM).listFiles(File::isDirectory);
		Arrays.asList(listFiles).forEach(f -> assertTrue(f.isDirectory()));
	}

}
