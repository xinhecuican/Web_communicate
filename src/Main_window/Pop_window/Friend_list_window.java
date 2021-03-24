package Main_window.Pop_window;

import Main_window.Listener.Show_card_listener;
import Main_window.Main;
import Main_window.Separate_panel.Left_panel;
import Main_window.User_Server.User_friend;
import Main_window.Window;

import javax.swing.*;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/21 19:09
 **/
public class Friend_list_window extends Pop_window
{
    public Friend_list_window(JFrame frame)
    {
        super(frame);
        Left_panel root_panel = new Left_panel();
        add(root_panel);
        List<User_friend> all_friends = Main.main_user.get_all_friends();
        for(User_friend friend : all_friends)
        {
            root_panel.add_card(friend.getId(), friend.name, friend.communicate_data, new Show_card_listener());
        }
    }
}
