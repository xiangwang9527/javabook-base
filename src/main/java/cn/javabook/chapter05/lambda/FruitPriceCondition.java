package cn.javabook.chapter05.lambda;

public class FruitPriceCondition implements FruitInterface<Fruit> {
	@Override
	public boolean select(Fruit fruit) {
		return fruit.getPrice() < 10;
	}
}
