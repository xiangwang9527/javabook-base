package cn.javabook.chapter02.generics;

import java.util.ArrayList;
import java.util.List;

/**
 * 宠物类
 */
class Pet {};

/**
 * 阿猫
 */
class Cat extends Pet {};
class TianYuanCat extends Cat {};
class LihuaCat extends TianYuanCat {};
class SanhuaCat extends TianYuanCat {};
class XianluoCat extends Cat {};

/**
 * 阿狗
 */
class Dog extends Pet {};
class BianmuDog extends Dog {};
class ChaiquanDog extends Dog {};

/**
 * 宠物店
 */
public class PetStore<T> {
    private T item;

    public PetStore(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

//    // 上界通配符
//    public static void main(String[] args) {
//        // 构造器参数只允许Cat及其子类通过
//        PetStore<? extends Cat> petStore = new PetStore<>(new TianYuanCat());
//        // 不能保存任何元素
//        // petStore.setItem(tianYuanCat);
//        // 取出来的只能放在Cat及其父类里，这可以保证安全
//        Object object = petStore.getItem();
//        Pet pet = petStore.getItem();
//        Cat cat = petStore.getItem();
//        // 获取Cat的子类需要转型，但不保证安全
//        TianYuanCat tianYuanCat = (TianYuanCat) petStore.getItem();
//        // 取Dog及其子类则会报错
//        // BianmuDog bianmuDog = petStore.getItem();
//
//        List<? extends Cat> list = new ArrayList<>();
//        // 换成List也是一样无法保存
//        // list.add(cat);
//        // list.add(tianYuanCat);
//        // 但可以读取
//        object = list.get(0);
//        pet = list.get(0);
//        cat = list.get(0);
//        // 获取子类需要转型，但不保证安全
//        tianYuanCat = (TianYuanCat) list.get(0);
//    }

    // 下界通配符
    public static void main(String[] args) {
        // 构造器参数只允许XianluoCat及其父类通过
        PetStore<? super XianluoCat> petStore = new PetStore<>(new XianluoCat());
        // 除了Obejct，不能获取任何元素
        Object object = petStore.getItem();
        // Pet pet = petStore.getItem();
        // Cat cat = petStore.getItem();
        Cat cat = new Cat();
        XianluoCat xianluoCat = new XianluoCat();
        // 除了XianluoCat及其子类，保存不了其他东西
        petStore.setItem(xianluoCat);
        // petStore.setItem(cat);

        List<? super XianluoCat> list = new ArrayList<>();
        // 换成List也是一样无法获取具体子类，只能获取Object
        object = list.get(0);
        // Pet pet = list.get(0);
        // cat = list.get(0);
        // 除了XianluoCat及其子类，保存不了其他东西
        list.add(xianluoCat);
        // list.add(cat);
    }
}
