package cn.javabook.chapter02.proxy;

/**
 * 买方行为
 *
 */
public class Consumer implements Renting {
    @Override
    public void renthouse() {
        System.out.println("租客租房");
    }
}
