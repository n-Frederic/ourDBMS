package UI;

import javax.swing.*;
import java.awt.*;

public class BlankQueryTab extends JPanel {

    private JTextArea queryTextArea;
    private JTextArea resultTextArea;

    public BlankQueryTab() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // 查询内容区域
        queryTextArea = new JTextArea();
        queryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        queryTextArea.requestFocusInWindow(); // 设置焦点
        JScrollPane queryScrollPane = new JScrollPane(queryTextArea);

        // 查询结果显示区域
        resultTextArea = new JTextArea();
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultTextArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);

        // 使用 JSplitPane 包裹两个滚动面板，允许用户调整大小
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryScrollPane, resultScrollPane);
        splitPane.setResizeWeight(0.5); // 初始时上下部分各占 50%
        splitPane.setDividerSize(5);    // 设置分割条的宽度

        this.add(splitPane, BorderLayout.CENTER);
    }

    // 获取选中的文本
    public String getSelectedText() {
        return queryTextArea.getSelectedText();
    }

    // 设置查询结果
    public void setQueryResult(String result) {
        resultTextArea.setText(result);
    }
}