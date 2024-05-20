package cn.javabook.chapter02.generics;

import java.util.*;

/**
 * 泛型擦除
 *
 */
class Product {}

class Shop<T> {}

class Particle<K, V> {}

public class GenericsErase {
    public static void main(String[] args) {
        // ArrayList<String>和ArrayList<Integer>应该是不同的类型，但结果是它们完全相等
        Class<?> c1 = new ArrayList<String>().getClass();
        Class<?> c2 = new ArrayList<Integer>().getClass();
        System.out.println(c1 == c2);
        // 无法从泛型获得任何有关参数类型的信息
        List<User> list = new ArrayList<>();
        Map<User, Product> map = new HashMap<>();
        Shop<User> shop = new Shop<>();
        Particle<Long, Double> particle = new Particle<>();
        System.out.println(Arrays.toString(list.getClass().getTypeParameters()));
        System.out.println(Arrays.toString(map.getClass().getTypeParameters()));
        System.out.println(Arrays.toString(shop.getClass().getTypeParameters()));
        System.out.println(Arrays.toString(particle.getClass().getTypeParameters()));
    }
}
