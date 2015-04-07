package net.tyack.java8for.chapter2;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * Java 8 Samples - Streams examples based on Chapter 2 Java SE 8 for the Really
 * See:- Horstmann, Cay S. (2014-01-10). Java SE 8 for the Really Impatient
 * (Kindle Location 222). Pearson Education. Kindle Edition.
 */
public class Chapter2StreamsTest {

    private static final String PATH_TO_LOTSA_WORDS = "/Users/jamestyack/git/java8for/src/test/resources/lotsa_words.txt";

    @Test
    public void chapter2Stream1() {
	String contents = "catch,catching,cat,cats,dog,eating";
	String[] splitted = contents.split("[\\ P{ L}] +");
	System.out.println(splitted);
	String[] wordsArray = { "catch", "catching", "cat", "cats", "dog", "eating" };
	Stream<String> stream = Arrays.stream(wordsArray, 1, wordsArray.length);
	stream.forEach(System.out::println);
	System.out.println(wordsArray);
	Stream<String> words = Stream.of(wordsArray);
	long c = words.filter(s -> s.contains("at")).count();
	System.out.println(c);
    }

    /*
     * Skip the first element of array when creating stream
     */
    @Test
    public void Chapter2StreamFromPartOfList() {
	String[] wordsArray = { "catch", "catching", "cat", "cats", "dog", "eating" };
	Stream<String> stream = Arrays.stream(wordsArray, 1, wordsArray.length);
	stream.forEach(System.out::println);
    }

    @Test
    public void infiniteStream() {
	Stream<Double> echos = Stream.generate(Math::random);
	// echos.forEach(System.out::println);
    }

    @Test
    public void infiniteSequence() {
	Stream<BigInteger> integers = Stream.iterate(BigInteger.ZERO, n -> n.add(BigInteger.valueOf(2)));
	// just get first 5 values
	integers.limit(5).forEach(System.out::println);
    }

    @Test
    public void infiniteSequenceToListForParallel() {
	Stream<BigInteger> integers = Stream.iterate(BigInteger.ZERO, n -> n.add(BigInteger.valueOf(1)));
	// just get first 5 values
	Stream<BigInteger> map = integers.limit(5).map(bi -> bi);
	List<BigInteger> listOfNumbers = map.collect(Collectors.toList());
	Object[] array = listOfNumbers.parallelStream().map(bi -> bi).peek(System.out::println).toArray();
	for (Object object : array) {
	    System.out.println(object);
	}
    }

    @Test
    public void mapAStream() {
	CsvRow[] rows = { new CsvRow("huzi", "chipin", "4"), new CsvRow("tigger", "jack russell", "12") };
	Stream<CsvRow> stream = Arrays.stream(rows);
	Stream<Dog> dogs = stream.map(row -> new Dog(row.get(0), row.get(1)));
	Map<String, String> collectedDogs = dogs.collect(Collectors.toMap((Dog d) -> d.getName(), (Dog d) -> d.getType()));
	// Stream<Pet> petStream = stream.map(row -> new Pet(row.get(0),
	// row.get(1)));
	// Map<String, String> collectedPets =
	// petStream.collect(Collectors.toMap((Pet p) -> p.getName(), (Pet p) ->
	// p.getType()));
	System.out.println(collectedDogs);
	// sSystem.out.println(collectedPets);
    }

    @Test
    public void streamOfStreamsWithMap() {
	List<String> words = new ArrayList<>();
	words.add("james");
	words.add("john");
	Stream<Stream<Character>> result = words.stream().map(w -> characterStream(w));
	List<Stream<Character>> collect = result.collect(Collectors.toList());
	collect.forEach(str -> System.out.println(str.collect(Collectors.toList())));
    }

    @Test
    public void streamOfStreamsWithFlatMap() {
	List<String> words = new ArrayList<>();
	words.add("james");
	words.add("john");
	Stream<Character> flatMap = words.stream().flatMap(w -> characterStream(w));
	List<Character> collectedChars = flatMap.collect(Collectors.toList());
	collectedChars.forEach(System.out::print);
	Stream<String> sIter = Stream.iterate("str", s -> s + "s");
	List<String> collect = sIter.limit(5).collect(Collectors.toList());
	System.out.println(collect);

    }

    public static Stream<Character> characterStream(String s) {
	List<Character> result = new ArrayList<>();
	for (char c : s.toCharArray())
	    result.add(c);
	return result.stream();
    }

    /*
     * Peeking to see elements as they go though the pipeline
     */
    @Test
    public void peeking() {
	Stream.of("one", "two", "three", "four").filter(e -> e.length() > 3).peek(e -> System.out.println("Filtered value: " + e)).map(String::toUpperCase)
		.peek(e -> System.out.println("Mapped value: " + e)).collect(Collectors.toList());
    }

    /**
     * This is stateful (needs to know previous values)
     */
    @Test
    public void distinctOnStream() {
	Stream<String> uniqueWords = Stream.of("merrily", "merrily", "merrily", "gently").distinct();
	System.out.println(Arrays.asList(uniqueWords.toArray()));
    }

    /**
     * Sorting by longest word first
     */
    @Test
    public void wordSort() {
	String[] wordsArray = { "catch", "catching", "cat", "cats", "dog", "eating" };
	Stream<String> words = Arrays.stream(wordsArray, 1, wordsArray.length);
	Stream<String> longestFirst = words.sorted(Comparator.comparing(String::length));// .reversed());
	System.out.println(Arrays.asList(longestFirst.toArray()));
    }

    /**
     * Reductions are terminal operations. They give result you can work with in
     * your program. This introduces optional. In place of null - allows you to
     * handle situation where no result (eg. stream empty)
     */
    @Test
    public void introducingOptional() {
	String[] wordsArray = { "catch", "catching", "cat", "cats", "dog", "eating" };
	Stream<String> words = Arrays.stream(wordsArray);
	Optional<String> largest = words.max(String::compareToIgnoreCase);
	if (largest.isPresent()) {
	    System.out.println("largest: " + largest.get());
	}

    }

    /**
     * This finds the first occurrence in stream and stops - not for
     * parallelizing
     */
    @Test
    public void findFirstTest() {
	String[] wordsArray2 = { "catch", "Queue", "cat", "cats", "dog", "eating" };
	Stream<String> words2 = Arrays.stream(wordsArray2);
	Optional<String> startsWithQ = words2.filter(s -> s.startsWith("Q")).findFirst();

	if (startsWithQ.isPresent()) {
	    System.out.println("startsWithQ: " + startsWithQ.get());
	}
    }

    /**
     * This finds and stops at occurrence in stream - good for parallelizing
     */
    @Test
    public void findAnyTest() {
	String[] wordsArray2 = { "catch", "Queue", "cat", "cats", "dog", "Quebec" };
	Stream<String> words2 = Arrays.stream(wordsArray2);
	Optional<String> startsWithQ = words2.filter(s -> s.startsWith("Q")).findAny();
	if (startsWithQ.isPresent()) {
	    System.out.println("startsWithQ: " + startsWithQ.get());
	}
    }

    /**
     * Use this if you just want to know if there is a match in the stream there
     * is also allMatch and noneMatch
     */
    @Test
    public void anyMatchTest() {
	String[] wordsArray2 = { "catch", "Queue", "cat", "cats", "dog", "Quebec" };
	Stream<String> words2 = Arrays.stream(wordsArray2);
	boolean anyMatch = words2.anyMatch(s -> s.startsWith("Q"));
	System.out.println("is there match for starts with Q?: " + anyMatch);
    }

    /**
     * Using Optional<T> properly ... Supposed to be safer than returning null
     * if no match - only safe if you use it right The get method gets the
     * wrapped element or throws NoSuchElementException if it doesn't Checking
     * isPresent is just like null check! THE KEY TO USING OPTIONAL EFFECTIVELY
     * ...
     */
    @Test
    public void useOptionalProperly() {
	String[] wordsArray = { "catch", "Queue", "cat", "cats", "dog", "Quebec" };
	Stream<String> words = Arrays.stream(wordsArray);
	Optional<String> startsWithQ = words.filter(s -> s.startsWith("Q")).findAny();
	Stream<String> words2 = Arrays.stream(wordsArray);
	Optional<String> startsWithL = words2.filter(s -> s.startsWith("L")).findAny();

	// using ifPresent
	Set<String> results = new HashSet<>();
	startsWithQ.ifPresent(v -> results.add(v)); // this will add to set
						    // (because optional value
						    // present)
	startsWithL.ifPresent(v -> results.add(v)); // this won't add to set (no
						    // optional value present)
	// or
	startsWithL.ifPresent(results::add);

	// this allows you to get the result of whether results was added to...
	Optional<Boolean> added = startsWithQ.map(results::add);

	System.out.println(added);

	System.out.println(results);

	// use orElse for default
	System.out.println("starts with Q: " + startsWithQ.orElse(""));
	System.out.println("starts with L: " + startsWithL.orElse(""));

	// can also pass function
	System.out.println("starts with L with orElseGet: " + startsWithL.orElseGet(() -> "no match"));

    }

    // shows how to create optional... there are static methods
    public static Optional<Double> inverse(Double x) {
	return x == 0 ? Optional.empty() : Optional.of(1 / x);
    }

    // Reduction operations
    @Test
    public void sumAStreamOfNumbers() {
	Stream<Integer> values = Stream.of(1, 5, 9, 3, 2);
	Optional<Integer> reduce = values.reduce((x, y) -> x + y);
	reduce.ifPresent(i -> System.out.println(i));
    }

    // another way to do same...
    @Test
    public void sumAStreamOfNumbers2() {
	Stream<Integer> values = Stream.of(1, 5, 9, 3, 2);
	Optional<Integer> reduce = values.reduce(Integer::sum);
	reduce.ifPresent(i -> System.out.println(i));
    }

    // and if stream empty...
    // Reduction operations
    @Test
    public void sumAStreamOfNumbersEmpty() {
	Stream<Integer> values = Stream.of();
	Optional<Integer> result = values.reduce((x, y) -> x + y);
	System.out.println(result);
    }

    // since stream.toArray() returns Object[] array .. poss to return array of
    // specific type from stream
    @Test
    public void getArrayOfTypeFromStream() {
	Stream<String> wordsStartingWithT = Stream.of("huzi", "tigger", "trold", "bullet", "hugo").filter(s -> s.startsWith("t"));
	String[] arrayOfWords = wordsStartingWithT.toArray(String[]::new);
	for (String word : arrayOfWords) {
	    System.out.println(word);
	}
    }

    // now.. to collect to Set
    @Test
    public void collectResultsToSet() {
	Stream<String> pets = Stream.of("huzi", "tigger", "trold", "bullet", "hugo");
	Set<String> result = pets.collect(Collectors.toSet());
	System.out.println(result);
    }

    // also possible to collect to specific type of set/collection...
    @Test
    public void collectResultsToTreeSet() {
	Stream<String> pets = Stream.of("huzi", "tigger", "trold", "bullet", "hugo");
	TreeSet<String> result = pets.collect(Collectors.toCollection(TreeSet::new));
	System.out.println(result);
    }

    // also possible to collect to specific type of set/collection...
    @Test
    public void concatStream() {
	Stream<String> pets = Stream.of("huzi", "tigger", "trold", "bullet", "hugo");
	String concatString = pets.collect(Collectors.joining());
	System.out.println(concatString);
    }

    // also possible to collect to specific type of set/collection...
    @Test
    public void concatStreamWithDelim() {
	Stream<String> pets = Stream.of("huzi", "tigger", "trold", "bullet", "hugo");
	String concatString = pets.collect(Collectors.joining(":"));
	System.out.println(concatString);
    }

    // do some stats - SummaryStatistics
    @Test
    public void someSummaryStatistics() {
	Stream<String> pets = Stream.of("huzi", "tigger", "trold", "bullet", "hugo");
	IntSummaryStatistics summary = pets.collect(Collectors.summarizingInt(String::length));
	double avgWordLength = summary.getAverage();
	double maxWordLength = summary.getMax();
	System.out.println("avg word len = " + avgWordLength);
	System.out.println("max word len = " + maxWordLength);
    }

    // collect to map
    @Test
    public void collectToMap() {
	Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
	Map<String, String> languageNames = locales.collect(Collectors.toMap(l -> l.getDisplayLanguage(), l -> l.getDisplayLanguage(l), (existingValue,
		newValue) -> existingValue));
	languageNames.forEach((key, value) -> System.out.println(key + "=" + value));
    }

    // collect to map with merge of values that have same key
    // (all languages in country)
    @Test
    public void collectToMapWithMerge() {
	Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
	Map<String, Set<String>> countryLanguageSets = locales.collect(Collectors.toMap(l -> l.getDisplayCountry(),
		l -> Collections.singleton(l.getDisplayLanguage()), (a, b) -> { // union
										// of
										// a
										// and
										// b
		    Set<String> r = new HashSet<>(a);
		    r.addAll(b);
		    return r;
		}));
	countryLanguageSets.forEach((k, v) -> System.out.println(k + "=" + v));
    }

    // grouping and partitioning

    @Test
    public void partitionStuff() {
	Stream<Locale> locales = Stream.of(Locale.CANADA, Locale.JAPAN);
	Map<String, List<Locale>> countryToLocales = locales.collect(Collectors.groupingBy(Locale::getCountry));
	countryToLocales.forEach((key, value) -> System.out.println(key + "=" + value));
    }

    @Test
    public void groupLocalesByCountry() {
	Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
	Map<String, List<Locale>> countryToLocales = locales.collect(Collectors.groupingBy(Locale::getCountry));
	List<Locale> swissLocale = countryToLocales.get("CH");
	System.out.println(swissLocale);
    }

    @Test
    public void groupLocalesByCountryWithCounting() {
	Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
	Map<String, Long> countryToLocaleCounts = locales.collect(Collectors.groupingBy(Locale::getCountry, Collectors.counting()));
	System.out.println(countryToLocaleCounts);
    }

    // primitive type streams

    @Test
    public void intStream() {
	IntStream stream = IntStream.of(1, 2, 3, 4, 5);
	int[] values = { 1, 2, 3, 4, 5 };
	IntStream valStream = Arrays.stream(values, 1, 4);
	OptionalInt max = stream.max();
	OptionalInt min = valStream.min();
    }

    @Test
    public void primativeRange() {
	IntStream zeroToNinetyNine = IntStream.range(0, 100);
	IntStream zeroToHundred = IntStream.rangeClosed(0, 100);
    }

    // Parallel streams

    @Test
    public void wordCountSorted() throws IOException {
	Stream<String> allWordsInText = getStreamOfWordsInFile(new File(PATH_TO_LOTSA_WORDS));
	Map<String, Long> result = allWordsInText.collect(Collectors.groupingBy(word -> word, Collectors.counting()));
	Stream<Map.Entry<String, Long>> sorted = result.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
	sorted.forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));
    }

    @Test
    public void badParallelStream() throws IOException {
	Stream<String> words = getStreamOfWordsInFile(new File(PATH_TO_LOTSA_WORDS));
	int[] shortWords = new int[12];
	words.parallel().forEach(s -> {
	    if (s.length() < 12)
		shortWords[s.length()]++;
	}); // Error— race condition! - updating a shared array is not thread
	    // safe!!
	System.out.println(Arrays.toString(shortWords));
    }

    private Stream<String> getStreamOfWordsInFile(File file) throws IOException {
	List<String> lines = Files.readLines(file, Charsets.UTF_8);
	Stream<String> allWordsInText = lines.stream().flatMap(line -> getStreamOfWords(line.toLowerCase()));
	return allWordsInText;
    }

    private Stream<String> getStreamOfWords(String line) {
	return Splitter.on(CharMatcher.BREAKING_WHITESPACE).omitEmptyStrings().splitToList(line).stream();
    }

}