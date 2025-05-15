package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// 注册界面类
class RegisterFrame extends JFrame {

    private final JPanel registerBox = new JPanel();

    private final JTextField usernameTextField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();
    private final JTextField databaseAddressTextField = new JTextField();

    private final JButton registerButton = new JButton();
    private final JButton backToLoginButton = new JButton();

    public boolean registerSuccess = false; // 实际应用中根据后端验证结果设置

    public RegisterFrame() {
        // 设置窗口
        this.setLayout(new BorderLayout());
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("简易 DBMS 系统 - 注册");

        // 注册框样式
        registerBox.setLayout(new GridBagLayout());
        registerBox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        registerBox.setPreferredSize(new Dimension(400, 400));

        // 设置用户名输入框
        usernameTextField.setPreferredSize(new Dimension(300, 40));

        // 设置密码输入框
        passwordField.setPreferredSize(new Dimension(300, 40));

        // 设置确认密码输入框
        confirmPasswordField.setPreferredSize(new Dimension(300, 40));

        // 设置注册按钮
        registerButton.setPreferredSize(new Dimension(300, 40));
        registerButton.setText("注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // 注册逻辑
                String username = getUsernameTextField();
                char[] password = getPasswordField();
                char[] confirmPassword = getConfirmPasswordField();

                String passwordStr = new String(password);
                String confirmPasswordStr = new String(confirmPassword);

                // 模拟注册成功
                if (passwordStr.equals(confirmPasswordStr)) {

                    //实际逻辑registerSuccess=bool fun(String username, char[] password,char[] confirmPassword){}//来自后端方法


                    registerSuccess=true;//实际

                    if (registerSuccess) {
                        System.out.println("注册成功");
                        // 跳转到登录界面
                        LoginFrame loginFrame = new LoginFrame();
                        loginFrame.setVisible(true);
                        // 关闭当前注册窗口
                        dispose();
                    } else {
                        System.out.println("注册失败");
                        // 显示错误提示
                    }
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "两次输入的密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 设置返回登录按钮
        backToLoginButton.setPreferredSize(new Dimension(300, 40));
        backToLoginButton.setText("返回登录");
        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 跳转到登录界面
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                // 关闭当前注册窗口
                dispose();
            }
        });

        // 添加组件到注册框
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);

        // 第一行：标题
        JLabel titleLabel = new JLabel("简易 DBMS 系统 - 注册");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        registerBox.add(titleLabel, gbc);

        // 第二行：用户名
        gbc.gridwidth = 1;
        registerBox.add(new JLabel("用户名"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        registerBox.add(usernameTextField, gbc);

        // 第三行：密码
        gbc.gridwidth = 1;
        registerBox.add(new JLabel("密码"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        registerBox.add(passwordField, gbc);

        // 第四行：确认密码
        gbc.gridwidth = 1;
        registerBox.add(new JLabel("确认密码"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        registerBox.add(confirmPasswordField, gbc);

        // 第六行：注册按钮
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        registerBox.add(registerButton, gbc);

        // 第七行：返回登录按钮
        registerBox.add(backToLoginButton, gbc);

        // 添加注册框到主窗口
        this.add(registerBox, BorderLayout.CENTER);

        // 设置注册框居中显示
        this.setLocationRelativeTo(null);
    }

    // 留出函数接口：获取用户名输入框的值
    public String getUsernameTextField() {
        return usernameTextField.getText();
    }

    // 留出函数接口：设置用户名输入框的值
    public void setUsernameTextField(String text) {
        usernameTextField.setText(text);
    }

    // 留出函数接口：获取密码输入框的值
    public char[] getPasswordField() {
        return passwordField.getPassword();
    }

    // 留出函数接口：获取确认密码输入框的值
    public char[] getConfirmPasswordField() {
        return confirmPasswordField.getPassword();
    }

    // 留出函数接口：设置密码输入框的值
    public void setPasswordField() {
        // 注意：密码字段通常不显示明文，所以不建议直接设置密码文本
        // 这里只提供一个空的设置方法作为示例
    }

    // 留出函数接口：获取数据库地址输入框的值
    public String getDatabaseAddressTextField() {
        return databaseAddressTextField.getText();
    }

    // 留出函数接口：设置数据库地址输入框的值
    public void setDatabaseAddressTextField(String text) {
        databaseAddressTextField.setText(text);
    }
}