package Main_window;

import Main_window.Server.Server;
import Main_window.Server.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: 李子麟
 * @date: 2021/3/18 20:11
 **/
public class Pop_window extends JDialog
{
    public Pop_window(int x, int y)
    {
        String[] label_string = new String[]{"名称", "发送端口", "发送IP", "接收端口"};
        String[] text_string = new String[]{Main.main_user.get_name(), String.valueOf(User.send_port), User.send_host, String.valueOf(Server.receive_port)};
        JPanel root_panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        root_panel.setLayout(layout);
        for(int i=0; i<4; i++)
        {
            JLabel label = new JLabel(label_string[i]);
            JTextField textField = new JTextField(text_string[i]);
            JButton button = new JButton("确定");
            button.setToolTipText(label_string[i]);
            constraints.gridwidth = 1;
            constraints.gridheight = 10;
            root_panel.add(label, constraints);
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            root_panel.add(textField, constraints);
            constraints.weightx = 0;
            constraints.fill = GridBagConstraints.NONE;
            constraints.gridwidth = 0;
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent actionEvent)
                {
                    if(button.getToolTipText().equals("发送端口"))
                    {
                        User.send_port = Integer.parseInt(textField.getText());
                        Main.need_reset = true;
                    }
                    else if(button.getToolTipText().equals("发送IP"))
                    {
                        User.send_host = textField.getText();
                        Main.need_reset = true;
                    }
                    else
                    {
                        Main.main_user.set_name(textField.getText());
                    }
                }
            });
            if(i == 4)
            {
                textField.setEditable(false);
            }
            else
            {
                root_panel.add(button, constraints);
            }
        }

        add(root_panel);
        pack();
        setResizable(false);
        setVisible(true);
        setLocation(x, y);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


    }
}