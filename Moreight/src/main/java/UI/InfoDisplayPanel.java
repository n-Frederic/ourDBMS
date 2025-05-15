package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InfoDisplayPanel extends JPanel {

    private DBMSMainFrame mainFrame;
    private boolean isDarkMode = false; // 跟踪当前是否为暗黑模式

    public InfoDisplayPanel(DBMSMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // 设置布局为 BorderLayout
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加标题面板
        add(createTitlePanel(), BorderLayout.WEST);

        // 添加状态面板
        add(createStatusPanel(), BorderLayout.CENTER);

        // 添加操作面板
        add(createActionPanel(), BorderLayout.EAST);

        // 添加菜单栏
        add(new MenuPanel(mainFrame), BorderLayout.SOUTH);
    }

    // 创建标题面板
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = new JLabel("MorNight-DBMS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        return titlePanel;
    }

    // 创建状态面板
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel statusLabel = new JLabel("已连接  用户: 1");
        statusLabel.setForeground(Color.BLACK);
        statusPanel.add(statusLabel);

        return statusPanel;
    }

    // 创建操作面板
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // 创建主题切换按钮
        JButton themeButton = new JButton();
        themeButton.setPreferredSize(new Dimension(30, 30));
        themeButton.setContentAreaFilled(false);
        themeButton.setBorder(BorderFactory.createEmptyBorder());
        themeButton.setFocusPainted(false);

        // 初始化为主题图标
        JLabel themeIcon = new JLabel("☀️");
        themeIcon.setFont(new Font("SansSerif", Font.PLAIN, 16));
        themeIcon.setHorizontalAlignment(JLabel.CENTER);
        themeIcon.setVerticalAlignment(JLabel.CENTER);
        themeButton.add(themeIcon);

        // 添加主题切换功能
        themeButton.addActionListener(e -> {
            isDarkMode = !isDarkMode;
            if (isDarkMode) {
                // 切换到暗黑模式
                themeIcon.setText("🌙");
                mainFrame.setDarkMode(true);
            } else {
                // 切换到白天模式
                themeIcon.setText("☀️");
                mainFrame.setDarkMode(false);
            }
        });

        actionPanel.add(themeButton);

        // 创建用户按钮
        JButton userButton = new JButton();
        userButton.setPreferredSize(new Dimension(30, 30));
        userButton.setContentAreaFilled(false);
        userButton.setBorder(BorderFactory.createEmptyBorder());
        userButton.setFocusPainted(false);

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userIcon.setHorizontalAlignment(JLabel.CENTER);
        userIcon.setVerticalAlignment(JLabel.CENTER);
        userButton.add(userIcon);
        actionPanel.add(userButton);

        // 创建用户菜单
        JPopupMenu userMenu = new JPopupMenu();

        // 个人设置菜单项
        JMenuItem personalSettings = new JMenuItem("个人设置");
        personalSettings.setFont(new Font("SansSerif", Font.PLAIN, 14));
        personalSettings.addActionListener(e -> {
            // 处理个人设置点击事件
            JOptionPane.showMessageDialog(this, "个人设置功能即将上线", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
        userMenu.add(personalSettings);

        // 连接设置菜单项
        JMenuItem connectionSettings = new JMenuItem("连接设置");
        connectionSettings.setFont(new Font("SansSerif", Font.PLAIN, 14));
        connectionSettings.addActionListener(e -> {
            // 处理连接设置点击事件
            JOptionPane.showMessageDialog(this, "连接设置功能即将上线", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
        userMenu.add(connectionSettings);

        // 退出登录菜单项
        JMenuItem logout = new JMenuItem("退出登录");
        logout.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logout.setForeground(Color.RED); // 设置文字为红色
        logout.addActionListener(e -> {
            // 处理退出登录点击事件
            int confirm = JOptionPane.showConfirmDialog(this, "确定要退出登录吗?", "确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // 创建并显示登录界面
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                // 关闭当前主界面
                mainFrame.dispose();
            }
        });
        userMenu.add(logout);

        // 为用户按钮添加鼠标监听器，显示菜单
        userButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                userMenu.show(userButton, e.getX(), e.getY());
            }
        });

        return actionPanel;
    }
}
