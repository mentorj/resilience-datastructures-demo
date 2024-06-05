package com.foo.service;

import com.foo.service.impl.SimpleServiceRandomizedImpl;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
/**
 * tests suite showing different decorations for our Service
 * uses Mockito to make code more concise & elegant.
 * @author deadbrain
 */
public class ResilienceTest {
    private SimpleService service;
    @BeforeEach
    void setUpTest(){
        service= new SimpleServiceRandomizedImpl();
    }
    @Test
    void plentyStandardCallsShouldFail(){
        assertThrows( RuntimeException.class, ()-> {
                    for (int i = 0; i < 100; i++) {
                        service.sayHello("Merlin");
                    }
                }
        );
    }

    @Test
    void circuitBreakerShouldBeOpenedAfterPlentyCalls(){
        // CB status -
        AtomicBoolean circuitsOpened = new AtomicBoolean(false);
        // create circuit breaker config
        CircuitBreakerConfig cfg = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .minimumNumberOfCalls(5)
                .slidingWindowSize(25)
                .failureRateThreshold(10.0f)
                //.ignoreExceptions(RuntimeException.class)
                .build();

        // get the CB after registering the config
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(cfg);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("SimpleService");

        // creates a listener for CB generated events
        // toggle the flag if number of failures exceed threshold
        // test expects it to be Opened after a couple of calls
        circuitBreaker.getEventPublisher().onError(event -> {
            System.out.println(event);});
        circuitBreaker.getEventPublisher().onFailureRateExceeded(event -> circuitsOpened.set(true));
        // create a Supplier from our service
        // then decorates it with CB
        Supplier<String> simpleServiceSupplier = () -> service.sayHello("Junit");
        Supplier<String> simpleServiceWithCBSupplier =  circuitBreaker.decorateSupplier(simpleServiceSupplier);


        // now runs some service calls
        for(int i = 0 ; i < 100;i++) {
            Try<String> result = Try.ofSupplier(simpleServiceWithCBSupplier);
        }
        assertThat(circuitsOpened.get()).isEqualTo(true);
    }
}
