package UI;

import javax.swing.*;
import java.awt.*;

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
    }
}
