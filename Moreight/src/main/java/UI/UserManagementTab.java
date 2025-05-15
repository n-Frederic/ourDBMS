package UI;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

import User.UserManager;
import Controller.UserAuthentication;

public class UserManagementTab extends JPanel {

    public UserManagementTab() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // 标题
        JLabel titleLabel = new JLabel("用户管理");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        this.add(titleLabel, BorderLayout.NORTH);

        // 内容
        JLabel contentLabel = new JLabel("这里将显示用户管理功能");
        contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(contentLabel, BorderLayout.CENTER);

//        // 添加文本框
//        JTextField scTextField = new JTextField(20); // 创建一个宽度为20的文本框
//        this.add(scTextField, BorderLayout.SOUTH);
//
//        // 创建Scanner对象来读取文本框的输入
//        Scanner sc = new Scanner(scTextField.getText());
//
//        if (UserManager.getCurrentUser().hasPermission("admin")) {
//            System.out.println("start permission");
//            System.out.println(UserAuthentication.quit);
//            while (!UserAuthentication.quit) {
//                UserAuthentication.permissionManagement(sc);
//            }
//        }
//
//        if (UserManager.getCurrentUser().hasPermission("admin")) {
//            System.out.println("start permission");
//            System.out.println(UserAuthentication.quit);
//            while (!UserAuthentication.quit) {
//                UserAuthentication.permissionManagement(sc);
//            }
//        }
    }
}
