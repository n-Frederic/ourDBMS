package UI;
import User.UserManager;
import User.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserManagementDialog extends JDialog {

    //private final DBMSMainFrame mainFrame;
//    private final UserManager userManager; // 假设有一个UserManager类来处理用户操作

    private JTextField usernameField;
    private JComboBox<String> levelComboBox;
    private JTextArea resultArea;



    public UserManagementDialog() {
//        this.userManager = new UserManager(); // 假设UserManager已实现

        setTitle("用户管理");
        setSize(600, 400);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建选项面板
        JPanel optionPanel = new JPanel(new GridLayout(0, 1));
        optionPanel.setBorder(BorderFactory.createTitledBorder("选择操作"));

        // 选项按钮
        String[] options = {"授予/提升权限", "收回/降低权限", "查看所有用户", "删除用户", "返回主菜单"};
        for (String option : options) {
            JButton button = new JButton(option);
            button.addActionListener(e -> handleOptionSelection(option));
            optionPanel.add(button);
        }

        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入信息"));

        // 用户名输入框
        JPanel usernamePanel = new JPanel(new FlowLayout());
        usernamePanel.add(new JLabel("用户名:"));
        usernameField = new JTextField(15);
        usernamePanel.add(usernameField);
        inputPanel.add(usernamePanel, BorderLayout.NORTH);

        // 权限级别下拉框
        JPanel levelPanel = new JPanel(new FlowLayout());
        levelPanel.add(new JLabel("权限级别:"));
        levelComboBox = new JComboBox<>(new String[]{"admin", "user", "visitor"});
        levelPanel.add(levelComboBox);
        inputPanel.add(levelPanel, BorderLayout.CENTER);

        // 结果显示区域
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        inputPanel.add(scrollPane, BorderLayout.SOUTH);

        mainPanel.add(optionPanel, BorderLayout.WEST);
        mainPanel.add(inputPanel, BorderLayout.CENTER);


        add(mainPanel);
    }

    private void handleOptionSelection(String option) {
        switch (option) {
            case "授予/提升权限":
                grantPermission();
                break;
            case "收回/降低权限":
                revokePermission();
                break;
            case "查看所有用户":
                displayAllUsers();
                break;
            case "删除用户":
                deleteUser();
                break;
            case "返回主菜单":
                dispose();
                break;
        }
    }

    private void grantPermission() {
        String username = usernameField.getText().trim();
        String level = (String) levelComboBox.getSelectedItem();

        boolean success = UserManager.grantPermission(username, level);
        if(success) UserManager.updateUserFile();
        resultArea.setText(success ? "操作成功" : "操作失败");
    }

    private void revokePermission() {
        String username = usernameField.getText().trim();
        String level = (String) levelComboBox.getSelectedItem();

        boolean success = UserManager.revokePermission(username, level);
        if(success) UserManager.updateUserFile();
        resultArea.setText(success ? "操作成功" : "操作失败");
    }

    private void displayAllUsers() {
        resultArea.setText("=== 所有用户列表 ===\n");
        resultArea.append(String.format("%-15s %-10s\n", "用户名", "权限级别"));


        // 假设User类有getUserName和getLevel方法
        for (User user : UserManager.getAllUsers()) {
            resultArea.append(String.format("%-15s %-10s\n", user.getUserName(), user.getLevel()));
        }
    }

    private void deleteUser() {
        String username = usernameField.getText().trim();

        boolean success = UserManager.dropUser(username);
        if(success) UserManager.updateUserFile();
        resultArea.setText(success ? "操作成功" : "操作失败");
    }

    private void confirmAction() {
        // 这里可以添加确认逻辑，比如验证输入是否有效等
    }
}
