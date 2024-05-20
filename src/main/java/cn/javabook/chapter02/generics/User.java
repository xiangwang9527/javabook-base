package cn.javabook.chapter02.generics;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户类
 *
 */
public class User<K> {
    private K name;

    public K getName() {
        return name;
    }

    public void setName(K name) {
        this.name = name;
    }

    public <T> User<K> get(K k) {
        return null;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();

        User user1 = new User();
        user1.setName("java");
        System.out.println(user1.getName() instanceof String);

        User user2 = new User();
        user2.setName(123456);
        System.out.println(user2.getName() instanceof Integer);

        User user3 = new User();
        user3.setName(123.456);
        System.out.println(user3.getName() instanceof Double);

        User user4 = new User();
        System.out.println(user4.getName() instanceof Double);
    }
}
