package com.foo.datastructures;
import com.foo.Address;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
/**
 * <p>
 *     some Lists,Maps & Sets from VAVR samples
 * </p>
 */
public class CollectionsTest {

    @Test
    void  creatingAVAVRMap(){
        Map<String,Integer> myMap = HashMap.of("key1",42,"key2",12);
        assertThat(myMap.size()).isEqualTo(2);
        assertThat(myMap.contains(Tuple.of("key2",12)));
        assertThat(myMap.filter((s, integer) -> s.equals("key2") ).size()).isEqualTo(1);
    }

    @Test
    void usingMapContentsAndKeys(){
        Map<String,Integer> myMap = HashMap.of("key1",42,"key2",12);
        assertThat(myMap.values().size()).isEqualTo(2);
        assertThat(myMap.keySet().size()).isEqualTo(2);
        assertThat(myMap.values()).isEqualTo(List.of(42,12));
    }

    @Test
    void mapsDoNotHandleDuplicates(){
        Map<String,Integer> myMap = HashMap.of("key1",42,"key2",12,"key1",12);
        assertThat(myMap.values().size()).isEqualTo(2);
        assertThat(myMap.keySet().size()).isEqualTo(2);
        assertThat(myMap.values()).isEqualTo(List.of(12,12));
    }

    @Test
    void listsHandleDuplicates(){
        List<String> languagesList= List.of("Haskell","Java","Rust","Python","Java");
        assertThat(languagesList.size()).isEqualTo(5);
        assertThat(languagesList.filter(s -> s.equals("Java")).size()).isEqualTo(2);
    }

    @Test
    void vavrCollectionsAreImmutable(){
        List<String> languagesList= List.of("Haskell","Java","Rust","Python","Java");
        List<String> filteredLanguagesList = languagesList.distinct();
        assertThat(filteredLanguagesList.size()).isEqualTo(4);
        assertThat(filteredLanguagesList).isNotEqualTo(languagesList);
    }

    @Test
    void setsDoNotAllowDupliactes(){
        Set<String> languagesSet = HashSet.of("Haskell","Java","Rust","Python","Java");
        assertThat(languagesSet).contains("Java");
        assertThat(languagesSet.size()).isEqualTo(4);
    }

    @Test
    void mappingAndFilteringWithDistinct(){
        List<String> languagesList= List.of("Haskell","Java","Rust","Python","Java");
        List<String> transformedList = languagesList
                .map(s -> {
                    System.out.println("avant Uppercase : "+ s);
                    return s.toUpperCase();
                })
                .distinct()
                .map(s -> {
                    System.out.println("après Uppercase : "+ s);
                    return s;
                });
        assertThat(transformedList.size()).isEqualTo(languagesList.size() -1);

    }

    @Test
    void partitionAListByCase(){
        List<String> languagesList= List.of("Haskell","Java","rust","Python","java");
        Tuple2<List<String>,List<String>> lowerCaseLanguagesList = languagesList.partition(s ->s.toLowerCase().equals(s) );
        assertThat(lowerCaseLanguagesList._1().size()).isEqualTo(2);// .1() contains the list of elements matching predicate
        assertThat(lowerCaseLanguagesList._2).contains("Python");   //.2() contains llist of elements not matching the predicate
    }

    @Test
    void dropWhileCreatesANewListFiltered(){
        List<Integer> someMiscValues = List.of(1,3,5,7,9,10,11,13);
        List<Integer>  valuesCutUntilFirstEven = someMiscValues.dropWhile(integer -> integer%2 !=0);
        assertThat(valuesCutUntilFirstEven.size()).isEqualTo(3);
        assertThat(valuesCutUntilFirstEven).contains(10);
        assertThat(valuesCutUntilFirstEven).doesNotContain(1);
        assertThat(valuesCutUntilFirstEven).doesNotContain(3);
    }

    @Test
    void combinateSTrings(){
        List<String> addressList = List.of("Hello","World","of","Java");
        List<List<String>> allCombinations = addressList.combinations();
        assertThat(allCombinations).size().isGreaterThan(addressList.size());
        assertThat(allCombinations).contains(List.of("Hello","World","of","Java"));
        assertThat(allCombinations).contains(List.of("Hello","World","of"));
        assertThat(allCombinations).contains(List.of("Hello","World"));
        assertThat(allCombinations).contains(List.empty());
    }
}
