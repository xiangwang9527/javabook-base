package cn.javabook.chapter05.lambda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JButtonTest1 extends JFrame {
    public JButtonTest1() {
        setLayout(new GridLayout(1, 1, 1, 1));
        Container container = getContentPane();

        JButton jb1 = new JButton("button1");
        jb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "我是按钮1");
            }
        });

        JButton jb2 = new JButton("button2");
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "我是按钮2");
            }
        });

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
                JButtonTest1 jb = new JButtonTest1();
                System.out.println("this is runnable start -> " + Thread.currentThread());
            }
        }).start();
    }
}
