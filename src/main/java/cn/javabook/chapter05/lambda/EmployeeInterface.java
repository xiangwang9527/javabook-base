package cn.javabook.chapter05.lambda;

@FunctionalInterface
public interface EmployeeInterface<T> {
	boolean select(T t);
}
