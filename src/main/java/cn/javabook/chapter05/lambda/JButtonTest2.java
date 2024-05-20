package cn.javabook.chapter05.lambda;

import javax.swing.*;
import java.awt.*;

public class JButtonTest2 extends JFrame {

    public JButtonTest2() {
        setLayout(new GridLayout(1, 1, 1, 1));
        Container container = getContentPane();

        JButton jb1 = new JButton("button1");
        jb1.addActionListener(e -> JOptionPane.showMessageDialog(null, "我是按钮1"));

        JButton jb2 = new JButton("button2");
        jb2.addActionListener(e -> JOptionPane.showMessageDialog(null, "我是按钮2"));

        container.add(jb1);
        container.add(jb2);
        setTitle("按钮测试");
        setVisible(true);
        setSize(500, 300);
        setLocation(960, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        // 这里开一个多线程来运行Swing
        new Thread(new Runnable() {
            @Override
            public void run() {
                JButtonTest2 jb = new JButtonTest2();
                System.out.println("this is runnable start -> " + Thread.currentThread());
            }
        }).start();
    }

}
