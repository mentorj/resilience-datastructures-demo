package com.foo.datastructures;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
public class OptionTest {

    @Test
    void createAnOptionFromContentOrOptional(){
        Option<String> myStringOption = Option.of("Hello");
        assertThat(myStringOption.isDefined()).isTrue();
        assertThat(myStringOption.get()).isEqualTo("Hello");

        myStringOption = Option.ofOptional(Optional.of("world"));
        assertThat(myStringOption.isDefined()).isTrue();
        assertThat(myStringOption.get()).isEqualTo("world");
    }

    @Test
    void showCasingOptionHandling(){
        Option<String> myStringOption = Option.of("Hello");
        String myString = myStringOption.filter(s -> s.length()==5)
                .map(s -> s.toUpperCase(Locale.ENGLISH))
                .getOrElse("default")
                ;
        assertThat(myString).isEqualTo("Hello".toUpperCase());
    }
}
