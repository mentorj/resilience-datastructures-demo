package com.foo;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.assertj.core.api.Condition;
import org.assertj.core.condition.VerboseCondition;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * flatmap() samples tests suite
 * @author deadbrain - jerome@javaxpert.com
 */
public class FlatmappingTest {
    private Condition<java.util.List> sizeIsEqualTo2 = VerboseCondition.verboseCondition(actual -> actual.size() == 2,
            // predicate description
            "not equal to 2",
            // value under test description transformation function
            s -> String.format(" but length was %s", s.size()));


    private Condition<List> sizeIsEqualTo2VavrList = VerboseCondition.verboseCondition(actual -> actual.size() == 2,
            // predicate description
            "not equal to 2",
            // value under test description transformation function
            s -> String.format(" but length was %s", s.size()));

    @Test
    /**
     * flatmap will flatten the list of options to a List of strings for any element non empty from
     * the initial collection
     */
    void flatMappingOptions(){
        List<Option<String>> optionList = List.of(Option.some("Hello"),Option.some(" "),Option.none(),Option.some("World"));
        String result =optionList.flatMap(obj -> obj).reduce((o, o2) -> o + o2);
        assertThat(result).contains("World");
        assertThat(result).contains("Hello");
        assertThat(result.length()).isBetween(10,30);

    }

    /**
     * shows how to transform Optional  to Option to use the VAVR API
     */
    @Test
    void flatMappingListOfOptionals(){
        List<Optional<String>> optionalList = List.of(Optional.of("Hello"),Optional.empty(),Optional.of(" "),Optional.of("World!!!"));
        String result = optionalList.map(option  -> Option.ofOptional(option)).flatMap(obj-> obj).reduce((o, o2) -> o + o2);
        assertThat(result).contains("World");
        assertThat(result).contains("Hello");
        assertThat(result.length()).isBetween(10,30);
    }

    @Test
    /**
     * shows how to apply flatmap with standard Java collections
     * also adds an example of quite advanced usage of AssertJ Condition
     */
    void flatmapJavaStdCollections(){
        java.util.List<Optional<String>> javaStdOptionals = java.util.List.of(Optional.of("Hello "),Optional.empty(),Optional.of("World"),Optional.empty());
        java.util.List<String> flatmappedList = javaStdOptionals.stream().flatMap(s -> s.stream()).collect(Collectors.toList());
        assertThat(flatmappedList).satisfies(sizeIsEqualTo2);
    }

    @Test
    void flatMapEithersList(){
        List<Either<String,Integer>> eithers = List.of(Either.left("error"),Either.left("Boom"),Either.right(42),Either.right(12));
        List<Integer> flattenedList = eithers.flatMap(integers -> integers);
        assertThat(flattenedList.size()).isEqualTo(2);
    }

    @Test
    void flatMapAndReduceEithersList(){
        List<Either<String,Integer>> eithers = List.of(Either.left("error"),Either.left("Boom"),Either.right(42),Either.right(12));
        List<Integer> flattenedList = eithers.flatMap(integers -> integers);
        int sum = flattenedList.foldLeft(0,(integer, integer2) -> integer+integer2);
        assertThat(sum).isEqualTo(54);
    }
}
