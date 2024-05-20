package cn.javabook.chapter02.annotate.base;

/**
 * 功能描述
 *
 */
@Deprecated
public class Person {
    private String name;

    @Deprecated
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
