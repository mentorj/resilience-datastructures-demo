package com.foo.datastructures;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;

/**
 * unit tests related to Try structure
 * @author deadbrain - jerome@javaxpert.com
 */
public class TryTest {

    @Test
    void tryShouldWrapFailure(){
        Try<String> dummyTry = Try.of(() -> {throw new RuntimeException("Boom");});
        assertThat(dummyTry.isSuccess()).isFalse();
    }

    @Test
    void tryShouldWrapSuccess(){
        Try<String> dummyTry = Try.of(() -> "Hello");
        assertThat(dummyTry.isSuccess()).isTrue();
    }

    @Test
    void tryChainingCalls(){
        Try<String> chainedTry = Try.of(() -> "    Java    ");
        String result = chainedTry.mapTry(s -> s.toLowerCase())
                .mapTry(s -> s.trim())
                .getOrElse("BROKEN")
                ;
        assertThat(result).isNotEqualTo("BROKEN");
        assertThat(result).isEqualTo("java");
    }

    @Test
    void mapNotInvokedWhenHandleErrorCase(){
        AtomicBoolean success = new AtomicBoolean(true);
        Try<String> chainedTry = Try.of(() -> {throw new RuntimeException("Boom");});
        chainedTry.mapTry(s->s.trim())
                .mapTry(s->s.toUpperCase(Locale.ENGLISH))
                .recover(throwable -> {
                    success.set(false);
                    return "broken";
                });
        assertThat(success.get()).isFalse();
        assertThat(chainedTry.isSuccess()).isFalse();
    }
    @Test
    void tryChainingCallsWithDefaultCase(){
        Try<String> chainedTry = Try.of(() -> {throw new RuntimeException("Boom");});
        String result = chainedTry.map(s -> s.toLowerCase())
                .map(s -> s.trim())
                .getOrElse("BROKEN")
                ;
        assertThat(result).isEqualTo("BROKEN");
    }

    @Test
    void tryCallFinallyBlock(){
        StupidClass obj = new StupidClass();
        Try<String> tryAlwaysFail = Try
                .of(() -> {return obj.alwaysFail();})
                .andFinally(() -> {obj.toggleCompletion();});
        assertThat(obj.isCompletion()).isTrue();
    }

}
