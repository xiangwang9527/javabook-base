package cn.javabook.chapter02.annotate.base;

/**
 * 使用注解
 *
 */
public class PasswordUtils {
	// 使用@UserCase注解
	@UseCase(id = 1, description = "密码必须至少包含一位数字")
	public static boolean validate(String password) {
		return (password.matches("\\w*\\d\\w*"));
	}

	public static void main(String[] args) {
		System.out.println(PasswordUtils.validate("werrd1"));
	}
}
