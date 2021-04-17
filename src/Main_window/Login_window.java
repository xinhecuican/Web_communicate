package Main_window;

import Common.ImgUtils;
import Main_window.Data.Login_data;
import Main_window.Pop_window.Register_window;
import Main_window.User_Server.User;
import Main_window.User_Server.User_server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: 李子麟
 * @date: 2021/3/20 9:48
 **/
public class Login_window extends JFrame
{
    private JPanel root_panel;
    private GridBagLayout root_layout;
    private JTextField message;
    private JTextField user_text;
    private JPasswordField password_text;
    public static Login_window current;
    public Login_window(String name)
    {
        super(name);
        current = this;
        root_panel = new JPanel();
        root_layout = new GridBagLayout();
        root_panel.setLayout(root_layout);
        add(root_panel);
        setIconImage(ImgUtils.getIcon("favicon.png").getImage());

        JLabel label_user = new JLabel("账号");
        JLabel label_password = new JLabel("密码");
        message = new JTextField();
        message.setEditable(false);
        message.setOpaque(false);

        user_text = new JTextField(15);
        password_text = new JPasswordField(15);

        JButton button_confirm = new JButton("登录");
        JButton button_register = new JButton("注册");

        JPanel panel1 = new JPanel();
        panel1.add(label_user);
        panel1.add(user_text);
        JPanel panel2 = new JPanel();
        panel2.add(label_password);
        panel2.add(password_text);
        JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel3.add(button_register);
        panel3.add(button_confirm);

        Box vBox = Box.createVerticalBox();
        vBox.add(panel1);
        vBox.add(panel2);
        vBox.add(panel3);
        vBox.add(message);
        setContentPane(vBox);
        button_confirm.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                Login_data data = User.get_login_data("", Integer.parseInt(user_text.getText())
                        , String.valueOf(password_text.getPassword()), Login_data.Login_type.Login);
                Main.main_user.send_login_message(data);
            }
        });

        button_register.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                new Register_window(current);
            }
        });
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void set_text(String text)
    {
        message.setText(text);
    }
}
