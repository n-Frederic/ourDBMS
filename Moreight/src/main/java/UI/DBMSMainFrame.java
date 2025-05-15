package UI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;

import Controller.Operating;
import Util.Func.Render;



public class DBMSMainFrame extends JFrame {

    // 内容面板
    private final JPanel contentPanel = new JPanel();

    // 左侧按钮面板
    private final LeftPanelUI leftPanel = new LeftPanelUI(this);

    // 顶部信息栏
    private final JPanel topInfoPanel = new JPanel();

    // 顶部标签页
    private final JTabbedPane tabbedPane = new JTabbedPane();

    // 底部状态栏
    private final JPanel statusBar = new JPanel();

    // 当前活动的查询标签页
    private BlankQueryTab activeQueryTab;

    public boolean enterDB=false;

    public DBMSMainFrame() {
        // 设置窗口
        this.setLayout(new BorderLayout());
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Moreight-DBMS 系统 - 主界面");

        // 创建顶部信息栏
        createTopInfoPanel();

        // 创建左侧功能按钮面板
        createLeftPanel();

        // 创建顶部标签页
        createTabbedPane();

        // 创建内容面板
        createContentPane();

        // 创建底部状态栏
        createStatusBar();

        // 添加组件到主窗口
        this.add(topInfoPanel, BorderLayout.NORTH);
        this.add(leftPanel, BorderLayout.WEST);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(statusBar, BorderLayout.SOUTH);

        // 设置主页内容
        setWelcomeTab(); // 添加欢迎标签页
        setHomePageTab();

        // 确保主页存在
        ensureHomePageExists();

        // 添加标签页切换监听器
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateActiveQueryTab();
            }
        });

        // 设置窗口居中显示
        this.setLocationRelativeTo(null);
    }

    // 设置暗黑模式
    public void setDarkMode(boolean isDark) {
        if (isDark) {
            // 暗黑模式
            this.getContentPane().setBackground(Color.DARK_GRAY);
            topInfoPanel.setBackground(Color.DARK_GRAY);
            statusBar.setBackground(Color.GRAY);
            // 更多组件的颜色设置...
        } else {
            // 白天模式
            this.getContentPane().setBackground(Color.WHITE);
            topInfoPanel.setBackground(Color.WHITE);
            statusBar.setBackground(Color.LIGHT_GRAY);
            // 更多组件的颜色设置...
        }
        this.repaint();
    }

    // 创建顶部信息栏
    private void createTopInfoPanel() {
        topInfoPanel.setLayout(new BorderLayout());
        topInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建信息显示栏并添加到顶部信息栏
        InfoDisplayPanel infoDisplayPanel = new InfoDisplayPanel(this);
        topInfoPanel.add(infoDisplayPanel, BorderLayout.NORTH);

        topInfoPanel.setBackground(Color.WHITE);
    }

    // 创建左侧功能按钮面板
    private void createLeftPanel() {
        // 左侧功能按钮面板由 LeftPanelUI 类管理
    }

    // 创建顶部标签页
    private void createTabbedPane() {
        // 设置标签页样式
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.DARK_GRAY);
    }

    // 创建新的查询标签页
    public void createNewQueryTab() {
        // 检查是否已存在相同名称的标签页
        String tabTitle = generateUniqueTabTitle("<查询>");

        BlankQueryTab queryTab = new BlankQueryTab();
        tabbedPane.addTab(tabTitle, queryTab);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        activeQueryTab = queryTab;

        // 为新标签页添加关闭按钮
        addCloseButtonToTab(tabbedPane.getTabCount() - 1, tabTitle, queryTab);
    }

    // 生成唯一的标签页标题
    private String generateUniqueTabTitle(String baseTitle) {
        int index = 1;
        String tabTitle = baseTitle + " " + index;
        while (isTabTitleExists(tabTitle)) {
            index++;
            tabTitle = baseTitle + " " + index;
        }
        return tabTitle;
    }

    // 检查标签页标题是否存在
    private boolean isTabTitleExists(String tabTitle) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(tabTitle)) {
                return true;
            }
        }
        return false;
    }

    // 确保主页存在
    private void ensureHomePageExists() {
        boolean homeTabExists = false;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals("主页")) {
                homeTabExists = true;
                break;
            }
        }
        if (!homeTabExists) {
            setHomePageTab();
        }
    }

    // 为标签页添加关闭按钮
    private void addCloseButtonToTab(int tabIndex, String tabTitle, JPanel tabContent) {
        // 创建自定义标签组件
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tabPanel.setOpaque(false);

        // 标签页标题
        JLabel titleLabel = new JLabel(tabTitle);
        titleLabel.setFont(tabbedPane.getFont());

        // 关闭按钮
        JButton closeButton = new JButton("×");
        closeButton.setFont(closeButton.getFont().deriveFont(12f));
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setForeground(Color.RED);

        // 添加关闭按钮的事件监听器
        closeButton.addActionListener(e -> {
            tabbedPane.removeTabAt(tabIndex);
            // 如果关闭最后一个标签页，自动切换到主页
            if (tabbedPane.getTabCount() == 0) {
                setHomePageTab();
            }
        });

        // 将标题和关闭按钮添加到自定义标签组件中
        tabPanel.add(titleLabel);
        tabPanel.add(closeButton);

        // 设置自定义标签组件为标签页的标题组件
        tabbedPane.setTabComponentAt(tabIndex, tabPanel);
    }

    // 更新当前活动的查询标签页
    private void updateActiveQueryTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < tabbedPane.getTabCount()) {
            Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
            if (selectedComponent instanceof BlankQueryTab) {
                activeQueryTab = (BlankQueryTab) selectedComponent;
            } else {
                activeQueryTab = null;
            }
        } else {
            activeQueryTab = null;
        }
    }

    private void updateUserQueryTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < tabbedPane.getTabCount()) {
            Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
            if (selectedComponent instanceof BlankQueryTab) {
                activeQueryTab = (BlankQueryTab) selectedComponent;
            } else {
                activeQueryTab = null;
            }
        } else {
            activeQueryTab = null;
        }
    }

    // 执行选中的查询内容
    public void executeSelectedQuery() throws IOException {
        updateActiveQueryTab(); // 确保获取当前活动的查询标签页
        if (activeQueryTab != null) {
            String selectedText = activeQueryTab.getSelectedText();
            activeQueryTab.setQueryResult(" ");
            if (selectedText != null && !selectedText.isEmpty()) {

                // 处理选中的文本
                String result = handleSelectedQuery(selectedText);
                activeQueryTab.setQueryResult(" ");
                activeQueryTab.setQueryResult(result);
            } else {
                JOptionPane.showMessageDialog(this, "请选择要执行的内容", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "没有活动的标签页", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void executeUserQuery() throws IOException {
        updateActiveQueryTab(); // 确保获取当前活动的查询标签页
        if (activeQueryTab != null) {
            String selectedText = activeQueryTab.getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                activeQueryTab.setQueryResult(" ");
                // 处理选中的文本
                String result = handleSelectedQuery(selectedText);
                activeQueryTab.setQueryResult(" ");
                activeQueryTab.setQueryResult(result);
            } else {
                JOptionPane.showMessageDialog(this, "请选择要执行的内容", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "没有活动的标签页", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 处理选中的查询内容
    private String handleSelectedQuery(String query) throws IOException {

        // 在这里添加处理查询的逻辑
        String result=" ";
        Operating operating=new Operating();
        result=operating.isDB(query);
//
//        Operating operating=new Operating();
//        operating.logAndRegister(query);
//        result=result+Operating.str1;
//        enterDB=operating.enter_database;
//        if(enterDB){
//            operating.logAndRegister1(query);
//            result=result+Operating.str1;
//        }

        // 模拟查询结果
        return "结果：\n" + result + "\n";
    }

    // 创建内容面板
    private void createContentPane() {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
    }

    // 创建底部状态栏
    private void createStatusBar() {
        statusBar.setLayout(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setBackground(Color.LIGHT_GRAY);

        JLabel statusLabel = new JLabel("就绪");
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        statusBar.add(statusLabel, BorderLayout.CENTER);
    }

    // 设置欢迎标签页
    public void setWelcomeTab() {
        tabbedPane.addTab("欢迎", new WelcomeTab());
    }

    // 设置主页标签页
    public void setHomePageTab() {
        // 移除所有已有的标签页
        tabbedPane.removeAll();

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout());
        homePanel.setBackground(Color.WHITE);

        // 主页标题
        JLabel titleLabel = new JLabel("欢迎使用Moreight-DBMS 系统");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        homePanel.add(titleLabel, BorderLayout.NORTH);

        // 主页内容
        JLabel contentLabel = new JLabel("请选择左侧的功能按钮进行操作");
        contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentLabel.setHorizontalAlignment(JLabel.CENTER);
        contentLabel.setVerticalAlignment(JLabel.CENTER);
        homePanel.add(contentLabel, BorderLayout.CENTER);

        tabbedPane.addTab("主页", homePanel);
    }

    // 设置用户管理标签页
    public void setUserManagementTab() {
        tabbedPane.removeAll();
        tabbedPane.addTab("用户管理", new UserManagementTab());
    }

    public void setUserManagementDialog() {
        tabbedPane.removeAll();
        tabbedPane.addTab("用户管理", new UserManagementDialog());
    }


    // 设置数据库管理标签页
    public void setDatabaseManagementTab() {
        tabbedPane.removeAll();
        tabbedPane.addTab("数据库管理", new DatabaseManagementTab());
    }

    // 设置数据查询标签页
    public void setQueryTab() {
        tabbedPane.removeAll();
        tabbedPane.addTab("数据查询", new QueryTab());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 首先显示登录界面
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}