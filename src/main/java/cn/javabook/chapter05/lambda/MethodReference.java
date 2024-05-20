package cn.javabook.chapter05.lambda;

import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 四种不同的方法引用
 *
 */
public class MethodReference {
    public static void main(String[] args) {
        /*
         * 第一种方法引用
         */
        Function<String, Integer> stringToInteger1 = (String s) -> Integer.parseInt(s);
        Function<String, Integer> stringToInteger2 = Integer::parseInt;

        /*
         * 第二种方法引用
         */
        // 比较两个字符串的大小
        Comparator<String> comparator1 = (s1, s2) -> s1.compareToIgnoreCase(s2);
        // 判断字符串列表是否包含某个字符串
        BiPredicate<List<String>, String> contains1 = (list, e) -> list.contains(e);
        // 和第一行效果一致
        Comparator<String> comparator2 = String::compareToIgnoreCase;
        // 和第二行效果一致
        BiPredicate<List<String>, String> contains2 = List::contains;

        /*
         * 第三种方法引用
         */
        Consumer<Fruit> consumer1 = (s) -> System.out.println();
        // 映射型接口
        Function<List<Track>, Stream<? extends Track>> func1 = ele -> ele.stream();
        // 和第一行效果一致
        Consumer<Fruit> consumer2 = System.out::println;
        // 和第二行效果一致
        Function<List<Track>, Stream<? extends Track>> func2 = Collection::stream;

        /*
         * 第四种方法引用
         */
        List<Fruit> list = new ArrayList<>();
        Predicate<Fruit> predicate1 = fruit -> list.add(fruit);
        Predicate<Fruit> predicate2 = list::add;
    }
}
