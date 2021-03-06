package Main_window.Pop_window;

import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author: 李子麟
 * @date: 2021/3/25 7:49
 **/
public class Create_group_window extends Pop_window
{
    private static final String tip_text = "群介绍信息";
    public Create_group_window(JFrame parent)
    {
        super(parent);
        JPanel root_panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        root_panel.setLayout(layout);
        JLabel label_name = new JLabel("群聊名");
        JTextField textField = new JTextField(15);
        JTextArea textArea = new JTextArea(10, 15);//
        textArea.setText(tip_text);
        textArea.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if(textArea.getText().equals(tip_text))
                {
                    textArea.setText("");
                }
            }
        });
        root_panel.add(label_name, constraints);
        constraints.gridwidth = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 5, 0, 0);
        constraints.weightx = 1;
        root_panel.add(textField, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);
        root_panel.add(textArea, constraints);
        JButton button = new JButton("确认");
        root_panel.add(button, constraints);
        button.addActionListener(actionEvent -> {
            Send_data data = new Send_data();
            data.data_type = Send_data.Data_type.Create_group_message;
            data.searched_user = textField.getText();
            data.data = new message_rightdata(null, textArea.getText(), Main.main_user.name);
            Main.main_user.send_message(data);
        });
        add(root_panel);
        pack();

    }
}
