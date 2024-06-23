package com.foo.datastructures;

import io.vavr.collection.Array;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class ArrayTest
{
    @Test
    void buildSimpleArray(){
        Array<String> strings = Array.of("Hello","World","from","VAVR");
        assertThat(strings).contains("Hello");
        assertThat(strings).size().isEqualTo(4);
        assertThat(strings.get(0)).isEqualTo("Hello");
        assertThat(strings.isLazy()).isFalse();// evaluated eagerly
    }

    @Test
    void buildArrayFromRange(){
        Array<Integer>  integers = Array.range(0,1000);
        assertThat(integers).size().isEqualTo(1000);
        assertThat(integers.max().get()).isEqualTo(999);
    }
}
