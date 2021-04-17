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
        setOpaque(false);
        setLayout(new BorderLayout());
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel left_panel = new JPanel();
        left_panel.setLayout(layout);
        constraints.insets = new Insets(10, 0,10,5);
        left_panel.add(label_left_up ,constraints);
        left_panel.setOpaque(false);
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
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
        constraints.anchor = GridBagConstraints.WEST;
        left_panel.add(label_right_down, constraints);
        add(left_panel, BorderLayout.CENTER);
        JPanel right_panel = new JPanel();
        GridBagLayout layout1 = new GridBagLayout();
        right_panel.setLayout(layout1);
        GridBagConstraints constraints1 = new GridBagConstraints();
        constraints1.anchor = GridBagConstraints.CENTER;
        constraints1.weightx = 1;
        constraints1.weighty = 1;
        right_panel.add(button_confirm, constraints1);
        right_panel.setOpaque(false);
        add(right_panel, BorderLayout.EAST);
    }
}
