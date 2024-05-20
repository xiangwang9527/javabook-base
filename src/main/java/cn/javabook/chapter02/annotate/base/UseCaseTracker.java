package cn.javabook.chapter02.annotate.base;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 解析注解
 *
 */
public class UseCaseTracker {
	public static void trackUseCases(List<Integer> useCases, Class<?> cl) {
		for (Method m : cl.getDeclaredMethods()) {
			UseCase uc = m.getAnnotation(UseCase.class);
			if (uc != null) {
				System.out.println("发现注解id：" + uc.id() + 
						" - 注解description：" + uc.description());
				// 剔除已有注解
				useCases.remove(Integer.valueOf(uc.id()));
			}
		}
		useCases.forEach(i -> System.out.println("缺少用例id：" + i));
	}

	public static void main(String[] args) {
		List<Integer> useCases = IntStream.range(1, 5).boxed().collect(Collectors.toList());
		trackUseCases(useCases, PasswordUtils.class);
	}
}
