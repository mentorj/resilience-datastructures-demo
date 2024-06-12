package com.foo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.match.annotation.Patterns;
import io.vavr.match.annotation.Unapply;

@Patterns
/**
 * <p>
 *     helper class containing static methods destructuring objects to Tuples
 *     Beware with annotations to be used, naming conventions & return type (TupleN)
 *     Ensure to add static imports for this class in classes using destructuring
 * </p>
 */
public class DestructureHelper {

    @Unapply
    static Tuple2<String,String> Address(Address adr){
        return Tuple.of(adr.getCountry(),adr.getTown());
    }
}
