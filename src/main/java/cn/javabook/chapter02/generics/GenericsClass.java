package cn.javabook.chapter02.generics;

/**
 * 方法带<T>标记与不带<T>标记的比较
 *
 */
public class GenericsClass<T extends Entity> {
    /**
     * 方法不带<T>标记
     */
    public void notSign(T t) {
        System.out.println("no Sign  " + t.toString());
    }

    /**
     * 方法带<T>标记
     */
    public <T> void hasSign(T e) {
        System.out.println("has Sign  " + e.toString());
    }

    public static void main(String[] args) {
        GenericsClass<Entity> clazz = new GenericsClass<>();
        Entity entity = new Entity();
        UserEntity userEntity = new UserEntity();
        UserDao userDao = new UserDao();

        System.out.println("不带标记的方法：");
        clazz.notSign(entity);
        clazz.notSign(userEntity);
        // 不能编译通过
        // 因为在GenericsClass<T extends Entity>中已经限定了T为Entity及其子类，
        // 所以不能再加入UserDao;
        // clazz.notSign(userDao);

        System.out.println("带标记的方法：");
        clazz.hasSign(entity);
        clazz.hasSign(userEntity);
        // 带上前缀<T>，就是在告诉编译器：这是新指定的一个类型，代表该方法自己独有的某个类，
        // 跟GenericsClass<T extends Entity>中的Entity及其子类没有任何关系
        // 或者说
        // hasSign方法重新定义泛型T、隐藏或者代替了GenericsClass<T>中的T，不再受限于Entity及其子类
        clazz.hasSign(userDao);
    }
}
