package cn.javabook.chapter06;

import cn.javabook.chapter05.lambda.Employee;
import reactor.core.publisher.Flux;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Flux实例
 *
 */
public class FluxDemo {
    public static void main(String[] args) throws InterruptedException {
        /*
         * 一：两段代码完成一样的功能：一段命令式编程，一段反应式编程
         */
        // 命令式编程
        int[] array = new int[] {1, 2, 3};
        for (int i : array) {
            int result = i * i;
            if (result > 3) {
                System.out.println(result);
            }
        }
        // 反应式编程
        Flux.just(1, 2, 3).log()
            .map(i -> {
                return i * i;
            }).log()
            .filter(i -> i > 3).log()
            .subscribe(System.out::println);

//        Flux.just(1, 2, 3).log()
//            // 产生异步边界
//            .publishOn(Schedulers.boundedElastic()).log()
//            .map(i -> {
//                return i * i;
//            }).log()
//            .filter(i -> i > 3).log()
//            .subscribe(System.out::println);

        System.out.println();
        System.out.println("################");
        System.out.println();

        /*
         * 二：定期生成一个自增数
         */
        Flux.interval(Duration.ofMillis(300)).log()
            // 大于5重算
            .map(output -> {
                if (output < 5) {
                    return " output " + output;
                }
                throw new RuntimeException("good game！");
            }).log()
            // 遇到错误重新运行一遍
            .retry(1).log()
            // 元素转为类似Scala的Tuple2<Long, T>类型，第一个字段为运行间隔
            .elapsed().log()
            // 输出映射后的数据
            .subscribe(System.out::println, System.err::println);
        TimeUnit.MILLISECONDS.sleep(3000);

        System.out.println();
        System.out.println("################");
        System.out.println();

        /*
         * 三：读取文件内容并打印
         */
        Path path = Paths.get("/Users/bear/Downloads/常用命令.txt");
        // 创建Flux对象并读取文件内容
        Flux.using(
                () -> Files.lines(path),
                Flux::fromStream,
                stream -> {
                    try {
                        stream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).log()
        // 订阅Flux对象并输出文件内容
        .subscribe(System.out::println);

        System.out.println();
        System.out.println("################");
        System.out.println();

        /*
         * 四：获取员工信息并按照年龄排序后打印
         */
        List<Employee> employees = Arrays.asList(
                new Employee("梅丽", "女", 26, true, Employee.Type.OFFICER),
                new Employee("郑帅", "男", 29, false, Employee.Type.OFFICER),
                new Employee("曾美", "女", 27, true, Employee.Type.SELLER),
                new Employee("郝俊", "男", 22, true, Employee.Type.SELLER)
        );
        // 创建Flux对象
        Flux.fromIterable(employees).log()
            // 按照年龄排序
            .sort(Comparator.comparing(Employee::getAge)).log()
            // 订阅Flux对象并打印员工信息
            .subscribe(emp -> System.out.println(emp.getName() + " - " + emp.getAge()));
    }
}
