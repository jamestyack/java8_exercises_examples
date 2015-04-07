package net.tyack.java8for.codingExercises.wordsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class WordSearchTest {

	@Test
	public void test() {
		char[][] testMatrix = {
				// 0    1    2    3    4    5    6
				{ 'a', 'a', 'a', 'a', 'e', 'a', 'a' }, // 0
				{ 'a', 'p', 'a', 'e', 'g', 'g', 's' }, // 1
				{ 'a', 'p', 'p', 'a', 'g', 'a', 'a' }, // 2
				{ 'a', 'l', 'a', 'l', 's', 'a', 'a' }, // 3
				{ 'n', 'e', 'n', 'a', 'e', 'a', 'a' }, // 4
				{ 'i', 'i', 'a', 'a', 'a', 'a', 'p' }, // 5
				{ 'j', 'i', 'n', 'a', 'a', 'a', 'p' }, // 6
		};
		
		WordSearch ws = new WordSearch(testMatrix);
		
		List<WordPosition> wordPositionsApple = ws.findWord("apple");
		assertEquals(2, wordPositionsApple.size());
		assertTrue(wordPositionsApple.contains(new WordPosition(0,0,4,4)));
		assertTrue(wordPositionsApple.contains(new WordPosition(1,0,1,4)));
		
		List<WordPosition> wordPositionsEggs = ws.findWord("eggs");
		assertEquals(2, wordPositionsEggs.size());
		assertTrue(wordPositionsEggs.contains(new WordPosition(4,0,4,3)));
		assertTrue(wordPositionsEggs.contains(new WordPosition(3,1,6,1)));
		
		List<WordPosition> wordPositionsJin = ws.findWord("jin");
		assertEquals(3, wordPositionsJin.size());
		assertTrue(wordPositionsJin.contains(new WordPosition(0,6,0,4)));
		assertTrue(wordPositionsJin.contains(new WordPosition(0,6,2,4)));
		assertTrue(wordPositionsJin.contains(new WordPosition(0,6,2,6)));
	}

}
