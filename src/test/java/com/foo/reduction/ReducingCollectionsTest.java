package com.foo.reduction;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;


/**
 * tests suite with simple collections tests
 * @author deadbrain - jerome@javaxpert.com
 */
public class ReducingCollectionsTest {
    @Test
    void reduceIntegersList(){
        List<Integer> integers = List.of(1,2,3,4,5);
        int sum = integers.reduce((integer, integer2) -> integer+ integer2);
        assertThat(sum).isEqualTo(15);
    }

    @Test
    void reduceWithFoldLeftIntegersList(){
        List<Integer> integers = List.of(1,2,3,4,5);
        int sum = integers.foldLeft(0,(integer, integer2) -> integer+integer2);//first arg is initial value for the operation
        assertThat(sum).isEqualTo(15);

    }

    @Test
    void reduceJavaIntegersList(){
        java.util.List<Integer> integers = java.util.List.of(1,2,3,4,5);
        int sum = integers.stream().reduce(0,(integer, integer2) -> integer+integer2);// first argument is initial value
        assertThat(sum).isEqualTo(15);
    }

    @Test
    void reduceStrings(){
        List<String> strings = List.of("I"," ","love"," ","coding in Java");
        String concatString = strings.foldLeft("",(s, s2) -> s + s2);
        assertThat(concatString).contains("Java");
        assertThat(concatString.length()).isBetween(15,25);
    }
}
