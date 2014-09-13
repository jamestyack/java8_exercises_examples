package net.tyack.java8for.chapter1;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class Chapter2Test {

	@Test
	public void exercise2lambda() {
		File[] listFiles = new File("/Users").listFiles(f -> f.isDirectory());
		List<File> files = Arrays.asList(listFiles);
		files.forEach(System.out::println);
		System.out.println("Total folders = " + files.size());
	}
	
	@Test
	public void exercise2functionalReference() {
		File[] listFiles = new File("/Users").listFiles(File::isDirectory);
		List<File> files = Arrays.asList(listFiles);
		files.forEach(System.out::println);
		System.out.println("Total folders = " + files.size());
	}

}
