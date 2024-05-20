package cn.javabook.chapter05.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 水果类
 *
 */
public class Fruit {
    private int weight;
    private String color;
    private int price;

    public Fruit(int weight, String color, int price) {
        this.weight = weight;
        this.color = color;
        this.price = price;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public static List<Fruit> filter(List<Fruit> fruits, FruitInterface<Fruit> fi) {
        List<Fruit> list = new ArrayList<>();
        for(Fruit fruit : fruits) {
            if (fi.select(fruit)) {
                list.add(fruit);
            }
        }
        return list;
    }

    public static void main(String[] args) {
        List<Fruit> fruits = Arrays.asList(
                new Fruit(15, "green", 30),
                new Fruit(21, "red", 8),
                new Fruit(25, "blue", 20)
        );

        // 符合重量要求的水果
        List<Fruit> heavyFruits = new ArrayList<>();
        for (Fruit fruit : fruits) {
            if (fruit.getWeight() > 20) {
                heavyFruits.add(fruit);
            }
        }

        // 符合价格要求的水果
        List<Fruit> cheapFruits = new ArrayList<>();
        for (Fruit fruit : fruits) {
            if (fruit.getPrice() < 10) {
                cheapFruits.add(fruit);
            }
        }

        // 符合重量要求的水果
        FruitWeightCondition fruitWeight = new FruitWeightCondition();
        heavyFruits = new ArrayList<>();
        for (Fruit fruit : fruits) {
            if (fruitWeight.select(fruit)) {
                heavyFruits.add(fruit);
            }
        }

        // 符合价格要求的水果
        FruitPriceCondition fruitPrice = new FruitPriceCondition();
        cheapFruits = new ArrayList<>();
        for (Fruit fruit : fruits) {
            if (fruitPrice.select(fruit)) {
                cheapFruits.add(fruit);
            }
        }

        // 第一种方式
        heavyFruits = filter(fruits, new FruitInterface<Fruit>() {
            @Override
            public boolean select(Fruit fruit) {
                return fruit.getWeight() > 20;
            }
        });

        // 第二种方式
        List<Fruit> weight = filter(fruits, fruit -> fruit.getWeight() > 20);
        List<Fruit> price = filter(fruits, fruit -> fruit.getPrice() < 10);
    }
}
