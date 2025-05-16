package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LeftPanelUI extends JPanel {

    public LeftPanelUI(DBMSMainFrame mainFrame) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setBackground(Color.LIGHT_GRAY);


        // 添加功能按钮
        String[] menuItems = {"主页", "用户管理", "数据库管理", "数据查询", "数据库监控"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setPreferredSize(new Dimension(160, 40));
            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            // 设置按钮样式
            button.setContentAreaFilled(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            button.setFocusPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            // 根据不同按钮设置不同的监听器
            switch (item) {
                case "主页":
                    button.addActionListener(e -> mainFrame.setHomePageTab());
                    break;
                case "用户管理":
                    //button.addActionListener(e -> mainFrame.setUserManagementTab());
                    button.addActionListener(e -> mainFrame.setUserManagementDialog());
                    break;
                case "数据库管理":
                    button.addActionListener(e -> mainFrame.setDatabaseManagementTab());
                    break;
                case "数据查询":
                    button.addActionListener(e -> mainFrame.setQueryTab());
                    break;
            }
            this.add(button);
        }
    }
}