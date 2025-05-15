package UI;

import javax.swing.*;
import java.awt.*;

public class DatabaseManagementTab extends JPanel {

    public DatabaseManagementTab() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // 标题
        JLabel titleLabel = new JLabel("数据库管理");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        this.add(titleLabel, BorderLayout.NORTH);

        // 数据表展示区域
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());

        // 操作按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton createButton = new JButton("创建数据库");
        createButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        createButton.setPreferredSize(new Dimension(150, 35));
        buttonPanel.add(createButton);
        this.add(buttonPanel, BorderLayout.EAST);

        // 表格数据
        String[] columnNames = {"数据库名称", "字符集", "表数量", "大小", "操作"};
        Object[][] data = {
                {"mysql", "utf8mb4", 31, "24.5 MB", "编辑 删除"},
                {"information_schema", "utf8", 79, "0.2 MB", "编辑 删除"},
                {"performance_schema", "utf8mb4", 87, "15.7 MB", "编辑 删除"},
                {"test_db", "utf8mb4", 5, "1.2 MB", "编辑 删除"}
        };

        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        this.add(tablePanel, BorderLayout.CENTER);
    }
}
