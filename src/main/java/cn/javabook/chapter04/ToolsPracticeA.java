/**
 * 在命令行中执行代码时需要先注释掉package相关的行再用javac命令编译
 * 否则会出现"找不到主类"的错误
 *
 */
package cn.javabook.chapter04;

import java.util.concurrent.TimeUnit;

/**
 * 代码运行基于如下命令（可修改并拷贝后执行）
 * cd [xxx.java源代码所在的目录]
 * javac ToolsPracticeA.java
 * java -Xms200M -Xmx200M -Xmn100M -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3M -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -verbose:gc -Xloggc:[指定目录地址]\gc.log ToolsPracticeA
 */
public class ToolsPracticeA {
    private static void loadData() throws Exception {
        byte[] data = null;
        for (int i = 0; i < 50; i++) {
            data = new byte[100 * 1024];
        }
        data = null;
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    public static void main(String[] args) throws Exception {
        // 这里休眠30秒纯粹是为了准备好命令行的相关准备工作，如通过jps查看PID以及输入命令所需的时间
        TimeUnit.MILLISECONDS.sleep(30000);
        while (true) {
            loadData();
        }
    }
}
