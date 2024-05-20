package cn.javabook.chapter07;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 消息客户端
 *
 */
public class ChatClient {
    public static void main(String[] args) throws IOException {
        // 连接请求
        Socket socket = new Socket("localhost", 9527);
        // 打印输出
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        // 读取输入
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try (socket; writer) {
            String input;
            while ((input = reader.readLine()) != null) {
                writer.println(input);
                //System.out.println("echo: " + reader.readLine());
                if (input.equalsIgnoreCase("3166")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("无法连接到服务器：" + e.getMessage());
        } finally {
            System.exit(1);
        }
    }
}
