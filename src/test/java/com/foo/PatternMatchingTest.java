package com.foo;

import io.vavr.API;
import io.vavr.Function1;
import io.vavr.MatchError;
import io.vavr.Predicates;
import io.vavr.control.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static io.vavr.API.*;
import static io.vavr.Predicates.*;
import static org.assertj.core.api.Assertions.*;
import static com.foo.DestructureHelperPatterns.*;

//  for objects destructuring

/**
 * <p>
 *     shows some basic & advanced Pattern Matching (with VAVR) features
 *
 * </p>
 * @author deadbrain - J.MOLIERE - jerome@javaxpert.com
 */
public class PatternMatchingTest {

    @Test
    void basicPatternMatchingWithoutDefaultClause(){
        String msg = Match("test").of(
                Case($("TEST"),"OK"),
                Case($("test"),"ok"),
                Case($("Micro"),"ko")
        );
        assertThat(msg).isEqualTo("ok");
    }

    @Test
    void basicPatternMatchingWithDefaultClause(){
        String msg = Match("test").of(
                Case($("test"),"OK"),
                Case($(),"ko")
        );
        assertThat(msg).isEqualToIgnoringCase("ok");
    }
    @Test
    void basicPatternMatchingWithDefaultClause2(){
        String msg = Match("test").of(
                Case($("test"),"OK"),
                Case($(),"ko")
        );
        assertThat(msg).isEqualToIgnoringCase("ok");
    }

    @Test
    void basicPatternMatchingWithNumbers(){
        String msg = Match(300).of(

                Case($(200),"normal response"),
                Case($(300),"redirects"),
                Case($(400),"request error"),
                Case($(500),"internal server error"),
                Case($(),"Unexpected code")
        );
        assertThat(msg).isEqualToIgnoringCase("redirects");

        msg = Match(0).of(

                Case($(200),"normal response"),
                Case($(300),"redirects"),
                Case($(400),"request error"),
                Case($(500),"internal server error"),
                Case($(),"Unexpected code")
        );
        assertThat(msg).isEqualToIgnoringCase("unexpected code");
    }

    @Test
    void whenPatternMatchingWithOptions(){
        Option<String> maybeOk = Some("OK");
        Option<String> maybeKo = None();

        String msg = Match(maybeOk).of(
                Case($(None()),"failure"),
                Case($(),"success")
        );
        assertThat(msg).isEqualTo("success");

        msg = Match(maybeKo).of(
                Case($(None()),"failure"),
                Case($(),"success")
        );
        assertThat(msg).isEqualTo("failure");
    }

    @Test
    void whenUsingBuiltInPredicate(){
        String msg = Match(300).of(

                Case($(isIn(200,300,400,500)),"valid HTTP response code"),
                Case($(),"Unexpected code")
        );
        assertThat(msg).isEqualTo("valid HTTP response code");
    }

    @Test
    void whenUsingCustomPredicate(){
        Address address = new Address();
        address.setTown("Paris");
        address.setCountry("France");
        String msg = Match(address.getCountry()).of(
                Case($(country ->country.equals("France")),"French guy"),
                Case($(),"Not from France, sorry")
        );
        assertThat(msg).isEqualTo("French guy");
    }

    @Test
    void whenUsingMultiplePredicates(){
        Predicate<Integer> greaterThan10 = n -> n > 10;
        Predicate<Integer> isLessThan100 = n -> n<100;
        Predicate<Integer> isEVen        = n -> n %2 == 0;

        int value = 42;
        String msg = Match(value).of(
                Case($(allOf(
                        greaterThan10 ,
                        isLessThan100 ,
                        isEVen)),"middle even number"),
                Case($(),"Something else")
        );
        assertThat(msg).isEqualTo("middle even number");
    }

    @Test
    void whenMatchReturnsAnOption(){
        Option<String> optMessage = Match(Integer.valueOf(42)).option(
                Case($(i -> i%2!=0),"odd number"),
                Case($(12),"12")
        );
        assertThat(optMessage.isDefined()).isFalse();
        assertThat(optMessage.toString()).isEqualTo("None");
    }

    @Test
    /**
     * refers to doc for more info about destructuring!!!
     */
    void whenDestructuringObject(){
        Address address = new Address();
        address.setTown("Paris");
        address.setCountry("France");
        String msg = Match(address).of(
                Case($Address($("France"),$("Paris") ) ,
                        "a guy from Paris"),
                Case($Address($("France"),$()) , "a french guy"),
                Case($(),"a foreigner")
        );
                assertThat(msg).isEqualTo("a guy from Paris");
    }

    @Test
    void reusingSamePatternMatching(){
        Function1<Address,String> matchAddressFn = address ->
        {
            return Match(address).of(
                    Case($Address($("France"),$("Paris") ) ,
                            "a guy from Paris"),
                    Case($Address($("France"),$()) , "a french guy"),
                    Case($(),"a foreigner")
            );
        };
        Address adr1 = new Address();
        adr1.setCountry("France");
        adr1.setTown("Bordeaux");

        Address adr2 = new Address();
        adr2.setTown("Paris");
        adr2.setCountry("France");

        assertThat(matchAddressFn.apply(adr1)).isEqualTo("a french guy");
        assertThat(matchAddressFn.apply(adr2)).isEqualTo("a guy from Paris");
    }

    @Test
    void whenInputDoesNotMatchAndNoDefaultClauseErrorIsRaised(){
        int i = -10;
        Assertions.assertThrows(MatchError.class,()-> {
            String msg = API.Match(i).of(
                    Case($(0), "strange HTTP code !!"),
                    Case($(200), "Good"),
                    Case($(300), "authentication?"),
                    Case($(400), "request error"),
                    Case($(500), "internal error")
            );
        });

    }




}
