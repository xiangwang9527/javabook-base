/**
 * 在命令行中执行代码时需要先注释掉package相关的行再用javac命令编译
 * 否则会出现"找不到主类"的错误
 *
 */
package cn.javabook.chapter04;

/**
 * 代码运行基于如下命令（可修改并拷贝后执行）
 *
 * 注意JVM参数的不同：-XX:PretenureSizeThreshold=3M
 *
 * cd [xxx.java源代码所在的目录]
 * javac ExperimentObjectE.java
 * java -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3M -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -verbose:gc -Xloggc:[指定目录地址]\gc.log ExperimentObjectE
 */
public class ExperimentObjectE {
    public static void main(String[] args) {
        byte[] array1 = new byte[4 * 1024 * 1024];
        array1 = null;
        byte[] array2 = new byte[2 * 1024 * 1024];
        byte[] array3 = new byte[2 * 1024 * 1024];
        byte[] array4 = new byte[2 * 1024 * 1024];
        byte[] array5 = new byte[128 * 1024];
        byte[] array6 = new byte[2 * 1024 * 1024];
    }
}
