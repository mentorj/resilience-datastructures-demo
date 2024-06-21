package com.foo.datastructures;

import io.vavr.Function0;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class TuplesTest {

    private static class Person{
        private String name,firstName;
        private int yearOfBirth;

        public Person(String name, String firstName, int yearOfBirth) {
            this.name = name;
            this.firstName = firstName;
            this.yearOfBirth = yearOfBirth;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public int getYearOfBirth() {
            return yearOfBirth;
        }

        public void setYearOfBirth(int yearOfBirth) {
            this.yearOfBirth = yearOfBirth;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return yearOfBirth == person.yearOfBirth && Objects.equals(name, person.name) && Objects.equals(firstName, person.firstName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, firstName, yearOfBirth);
        }
    }

    @Test
    void whenGivenATuple_shouldRetrieveFields(){
        Tuple3<String,Integer,Boolean> myTuple = Tuple.of("Hello",1,false);
        assertThat(myTuple._1).isEqualTo("Hello");
        assertThat(myTuple._2).isEqualTo(1);
        assertThat(myTuple._3()).isEqualTo(false);
    }

    @Test
    void vavrMapContainsTuples(){
        Map<String,Integer> myMap= HashMap.of("hello",1,"World",2);
        assertThat(myMap).contains(Tuple.of("hello",1));
    }

    @Test
    void groupedByReturnsMapOf(){
        List<Person> personList = List.of(new Person("Martin", "Alice",2006),
                new Person("Martin","Bob",1975),
                new Person("Doe","John",1970));

        assertThat(personList.groupBy(person -> person.getName())).hasSize(2);
        assertThat(
                personList.groupBy(person -> person.getName())
                )
                .contains(
                        Tuple.of("Doe",List.of(new Person("Doe","John",1970))));
    }

    @Test
    void functionsMayReturnTuples(){
        Function0<Tuple2<Integer,String>> constantTuple = () -> {return Tuple.of(1,"Hello");};
        Tuple2<Integer,String> result = constantTuple.apply();
        assertThat(result._1).isEqualTo(1);
        assertThat(result._2).isEqualTo("Hello");
    }
}
