package net.tyack.java8for.chapter1;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import org.junit.Test;

public class Chapter2Test {

	@Test
	public void exercise2isDirectoryFilterLambda() {
		File[] listFiles = new File("/Users").listFiles(f -> f.isDirectory());
		Arrays.asList(listFiles).forEach(f -> {
			assertTrue(f.isDirectory());
			System.out.println(f);
		});
	}

	@Test
	public void exercise2isDirectoryFilterFunctionalReference() {
		File[] listFiles = new File("/Users").listFiles(File::isDirectory);
		Arrays.asList(listFiles).forEach(f -> assertTrue(f.isDirectory()));
	}

}
