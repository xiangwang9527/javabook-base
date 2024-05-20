package cn.javabook.chapter07;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 消息服务
 *
 */
public class ChatServer {
    public static void main(String[] args) {
//        try (ServerSocket server = new ServerSocket(9527)) {
//            while (true) {
//                try {
//                    // 接受客户端连接请求
//                    Socket client = server.accept();
//                    // 打印输出
//                    PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
//                    // 读取输入
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                    // 启动服务的虚拟线程，处理客户端数据。其实他可以连接多个客户端，只要ChatClient不在一起就行
//                    Thread.ofVirtual().start(() -> {
//                        try (writer; reader) {
//                            String input;
//                            while ((input = reader.readLine()) != null) {
//                                System.out.println(input);
//                                writer.println(input);
//                            }
//                        } catch (IOException e) {
//                            System.out.println(e.getMessage());
//                        }
//                    });
//                } catch (Throwable throwable) {
//                    System.out.println(throwable.getMessage());
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("连接异常：" + e.getMessage());
//        } finally {
//            System.exit(1);
//        }
    }
}
