package Main_window.Component;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.http.WebSocket;
import java.util.zip.GZIPInputStream;

/**
 * @author: 李子麟
 * @date: 2021/3/25 14:42
 **/
public class Base_confirm_card extends JPanel
{
    private JLabel label_left_up;
    private JLabel label_up;
    private JLabel label_right_down;
    private JButton button_confirm;
    public Base_confirm_card()
    {
        super();
    }

    public Base_confirm_card(String left_up, String string_up, String right_down, ActionListener listener)
    {
        label_left_up = new JLabel(left_up);
        label_right_down = new JLabel(right_down);
        label_up = new JLabel(string_up);
        button_confirm = new JButton("确认");
        button_confirm.addActionListener(listener);
        set_style();
    }

    public void set_style()
    {
        GridBagLayout layout = new GridBagLayout();
        //setLayout(layout);
        setPreferredSize(new Dimension(200, 50));
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel left_panel = new JPanel();
        left_panel.setLayout(layout);
        constraints.insets = new Insets(5, 0, 5, 0);
        left_panel.add(label_left_up ,constraints);
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JTextField textField = new JTextField();
        textField.setOpaque(false);
        textField.setEditable(false);
        textField.setBorder(null);
        left_panel.add(textField, constraints);
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        //constraints.insets.left = 10;
        constraints.gridwidth = 0;
        left_panel.add(label_up, constraints);
        left_panel.add(label_right_down, constraints);
        add(left_panel);
        add(button_confirm);
    }
}