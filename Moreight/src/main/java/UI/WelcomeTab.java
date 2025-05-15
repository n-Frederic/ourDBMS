package UI;

import java.awt.*;
import javax.swing.*;

// 欢迎标签页类
class WelcomeTab extends JPanel {

    public WelcomeTab() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // 标题
        JLabel titleLabel = new JLabel("欢迎使用 DBMS 系统");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);

        // 内容
        JLabel contentLabel = new JLabel("<html>欢迎来到简易 DBMS 系统。<br><br>" +
                "本系统提供以下功能：<br>" +
                "- 数据库管理<br>" +
                "- 用户管理<br>" +
                "- 数据查询<br><br>" +
                "请通过左侧菜单选择相应的功能进行操作。</html>");
        contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentLabel.setHorizontalAlignment(JLabel.CENTER);
        contentLabel.setVerticalAlignment(JLabel.CENTER);
        this.add(contentLabel, BorderLayout.CENTER);
    }
}

