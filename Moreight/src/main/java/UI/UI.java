package UI;

import javax.swing.*; // 导入Swing库，用于创建图形用户界面
import java.awt.*; // 导入AWT库，用于处理图形界面的基础组件
import java.awt.event.KeyAdapter; // 导入键盘事件适配器，用于监听键盘事件
import java.awt.event.KeyEvent; // 导入键盘事件类，用于获取键盘事件的详细信息
import java.io.IOException; // 导入IOException，用于处理可能发生的输入输出异常
import java.io.OutputStream; // 导入OutputStream，用于重定向输出
import java.io.PrintStream; // 导入PrintStream，用于创建打印流

public class UI extends JFrame { // 定义UI类，继承自JFrame，用于创建窗口
    private final JTextArea outputArea; // 定义输出区域，用于显示控制台输出和用户输入
    private String prompt = "mysql> "; // 定义命令提示符
    private boolean isRedirecting = false; // 标志位，用于判断是否正在重定向输出

    private boolean isRightCode=false;

    private String usename="DBMS";

    private String useCode="11111111";



    public UI() { // 构造函数，用于初始化UI界面
        // 初始化界面设置
        setTitle("控制台"); // 设置窗口标题
        setSize(800, 600); // 设置窗口大小
        setLocationRelativeTo(null); // 设置窗口居中显示
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 设置窗口关闭操作

        Container contentPane = getContentPane(); // 获取窗口内容面板
        contentPane.setLayout(new BorderLayout()); // 设置内容面板布局为边框布局

        // 初始化输出区域
        outputArea = new JTextArea(); // 创建文本区域，用于显示输出
        outputArea.setEditable(false); // 设置文本区域不可编辑
        outputArea.setBackground(Color.BLACK); // 设置文本区域背景色为黑色
        outputArea.setForeground(Color.WHITE); // 设置文本区域前景色为白色
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // 设置文本区域字体
        JScrollPane scrollPane = new JScrollPane(outputArea); // 创建滚动面板，包含文本区域
        contentPane.add(scrollPane, BorderLayout.CENTER); // 将滚动面板添加到内容面板中心

        outputArea.append("Enter password:");
        outputArea.setEditable(true); // 设置文本区域可编辑，以便用户输入
        outputArea.addKeyListener(new KeyAdapter() { // 添加键盘事件监听器
            @Override
            public void keyPressed(KeyEvent e) { // 重写按键事件方法
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // 如果按下回车键
                    e.consume(); // 消耗事件，防止默认行为
                    int promptIndex = outputArea.getText().lastIndexOf("Enter password:"); // 获取提示符在文本中的索引
                    String input = outputArea.getText().substring(promptIndex + "Enter password:".length()); // 获取用户输入
                    if (input.equals(useCode)) {
                        outputArea.append("\n");
                        outputArea.append("Welcome to the Moreight DBMS.Your Moreight DBMS connection id is ");
                        outputArea.append(usename + ".");
                        outputArea.append("\n");
                        isRightCode = true;
                    }
                    outputArea.setCaretPosition(outputArea.getDocument().getLength()); // 将光标移动到提示符后
                    outputArea.append("\n");
                    //outputArea.append(prompt); // 在文本区域添加新的提示符
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) { // 如果按下退格键
                    int promptIndex = outputArea.getText().lastIndexOf("Enter password:"); // 获取提示符在文本中的索引
                    if (outputArea.getCaretPosition() <= promptIndex + "Enter password:".length()) { // 如果光标在提示符后
                        e.consume(); // 消耗事件，防止默认行为
                    }
                } else if (outputArea.getCaretPosition() < outputArea.getText().lastIndexOf("Enter password:") + "Enter password:".length()) { // 如果光标在提示符前
                    e.consume(); // 消耗事件，防止默认行为
                }
            }
        });




        if(isRightCode) {
            // 模拟命令行输入
            outputArea.setEditable(true); // 设置文本区域可编辑，以便用户输入
            outputArea.append(prompt); // 在文本区域添加命令提示符
            outputArea.addKeyListener(new KeyAdapter() { // 添加键盘事件监听器
                @Override
                public void keyPressed(KeyEvent e) { // 重写按键事件方法
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) { // 如果按下回车键
                        e.consume(); // 消耗事件，防止默认行为
                        int promptIndex = outputArea.getText().lastIndexOf(prompt); // 获取提示符在文本中的索引
                        String input = outputArea.getText().substring(promptIndex + prompt.length()); // 获取用户输入
                        handleInput(input); // 处理用户输入
                        outputArea.setCaretPosition(outputArea.getDocument().getLength()); // 将光标移动到提示符后
                        outputArea.append("\n");
                        //outputArea.append(prompt); // 在文本区域添加新的提示符
                    } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) { // 如果按下退格键
                        int promptIndex = outputArea.getText().lastIndexOf(prompt); // 获取提示符在文本中的索引
                        if (outputArea.getCaretPosition() <= promptIndex + prompt.length()) { // 如果光标在提示符后
                            e.consume(); // 消耗事件，防止默认行为
                        }
                    } else if (outputArea.getCaretPosition() < outputArea.getText().lastIndexOf(prompt) + prompt.length()) { // 如果光标在提示符前
                        e.consume(); // 消耗事件，防止默认行为
                    }
                }
            });
        }

        contentPane.revalidate(); // 重新验证内容面板
        contentPane.repaint(); // 重新绘制内容面板

        // 重定向标准输出到文本区域
        redirectSystemOut(); // 调用方法重定向输出
    }

    // 处理用户输入
    private void handleInput(String input) { // 定义处理用户输入的方法
        if (input.trim().isEmpty()) { // 如果输入为空
            return; // 返回，不执行任何操作
        }
        //printToConsole(input); // 显示用户输入
        String output = processInput(input); // 处理输入并获取输出
        printToConsole(output); // 显示处理结果
    }

    // 模拟业务逻辑处理（替换为你的实际逻辑）
    private String processInput(String input) { // 定义模拟业务逻辑处理的方法
        System.out.println("test");
        return prompt; // 返回处理结果
    }

    // 向控制台输出内容（自动换行）
    private void printToConsole(String message) { // 定义向控制台输出内容的方法
        SwingUtilities.invokeLater(() -> { // 在事件调度线程中执行
            if (outputArea != null && outputArea.isDisplayable()) { // 如果文本区域可用
                try {
                    outputArea.append(message); // 将消息追加到文本区域
                } catch (Exception e) { // 捕获并处理异常
                    System.err.println("Exception occurred while appending message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // 重定向 System.out 到文本区域
    private void redirectSystemOut() { // 定义重定向标准输出的方法
        try {
            PrintStream printStream = new PrintStream(new OutputStream() { // 创建新的打印流
                @Override
                public void write(int b) throws IOException { // 重写写入方法
                    if (!isRedirecting) { // 如果不在重定向输出
                        isRedirecting = true; // 设置正在重定向输出
                        printToConsole(String.valueOf((char) b)); // 将字符追加到文本区域
                        isRedirecting = false; // 重置正在重定向输出
                    }
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException { // 重写写入方法
                    if (!isRedirecting) { // 如果不在重定向输出
                        isRedirecting = true; // 设置正在重定向输出
                        printToConsole(new String(b, off, len)); // 将字符串追加到文本区域
                        isRedirecting = false; // 重置正在重定向输出
                    }
                }
            });
            System.setOut(printStream); // 设置新的标准输出
            System.setErr(printStream); // 设置新的标准错误输出
        } catch (SecurityException se) { // 捕获并处理安全异常
            System.err.println("设置标准输出重定向时出现安全异常: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) { // 捕获并处理其他异常
            System.out.println("Exception occurred during redirection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { // 主方法，程序入口点
        SwingUtilities.invokeLater(() -> { // 在事件调度线程中执行
            UI ui = new UI(); // 创建UI实例
            ui.setVisible(true); // 显示窗口
        });
    }
}