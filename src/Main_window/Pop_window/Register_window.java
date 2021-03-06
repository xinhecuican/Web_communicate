package Main_window.Pop_window;

import Main_window.Data.Login_data;
import Main_window.Main;
import Main_window.User_Server.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: 李子麟
 * @date: 2021/3/21 12:03
 **/
public class Register_window extends Pop_window
{
    private JTextField message_field;
    public Register_window(JFrame parent)
    {
        super(parent);

        JLabel label_user_name = new JLabel("用户名");
        JTextField text_user_name = new JTextField(15);
        JLabel label_password = new JLabel("密码");
        JPasswordField passwordField = new JPasswordField(15);
        JLabel label_confirm = new JLabel("确认密码");
        JPasswordField password_confirm = new JPasswordField(15);
        JPanel panel_button = new JPanel();
        JButton button = new JButton("确认");
        panel_button.add(button);
        message_field = new JTextField();
        message_field.setEditable(false);
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                String password = String.valueOf(passwordField.getPassword());
                String confirm = String.valueOf(password_confirm.getPassword());
                if(!password.equals(confirm))
                {
                    message_field.setText("密码不一致");
                    password_confirm.setText("");
                    passwordField.setText("");
                }
                else if(password.length() < 5)
                {
                    message_field.setText("密码太短");
                    password_confirm.setText("");
                    passwordField.setText("");
                }
                else
                {
                    String user_name = text_user_name.getText();
                    Login_data data = User.get_login_data(user_name, 0, password, Login_data.Login_type.Register);
                    Main.main_user.send_login_message(data);
                    dispose();
                }
            }
        });
        JPanel left_panel = new JPanel();
        left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.Y_AXIS));
        left_panel.add(label_user_name);
        left_panel.add(label_password);
        left_panel.add(label_confirm);

        JPanel right_panel = new JPanel();
        right_panel.setLayout(new BoxLayout(right_panel, BoxLayout.Y_AXIS));
        right_panel.add(text_user_name);
        right_panel.add(passwordField);
        right_panel.add(password_confirm);

        JPanel root_panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        root_panel.setLayout(layout);
        root_panel.add(left_panel, constraints);
        constraints.insets = new Insets(0, 2, 0, 0);
        constraints.gridwidth = 0;
        root_panel.add(right_panel, constraints);
        root_panel.add(panel_button, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        root_panel.add(message_field, constraints);
        add(root_panel);
        pack();
    }
}
