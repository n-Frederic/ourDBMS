package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

// 定义UI类，继承自JFrame用于创建图形用户界面窗口
public class UI extends JFrame {

    // 界面组件变量
    private JTextArea outputArea; // 输出区域文本框
    private String prompt = "mysql> "; // 命令提示符
    private boolean isRedirecting = false; // 标识是否正在重定向输出

    public String rightCode="11111111";

    // 定义业务逻辑接口，用于处理用户命令
    public interface CommandHandler {
        void handleCommand(String cmd); // 处理命令的方法
    }

    private final CommandHandler commandHandler; // 外部传入的命令处理器

    // 构造函数，接收命令处理器并初始化界面和输出重定向
    public UI(CommandHandler handlerring) {
        this.commandHandler = handlerring;
        initializeUI(); // 初始化用户界面
        redirectSystemStreams(); // 重定向系统输出流
    }


    // 初始化用户界面的方法
    private void initializeUI() {
        setTitle("DBMS Console"); // 设置窗口标题
        setSize(800, 600); // 设置窗口大小
        setLocationRelativeTo(null); // 窗口居中显示
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 设置关闭操作为退出程序

        // 配置输出区域文本框
        outputArea = new JTextArea();
        outputArea.setEditable(false); // 设置为只读
        outputArea.setBackground(Color.BLACK); // 设置背景色为黑色
        outputArea.setForeground(Color.WHITE); // 设置前景色（文本色）为白色
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14)); // 设置字体

        // 将文本框封装在滚动面板中
        JScrollPane scrollPane = new JScrollPane(outputArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER); // 添加到窗口中央

        // 设置初始密码验证
        setupPasswordAuthentication();
        setVisible(true); // 显示窗口
    }

    // 设置初始密码验证逻辑
    private void setupPasswordAuthentication() {
        outputArea.append("Enter password: "); // 显示密码提示
        outputArea.setEditable(true); // 设置文本框可编辑

        // 为文本框添加键盘事件监听器
        outputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 如果按下回车键，处理密码输入
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handlePasswordInput();
                    e.consume(); // 标记事件为已处理
                }
                // 处理输入限制，防止修改提示符内容
                handleInputRestrictions(e, "Enter password: ");
            }
        });
    }

    // 处理密码输入逻辑
    private void handlePasswordInput() {
        String content = outputArea.getText(); // 获取文本框全部内容
        int promptIndex = content.lastIndexOf("Enter password: "); // 找到密码提示位置
        String input = content.substring(promptIndex + 16).trim(); // 提取用户输入的密码

        // 验证密码是否正确
        if (rightCode.equals(input)) {
            outputArea.removeKeyListener(outputArea.getKeyListeners()[0]); // 移除密码验证监听器
            showWelcomeMessage(); // 显示欢迎信息
            setupCommandInput(); // 设置命令输入逻辑
        } else {
            showPasswordError(); // 显示密码错误信息
        }
    }

    // 显示欢迎信息
    private void showWelcomeMessage() {
        appendToConsole("\n\nWelcome to Moreight DBMS\n"); // 欢迎信息
        appendToConsole("Version 1.0\n"); // 版本信息
        appendToConsole("Type 'help' for commands\n"); // 帮助提示
        appendPrompt(); // 显示命令提示符
    }

    // 设置命令输入逻辑
    private void setupCommandInput() {
        // 为文本框添加键盘事件监听器，处理命令输入
        outputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // 检测回车键
                    handleCommandInput(); // 处理命令输入
                    e.consume(); // 标记事件为已处理
                }
                handleInputRestrictions(e, prompt); // 处理输入限制
            }
        });
    }

    // 处理命令输入逻辑
    private void handleCommandInput() {
        String content = outputArea.getText(); // 获取文本框全部内容
        int promptIndex = content.lastIndexOf(prompt); // 找到命令提示符位置
        String cmd = content.substring(promptIndex + prompt.length()).trim(); // 提取用户输入的命令

        commandHandler.handleCommand(cmd); // 调用外部命令处理器处理命令
        appendPrompt(); // 显示新的命令提示符
    }

    // 处理输入限制，防止修改提示符内容
    private void handleInputRestrictions(KeyEvent e, String targetPrompt) {
        String content = outputArea.getText(); // 获取文本框全部内容
        int promptIndex = content.lastIndexOf(targetPrompt); // 找到目标提示符位置

        // 如果按下退格键且光标在提示符区域内，禁止删除
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE &&
                outputArea.getCaretPosition() <= promptIndex + targetPrompt.length()) {
            e.consume();
        }

        // 如果光标在提示符前面，禁止输入
        if (outputArea.getCaretPosition() < promptIndex + targetPrompt.length()) {
            e.consume();
        }
    }

    // 向控制台追加文本的方法
    public void appendToConsole(String text) {
        SwingUtilities.invokeLater(() -> { // 确保UI更新在事件调度线程执行

            outputArea.append(text); // 追加文本
            outputArea.setCaretPosition(outputArea.getDocument().getLength()); // 移动光标到末尾
        });
    }

    // 追加命令提示符
    private void appendPrompt() {
        appendToConsole("\n" + prompt);
    }

    // 显示密码错误信息
    private void showPasswordError() {
        appendToConsole("\nERROR 1045 (28000): Access denied\n"); // 错误信息
        appendToConsole("Enter password: "); // 重新显示密码提示
    }

    // 重定向系统输出流，将输出显示在UI上
    private void redirectSystemStreams() {
        PrintStream ps = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // 将单个字节转换为字符并追加到控制台
                if (!isRedirecting) {
                    isRedirecting = true;
                    appendToConsole(String.valueOf((char) b));
                    isRedirecting = false;
                }
            }

            @Override
            public void write(byte[] b, int off, int len) {
                // 将字节数组转换为字符串并追加到控制台
                if (!isRedirecting) {
                    isRedirecting = true;
                    appendToConsole(new String(b, off, len));
                    isRedirecting = false;
                }
            }
        });

        System.setOut(ps); // 设置自定义输出流为系统标准输出
        System.setErr(ps); // 设置自定义输出流为系统标准错误输出
    }
}