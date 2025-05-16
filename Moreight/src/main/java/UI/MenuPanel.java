package UI;

import Database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Action;

public class MenuPanel extends JPanel {

    private final DBMSMainFrame mainFrame;

    public MenuPanel(DBMSMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // 设置布局为 FlowLayout，组件从左到右排列
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBackground(Color.WHITE);

        // 添加数据库选择下拉框
        JLabel dbLabel = new JLabel("选择数据库:");
        dbLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(dbLabel);
        List databases= DatabaseManager.listDatabases();
        //DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(databases.toArray(new String[0]));
        //JComboBox<String> dbComboBox = new JComboBox<>(model);

        JComboBox<String> dbComboBox = new JComboBox<>(new String[]{"mysql", "test_db"});
        dbComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(dbComboBox);

        // 添加水平间隔
        add(Box.createHorizontalStrut(20));

        // 添加新建查询按钮
        JButton newQueryButton = createButton("新建查询", Color.decode("#4285F4"));
        add(newQueryButton);

        newQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.createNewQueryTab();
            }
        });

        // 添加水平间隔
        add(Box.createHorizontalStrut(10));

        // 添加执行按钮
        JButton executeButton = createButton("执行", Color.decode("#34A853"));
        add(executeButton);

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mainFrame.executeSelectedQuery();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


    }

    // 创建带有图标的按钮
    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        return button;
    }
}