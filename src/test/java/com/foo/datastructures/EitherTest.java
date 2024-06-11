package com.foo.datastructures;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 *     using VAVR Either datastructure
 *     simple demos
 * </p>
 */
public class EitherTest {
    @Test
    void rightProjectionWithLeftShouldDriveToElseClause(){
        Either<String,String> myEither = Either.left("ko");
        String processResult = myEither.right().map(s -> s.toUpperCase()).getOrElse("boom");
        assertThat(processResult).isEqualTo("boom");
    }

    @Test
    void rightProjectionWithRightContentGivesExpectedResult(){
        Either<String,String> myEither = Either.right("rulez");
        String processResult = myEither.right().map(s -> s.toUpperCase()).getOrElse("boom");
        assertThat(processResult).isEqualTo("RULEZ");
    }


}
