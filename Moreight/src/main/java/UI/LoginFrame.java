package UI;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// 登录界面类
class LoginFrame extends JFrame {

    private final JPanel loginBox = new JPanel();

    private final JTextField usernameTextField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    private final JButton loginButton = new JButton();
    private final JButton registerButton = new JButton(); // 添加注册按钮

    public boolean loginSuccess =false; // 实际应用中根据后端验证结果设置

    public LoginFrame() {
        // 设置窗口
        this.setLayout(new BorderLayout());
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("简易 DBMS 系统");

        // 登录框样式
        loginBox.setLayout(new GridBagLayout());
        loginBox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loginBox.setPreferredSize(new Dimension(400, 400));

        // 设置用户名输入框
        usernameTextField.setPreferredSize(new Dimension(300, 40));
        setUsernameTextField("");

        // 设置密码输入框
        passwordField.setPreferredSize(new Dimension(300, 40));
        setPasswordField();


        // 设置登录按钮
        loginButton.setPreferredSize(new Dimension(300, 40));
        loginButton.setText("登录");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path path = Paths.get("your_file_path.json");
                Reader reader = null;
                try {
                    reader = Files.newBufferedReader(path);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                JsonElement rootElement = JsonParser.parseReader(reader);
                if (!rootElement.isJsonObject()) {
                    throw new IllegalArgumentException("文件不是一个 JsonObject！");
                }
                JsonArray data = rootElement.getAsJsonArray();

                // 登录逻辑
                String username = getUsernameTextField();
                char[] password = getPasswordField();

                for (JsonElement element : data) {
                    JsonObject object = element.getAsJsonObject();
                    String name = object.get("userName").getAsString();
                    if(username.equals(name)) {
                        String pw = object.get("password").getAsString();
                        if(pw.equals(new String(password))) {
                            loginSuccess = true;
                        }
                    }
                }

                try {
                    reader.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                //登录成功
                if (loginSuccess) {
                    System.out.println("登录成功");
                    // 跳转到主界面
                    DBMSMainFrame mainFrame = new DBMSMainFrame();
                    mainFrame.setVisible(true);
                    // 关闭当前登录窗口
                    dispose();
                } else {
                    System.out.println("登录失败");
                    // 显示错误提示
                }
            }
        });

        // 设置注册按钮
        registerButton.setPreferredSize(new Dimension(300, 40));
        registerButton.setText("注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 跳转到注册界面
                RegisterFrame registerFrame = new RegisterFrame();
                registerFrame.setVisible(true);
                // 关闭当前登录窗口
                dispose();
            }
        });

        // 添加组件到登录框
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);

        // 第一行：标题
        JLabel titleLabel = new JLabel("简易 DBMS 系统");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        loginBox.add(titleLabel, gbc);

        // 第二行：用户名
        gbc.gridwidth = 1;
        loginBox.add(new JLabel("用户名"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        loginBox.add(usernameTextField, gbc);

        // 第三行：密码
        gbc.gridwidth = 1;
        loginBox.add(new JLabel("密码"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        loginBox.add(passwordField, gbc);


        // 第五行：登录按钮
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        loginBox.add(loginButton, gbc);

        // 第六行：注册按钮
        loginBox.add(registerButton, gbc);

        // 添加登录框到主窗口
        this.add(loginBox, BorderLayout.CENTER);

        // 设置登录框居中显示
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




    // 留出函数接口：设置密码输入框的值
    public void setPasswordField() {
        // 注意：密码字段通常不显示明文，所以不建议直接设置密码文本
        // 这里只提供一个空的设置方法作为示例
    }


}