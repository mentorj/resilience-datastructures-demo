package com.foo;

import io.vavr.*;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

public class FunctionsTest {
    @Test
    void declaringFunctionWithZeroArgsReturningString(){
        Function0<String> helloFn = () -> "Hello World";
        assertThat(helloFn.apply()).isEqualTo("Hello World");

        assertThat(helloFn.arity()).isEqualTo(0);
    }

    @Test
    void declaringFunctionWithOneArgReturningString(){
        Function1<String,String> helloFn = (String str) -> "Hello " + str;
        assertThat(helloFn.apply("World")).isEqualTo("Hello World");
        assertThat(helloFn.arity()).isEqualTo(1);
    }

    @Test
    void invokingmemoizedFunctionTwiceReturnSameResult(){
        Function0<Integer> randomFn = () -> {
            Random r = new SecureRandom();
            return r.nextInt(10);
        };

        Function0<Integer> memoizedFn = randomFn.memoized();
        assertThat(randomFn.isMemoized()).isFalse();
        assertThat(memoizedFn.isMemoized()).isTrue();
        assertThat(memoizedFn.arity()).isEqualTo(randomFn.arity());

        int r1 = memoizedFn.apply();
        int r2 = memoizedFn.apply();

        assertThat(r1).isEqualTo(r2);
    }

    // this is a not a test
    // standard function used by some tests
    private String foo(){
        return "foo";
    }


    @Test
    void createVavrFunctionFromMethodReference(){
        Function0<String> fooFn = Function0.of(this::foo);
        assertThat(fooFn.apply()).isEqualTo("foo");
    }

    @Test
    void applyPartially(){
        Function2<Integer,Integer,Integer> addFn = (i,j) -> i+j;
        Function1<Integer,Integer> add2Fn= addFn.apply(2);
        assertThat(add2Fn.apply(3)).isEqualTo(5);
        assertThat(add2Fn.apply(3)).isEqualTo(addFn.apply(2,3));
        assertThat(add2Fn.arity()).isEqualTo(addFn.arity()-1);
    }

    @Test
    void wrapDangerousFunctionsWithCheckedFunction(){
        CheckedFunction2<Integer,Integer,Integer> dangerousFn = (x,y) -> {return x/y;};
        Integer result = dangerousFn.unchecked().apply(4,2);
        assertThat(result).isEqualTo(2);
    }

    @Test
    void runCurriedFunction(){
        Function3<Integer,Integer,Integer,Integer> addFn = (i, j, k) -> i+j+k;
        Function1<Integer,Function1<Integer,Function1<Integer,Integer>>> curruedAddFn = addFn.curried();
        int result = curruedAddFn.apply(2).apply(3).apply(5);
        assertThat(result).isEqualTo(10);
    }

    @Test
    void composeFunctions(){
        Function1<String,String> createBanneFn = (msg) -> "<h1>"+ msg + "</h1>";
        Function1<String,String>  createMsg = (msg)  -> "Hello " + msg;
        Function1<String,String>  upperCaseFn = (msg)  ->  msg.toUpperCase();

        String resultMsg = createBanneFn.compose(createMsg.compose(upperCaseFn)).apply("world");
        assertThat(resultMsg).contains("WORLD");
        assertThat(resultMsg).startsWith("<h1>");
        assertThat(resultMsg).endsWith("</h1>");
    }

    @Test
    void liftPartialFunctionLeadsToOption(){
        Function2<Integer,Integer,Integer> divideFn  = (a,b) -> a/b;
        Function2<Integer,Integer, Option<Integer>> safeDivide = Function2.lift(divideFn);
        Option<Integer> okOption = safeDivide.apply(4,2);
        Option<Integer> koOption = safeDivide.apply(4,0);
        assertThat(okOption.isDefined()).isTrue();
        assertThat(okOption.get()).isEqualTo(2);
        assertThat(koOption.isDefined()).isFalse();

    }

    @Test
    void liftAndTransformationsWorkflow(){
        List<Integer> inputsList = List.of(2,4,6,0,8);
        List<Integer> transformedList = inputsList
                .map(CheckedFunction1.lift(integer -> 4/integer))
                .map(integer -> integer.getOrElse(-1));
        assertThat(transformedList.size()).isEqualTo(inputsList.size());
        assertThat(transformedList.filter(integer -> integer<0)).isNotEmpty();

    }
}
