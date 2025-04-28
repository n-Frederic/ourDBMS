package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI {
    private JFrame mainFrame;
    private JTextArea commandArea;
    private JTextArea resultArea;
    private JButton executeButton;

    public UI() {
        createUI();
    }

    public void createUI() {
        // 创建主窗口
        mainFrame = new JFrame("Moreight DBMS");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);

        // 创建命令输入区域
        commandArea = new JTextArea(5, 68);
        JScrollPane commandScrollPane = new JScrollPane(commandArea);

        // 创建结果显示区域
        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        // 创建执行按钮
        executeButton = new JButton("执行");
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCommand();
            }
        });

        // 创建一个面板用于放置命令输入区域和执行按钮
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(commandScrollPane, BorderLayout.CENTER);
        inputPanel.add(executeButton, BorderLayout.WEST);

        // 使用 JSplitPane 实现上下区域可调节
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                inputPanel,
                resultScrollPane
        );
        splitPane.setDividerLocation(150); // 设置初始分割位置
        splitPane.setResizeWeight(0.2);    // 设置上下区域的权重

        // 将 JSplitPane 添加到主窗口
        mainFrame.add(splitPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    public void executeCommand() {
        String command = commandArea.getText();
        String result = "命令执行成功: " + command; // 模拟结果
        resultArea.setText(result); // 将结果显示在 resultArea 中
    }
}