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
 * 在添加朋友列表中，点击确认后发送添加朋友信息
 **/
public class Confirm_add_friend_listener implements ActionListener
{
    private String name;
    private String id;
    private boolean is_group;
    public Confirm_add_friend_listener(String name, String id, boolean is_group)
    {
        this.name = name;
        this.id = id;
        this.is_group = is_group;
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        int result = 1;
        if(!is_group)
        {
            result = JOptionPane.showConfirmDialog(null,
                    "确认要添加" + name + "(id为" + id + ")为好友吗", "确认",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        else
        {
            result = JOptionPane.showConfirmDialog(null,
                    "确认要加入" + name + "吗?", "确认", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
        }
        if(result == 0)//是
        {
            if(Main.main_user.find_friend(Integer.parseInt(id)) != null)
            {
                JOptionPane.showMessageDialog(null,
                        "你已经添加过该" + (is_group ? "群" : "好友"), "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Send_data data = new Send_data();
            data.send_to_id = Integer.parseInt(id);
            data.data_type = !is_group ? Send_data.Data_type.Request_add_friend : Send_data.Data_type.Request_add_group;
            data.searched_user = Main.main_user.name;
            Main.main_user.send_message(data);
        }
    }
}
