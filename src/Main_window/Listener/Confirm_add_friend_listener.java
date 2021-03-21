package Main_window.Listener;

import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: 李子麟
 * @date: 2021/3/21 8:51
 **/
public class Confirm_add_friend_listener implements ActionListener
{
    private String name;
    private String id;
    public Confirm_add_friend_listener(String name, String id)
    {
        this.name = name;
        this.id = id;
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        int result = JOptionPane.showConfirmDialog(null,
                "确认要添加" + name + "(id为" + id  + ")为好友吗", "确认",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if(result == 0)//是
        {
            Send_data data = new Send_data();
            data.send_to_id = 2;
            data.searched_user = id;
            Main.main_user.send_message(data);
        }
    }
}
