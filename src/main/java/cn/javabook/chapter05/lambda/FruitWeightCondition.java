package cn.javabook.chapter05.lambda;

public class FruitWeightCondition implements FruitInterface<Fruit> {
	@Override
	public boolean select(Fruit fruit) {
		return fruit.getWeight() > 20;
	}
}
