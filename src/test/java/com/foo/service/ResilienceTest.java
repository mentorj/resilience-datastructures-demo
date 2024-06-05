package com.foo.service;

import com.foo.service.impl.SimpleServiceRandomizedImpl;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
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


    @Test
    void ensureRateLimiterProtectResource(){
        // AtomicBoolean used to toggle status
        AtomicBoolean status = new AtomicBoolean(true);
        // create the resource protected as a supplier object
        Supplier<String> simpleServiceSupplier = () -> "Hello";

        // create rate limiter config
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(1000)
                .timeoutDuration(Duration.ofMillis(250))
                .build();

// Create registry
        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("myLimiter",config);
        rateLimiter.getEventPublisher().onFailure(event -> {
            System.out.println("Rate Limiter error received");
            status.set(false);
        });
        Supplier<String> protectedResource = RateLimiter.decorateSupplier(rateLimiter, simpleServiceSupplier);


        for(int i =0;i < 10000;i++){
            Try<String> result = Try.of(() -> protectedResource.get());
            result.map(s -> s).onFailure(throwable -> {
                System.out.println("Unable to reach service..."+  throwable.getMessage());
            });
        }

        assertThat(status.get()).isFalse();

    }
}
