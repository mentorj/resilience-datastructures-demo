package com.foo.service.impl;

import com.foo.service.SimpleService;

import java.security.SecureRandom;
import java.util.Random;

/**
 * this implement uses Random to fail from time to time
 * throws RuntimeException randomly
 * requires caution from caller component
 * Impure function makes tests tricky and not reliable...
 * @author deadbrain - jerome@javaxpert.com
 */
public class SimpleServiceRandomizedImpl implements SimpleService {
    private final Random random;
    public SimpleServiceRandomizedImpl(){
        random = new SecureRandom();
    }

    @Override
    public String sayHello(String who) {
        System.out.println("sayHello invoked");
        int randomValue = random.nextInt(100);
        if(randomValue<35)
            throw new RuntimeException("Impl failed");
        else
            return "Hello " + who;
    }
}
