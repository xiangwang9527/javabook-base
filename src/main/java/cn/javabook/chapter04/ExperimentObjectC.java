/**
 * 在命令行中执行代码时需要先注释掉package相关的行再用javac命令编译
 * 否则会出现"找不到主类"的错误
 *
 */
package cn.javabook.chapter04;

/**
 * 代码运行基于如下命令（可修改并拷贝后执行）
 *
 * 注意JVM参数的不同：-Xms100M -Xmx100M -Xmn20M -XX:SurvivorRatio=6
 *
 * cd [xxx.java源代码所在的目录]
 * javac ExperimentObjectC.java
 * java -Xms100M -Xmx100M -Xmn20M -XX:SurvivorRatio=6 -XX:PretenureSizeThreshold=10M -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -verbose:gc -Xloggc:[指定目录地址]\gc.log ExperimentObjectC
 */
public class ExperimentObjectC {
    public static void main(String[] args) {
        byte[] array1 = new byte[128 * 1024];
        for (int i = 0; i < 20; i++) {
            byte[] array2 = new byte[4 * 1024 * 1024];
            array2 = new byte[4 * 1024 * 1024];
            array2 = new byte[3 * 1024 * 1024];
            array2 = null;
        }
    }
}
