package cn.javabook.chapter05.lambda;

@FunctionalInterface
public interface FruitInterface<T> {
	boolean select(T t);

	default String source() {
		return "apple";
	}

	// 可选方法，占位符
	default String remove() {
		throw new UnsupportedOperationException();
	}

	// 给接口加上静态方法
	public static void staticMethod() {
		System.out.println("这是一个静态方法");
	}
}
