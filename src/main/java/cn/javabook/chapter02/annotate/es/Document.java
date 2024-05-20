package cn.javabook.chapter02.annotate.es;

import java.lang.annotation.*;

/**
 * elastic文档注解，定义每个elasticsearch文档上的属性
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Document {
    String index();

    String type() default "_doc";

    boolean useServerConfiguration() default false;

    short shards() default 1;

    short replicas() default 0;

    String refreshInterval() default "1s";

    String indexStoreType() default "fs";
}
