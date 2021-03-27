package Main_window.Pop_window;

import Main_window.Component.Base_confirm_card;
import Main_window.Data.Friend_confirm_data;
import Main_window.Data.Message_data;
import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Listener.Confirm_add_friend_listener;
import Main_window.Main;
import Main_window.Component.Friend_confirm_card;
import Main_window.Separate_panel.Scroll_panel;
import Server.Data.Search_back_data;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

/**
 * @author: 李子麟
 * @date: 2021/3/20 20:04
 **/
public class Add_friend_window extends Pop_window
{
    public static Add_friend_window current;
    JPanel root_panel;
    private static final String hint_text = "输入用户名或Id添加好友";
    private Scroll_panel add_friend_panel;
    private Scroll_panel friend_confirm_panel;
    private JTextField textField;
    private JTabbedPane tab_panel;
    public Add_friend_window(JFrame parent)
    {
        super(parent);
        current = this;
        root_panel = new JPanel();
        //GridBagConstraints constraints = new GridBagConstraints();
        root_panel.setLayout(new BorderLayout());
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
                data.data_type = Send_data.Data_type.Search_friend;
                data.searched_user = search_text;
                Main.main_user.send_message(data);
            }
        });

        /*constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.gridwidth = 0;
        layout.setConstraints(button_confirm, constraints);*/
        tab_panel = new JTabbedPane();
        add_friend_panel = new Scroll_panel();
        add_friend_panel.add_search_panel(search_field, button_confirm);
        tab_panel.addTab("添加好友", add_friend_panel);
        friend_confirm_panel = new Scroll_panel();
        tab_panel.addTab("验证消息", friend_confirm_panel);
        root_panel.add(tab_panel, BorderLayout.CENTER);
        textField = new JTextField();
        textField.setEditable(false);
        root_panel.add(textField, BorderLayout.SOUTH);
        add(root_panel);
        tab_panel.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent changeEvent)
            {
                if(tab_panel.getSelectedIndex() == 1)
                {
                    friend_confirm_panel.remove_all_cards();
                    ArrayList<Friend_confirm_data> dataList = Main.main_user.getConfirm_data();
                    for(int i=dataList.size()-1; i>=0; i--)
                    {
                        friend_confirm_panel.add_card(new Friend_confirm_card(dataList.get(i)));
                    }
                }
            }
        });
        setPreferredSize(new Dimension(600, 600));
        pack();
    }

    public synchronized void add_friend_card(Search_back_data data)
    {
        for(int i=0; i<data.data.size(); i++)
        {
            Base_confirm_card confirm_card = new Base_confirm_card(data.data.get(i).name,
                    data.data.get(i).is_group ? "群" : "好友", "id:" + String.valueOf(data.data.get(i).id),
                    new Confirm_add_friend_listener(data.data.get(i).name,
                            String.valueOf(data.data.get(i).id), data.data.get(i).is_group));
            add_friend_panel.add_component(confirm_card);
        }
        /*for(int i=0; i<data.id.size(); i++)
        {
            add_friend_panel.add_card(0, data.name.get(i),
                    new Message_data(new message_rightdata("", String.valueOf(data.id.get(i)), false)),
                    new Confirm_add_friend_listener(data.name.get(i), String.valueOf(data.id.get(i))));
        }*/
    }

    public synchronized void add_confirm_message(Friend_confirm_card card)
    {
        friend_confirm_panel.add_card(card);
    }

    public int get_select_panel_index()
    {
        return tab_panel.getSelectedIndex();
    }

    public void set_tooltip_message(String text)
    {
        textField.setText(text);
    }
}
