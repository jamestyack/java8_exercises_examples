package net.tyack.java8for.chapter2;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

/**
 * @author jamestyack www.tyack.net These are my answers to questions from the
 *         excellent Java SE 8 for the Really Impatient:-
 * 
 *         See:- Horstmann, Cay S. (2014-01-10). Java SE 8 for the Really
 *         Impatient (Kindle Location 222). Pearson Education. Kindle Edition.
 *
 *         Chapter2 streams sample answers
 *
 */
public class Chapter2StreamsExercisesTest {

    private static final int WORD_LENGTH = 12;
    private static final String PATH_TO_LOTSA_WORDS = "/Users/jamestyack/git/java8for/src/test/resources/lotsa_words.txt";
    private static Set<String> threadsUsed = Collections.synchronizedSet(new HashSet<>());
    private int isLongWordCounter;

    @Before
    public void setup() {
	isLongWordCounter = 0;
    }

    /**
     * 1. Write a parallel version of the for loop in Section 2.1 , “From
     * Iteration to Stream Operations,” on page 22 . Obtain the number of
     * processors. Make that many separate threads, each working on a segment of
     * the list, and total up the results as they come in. (You don’t want the
     * threads to update a single counter. Why?)
     * 
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void ex1ParallelVersionOfForLoopWithoutStream() throws IOException, InterruptedException, ExecutionException {
	Stream<String> words = getStreamOfWordsInFile(PATH_TO_LOTSA_WORDS);
	List<String> wordsList = words.collect(Collectors.toList());
	final int totalWords = wordsList.size();
	final int cores = Runtime.getRuntime().availableProcessors();
	final int wordsPerCore = totalWords / cores;
	System.out.println("Total cores: " + cores + "\nTotal words: " + totalWords + "\nWords per core: " + wordsPerCore);
	List<Future<Map.Entry<String, Integer>>> results = new ArrayList<>();
	ExecutorService pool = Executors.newFixedThreadPool(cores);
	for (int i = 0; i < cores; i++) {
	    // for each core, work on a portion of list
	    final int partition = i;
	    Future<Entry<String, Integer>> future = pool.submit(createCallableToWorkOnPartition(wordsList, totalWords, wordsPerCore, partition));
	    results.add(future);
	}
	int finalResult = 0;
	for (Future<Entry<String, Integer>> result : results) {
	    Entry<String, Integer> resultPair = result.get();
	    System.out.println(resultPair.getKey() + " found " + resultPair.getValue() + " words longer than " + WORD_LENGTH + " chars");
	    finalResult += resultPair.getValue();
	}
	System.out.println("Final total: " + finalResult);
	assertEquals(getStreamOfWordsInFile(PATH_TO_LOTSA_WORDS).parallel().filter(w -> w.length() > WORD_LENGTH).count(), new Long(finalResult).longValue());
    }

    private Callable<Entry<String, Integer>> createCallableToWorkOnPartition(List<String> wordsList, final int totalWords, final int wordsPerCore,
	    final int partition) {
	Callable<Entry<String, Integer>> callable;
	callable = new Callable<Map.Entry<String, Integer>>() {
	    @Override
	    public Entry<String, Integer> call() throws Exception {
		final int startIndex = partition * wordsPerCore;
		final int endIndex = Math.min(totalWords - 1, (partition + 1) * wordsPerCore - 1);
		System.out.println("Thread " + Thread.currentThread().getName() + "\nWorking on partition " + partition + " from idx " + startIndex
			+ " to idx " + endIndex);
		int totalWordsLargerThanWordLength = 0;
		for (int j = startIndex; j <= endIndex; j++) {
		    if (wordsList.get(j).length() > WORD_LENGTH)
			totalWordsLargerThanWordLength++;
		}
		return new AbstractMap.SimpleEntry<String, Integer>(Thread.currentThread().getName(), totalWordsLargerThanWordLength);
	    }
	};
	return callable;
    }

    @Test
    public void ex1ParallelVersionOfForLoopWithStream() throws IOException {
	Stream<String> words = getStreamOfWordsInFile(PATH_TO_LOTSA_WORDS);
	long count = words.parallel().filter(w -> isLongWord(w, false)).count();
	System.out.println("Final result using parallel stream: " + count);
	System.out.println("used threads " + threadsUsed);
    }

    /**
     * 2. Verify that asking for the first five long words does not call the
     * filter method once the fifth long word has been found. Simply log each
     * method call.
     * 
     * @throws IOException
     */
    @Test
    public void ex2VerifyAskingForFirstFiveLongWordsDoesNotCallFilterAfterFound() throws IOException {
	Stream<String> streamOfWordsInFile = getStreamOfWordsInFile(PATH_TO_LOTSA_WORDS);
	Stream<String> result = streamOfWordsInFile.filter(w -> isLongWord(w, true)).limit(5);
	System.out.println("First 5 long words" + result.collect(Collectors.toList()) + " method was called " + isLongWordCounter + " times.");
    }

    /**
     * 3. Measure the difference when counting long words with a parallelStream
     * instead of a stream. Call System.currentTimeMillis before and after the
     * call, and print the difference. Switch to a larger document (such as War
     * and Peace) if you have a fast computer.
     * 
     * @throws IOException
     */
    @Test
    public void ex3MeasureCountingWithParallelVsStream() throws IOException {
	long beforeStream = System.currentTimeMillis();
	getStreamOfWordsInFile(PATH_TO_LOTSA_WORDS).filter(w -> isLongWord(w, true)).count();
	long afterStream = System.currentTimeMillis();

	long beforeParallelStream = System.currentTimeMillis();
	getStreamOfWordsInFile(PATH_TO_LOTSA_WORDS).parallel().filter(w -> isLongWord(w, true)).count();
	long afterParallelStream = System.currentTimeMillis();

	// Results
	System.out.println("Time with stream: " + (afterStream - beforeStream));
	System.out.println("Time with parallel stream: " + (afterParallelStream - beforeParallelStream));
    }

    /**
     * 4. Suppose you have an array int[] values = { 1, 4, 9, 16 }. What is
     * Stream.of(values)? How do you get a stream of int instead?
     */
    @Test
    public void ex4streamOfArrayValues() {
	int[] values = { 1, 4, 9, 16 };
	// it's a stream of int array
	Stream<int[]> whatIsThis = Stream.of(values);
	// containing 1 value
	System.out.println(whatIsThis.count());
	// this is what we need...
	IntStream stream = Arrays.stream(values);
	stream.forEach(System.out::println);
    }

    /**
     * 5. Using Stream.iterate, make an infinite stream of random numbers— not
     * by calling Math.random but by directly implementing a linear congruential
     * generator. In such a generator, you start with x0 = seed and then produce
     * xn + 1 = (a xn + c) % m, for appropriate values of a, c, and m. You
     * should implement a method with parameters a, c, m, and seed that yields a
     * Stream <Long>. Try out a = 25214903917, c = 11, and m = 248.
     */
    @Test
    public void ex5StreamIterateRandomStream() {
	Stream<Long> randomNumberStream = getRandomNumberStream(25214903917L, 11L, 248, System.currentTimeMillis());
	randomNumberStream.limit(100).forEach(System.out::println);
    }

    Stream<Long> getRandomNumberStream(long a, long c, long m, long seed) {
	return Stream.iterate(seed, n -> {
	    return (a * n + c) % m;
	});
    }

    /**
     * 6. The characterStream method in Section 2.3 , “The filter, map, and
     * flatMap Methods ,” on page 25 , was a bit clumsy (copied it below as
     * characterStreamOld!), first filling an array list and then turning it
     * into a stream. Write a stream-based one-liner instead. One approach is to
     * make a stream of integers from 0 to s.length() - 1 and map that with the
     * s:: charAt method reference.
     * 
     *
     */
    @Test
    public void ex6BetterStream() {
	Stream<Character> characterStreamNew = characterStreamNew("Argentina");
	characterStreamNew.forEach(System.out::println);
    }

    public static Stream<Character> characterStreamNew(String s) {
	return IntStream.range(0, s.length()).mapToObj(s::charAt);
    }

    public static Stream<Character> characterStreamOld(String s) {
	List<Character> result = new ArrayList<>();
	for (char c : s.toCharArray())
	    result.add(c);
	return result.stream();
    }

    /**
     * 7. Your manager asks you to write a method public static <T> boolean
     * isFinite( Stream < T > stream). Why isn’t that such a good idea? Go ahead
     * and write it anyway.
     */
    @Test
    public void ex7() {
	// this is silly, if it's infinite, you would never get a response.
	// Pushing back to boss!
    }

    /**
     * 8. Write a method public static <T> Stream <T> zip( Stream < T > first,
     * Stream < T > second) that alternates elements from the streams first and
     * second, stopping when one of them runs out of elements.
     */
    @Test
    public void ex8() {
	fail("unimplemented");
    }

    /**
     * 9. Join all elements in a Stream < ArrayList < T > > to one ArrayList <
     * T>. Show how to do this with the three forms of reduce.
     */
    @Test
    public void ex9() {
	fail("unimplemented");
    }

    /**
     * 10. Write a call to reduce that can be used to compute the average of a
     * Stream < Double>. Why can’t you simply compute the sum and divide by
     * count()?
     */
    @Test
    public void ex10() {
	fail("unimplemented");
    }

    /**
     * 11. It should be possible to concurrently collect stream results in a
     * single ArrayList, instead of merging multiple array lists, provided it
     * has been constructed with the stream’s size, since concurrent set
     * operations at disjoint positions are threadsafe. How can you achieve
     * that?
     */
    @Test
    public void ex11() {
	fail("unimplemented");
    }

    /**
     * 12. Count all short words in a parallel Stream < String>, as described in
     * Section 2.13 , “ Parallel Streams ,” on page 40 , by updating an array of
     * AtomicInteger. Use the atomic getAndIncrement method to safely increment
     * each counter.
     */
    @Test
    public void ex12() {
	fail("unimplemented");
    }

    /**
     * 13. Repeat the preceding exercise, but filter out the short strings and
     * use the collect method with Collectors.groupingBy and
     * Collectors.counting.
     */
    @Test
    public void ex13() {
	fail("unimplemented");
    }

    private boolean isLongWord(String word, boolean trackCalls) {
	if (trackCalls)
	    isLongWordCounter++;
	threadsUsed.add(Thread.currentThread().getName());
	return word.length() > WORD_LENGTH;
    }

    private Stream<String> getStreamOfWordsInFile(String path) throws IOException {
	List<String> lines = Files.readAllLines(Paths.get(path));
	Stream<String> allWordsInText = lines.stream().flatMap(line -> getStreamOfWords(line.toLowerCase()));
	return allWordsInText;
    }

    private Stream<String> getStreamOfWords(String line) {
	return Splitter.on(CharMatcher.BREAKING_WHITESPACE).omitEmptyStrings().splitToList(line).stream();
    }

}