package Main_window.Pop_window;

import Main_window.Data.Message_data;
import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Listener.Confirm_add_friend_listener;
import Main_window.Main;
import Main_window.Separate_panel.Left_panel;
import Server.Data.Search_back_data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author: 李子麟
 * @date: 2021/3/20 20:04
 **/
public class Add_friend_window extends Pop_window
{
    public static Add_friend_window current;
    JPanel root_panel;
    private static final String hint_text = "输入用户名或Id添加好友";
    private Left_panel add_friend_panel;
    private Left_panel friend_confirm_panel;
    public Add_friend_window(JFrame parent)
    {
        super(parent);
        current = this;
        root_panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        root_panel.setLayout(layout);
        //constraints.fill = GridBagConstraints.HORIZONTAL;
        //constraints.weightx = 1;
        JTextField search_field = new JTextField();
        search_field.setText(hint_text);
        search_field.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if(search_field.getText().equals(hint_text))
                {
                    search_field.setText("");
                }
            }
        });
        //layout.setConstraints(search_field, constraints);
        JButton button_confirm = new JButton("确认");
        button_confirm.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                String search_text = search_field.getText();
                Send_data data = new Send_data();
                data.send_to_id = 1;
                data.searched_user = search_text;
                Main.main_user.send_message(data);
            }
        });

        /*constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.gridwidth = 0;
        layout.setConstraints(button_confirm, constraints);*/
        JTabbedPane tab_panel = new JTabbedPane();
        add_friend_panel = new Left_panel();
        add_friend_panel.add_search_panel(search_field, button_confirm);
        tab_panel.addTab("添加好友", tab_panel);
        friend_confirm_panel = new Left_panel();
        tab_panel.addTab("验证消息", friend_confirm_panel);
    }

    public void add_friend_card(Search_back_data data)
    {
        for(int i=0; i<data.id.size(); i++)
        {
            add_friend_panel.add_card(false, data.name.get(i),
                    new Message_data(new message_rightdata("", String.valueOf(data.id.get(i)), false)),
                    new Confirm_add_friend_listener(data.name.get(i), String.valueOf(data.id.get(i))));
        }
    }

    public void add_confirm_message()
    {
        friend_confirm_panel.add_card();
    }

    public void load_friend_confirm_message()
    {

    }
}