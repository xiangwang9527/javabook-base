package cn.javabook.chapter02.proxy;

/**
 * 需要代理的行为
 *
 */
public interface NewRenting {
    // 租房
    default public void renthouse() {
        System.out.println("租客租房");
    };
}
