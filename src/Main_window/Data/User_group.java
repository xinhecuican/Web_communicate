package Main_window.Data;

import Main_window.Component.File_send_panel;
import Main_window.Separate_panel.Scroll_panel;
import Main_window.User_Server.User_friend;
import Server.Data.File_info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/26 21:04
 **/
public class User_group implements Serializable
{
    private List<User_friend> group_friends;
    private int group_id;
    private String group_name;
    public Message_data data;
    public boolean is_group_in_list;
    private int message_sum;

    public User_group()
    {
        data = new Message_data();
        group_friends = new ArrayList<>();
    }

    public User_group(int id, String name)
    {
        group_id = id;
        group_name = name;
        group_friends = new ArrayList<>();
        data = new Message_data();
    }

    public int getGroup_id()
    {
        return group_id;
    }
    public String getGroup_name() { return group_name;}
    public void message_sum_clear()
    {
        message_sum = 0;
    }

    public synchronized void message_sum_add()
    {
        message_sum++;
    }

    public int get_message_sum()
    {
        return message_sum;
    }

    public void set_file_finished(File_info file_info)
    {
        File_panel_data file = data.find_data(file_info.time);
        file.is_finished = true;
        file.file_info.file_name = file_info.file_name;
        file.file_info.total_path = file_info.total_path;
        file.file_info.file_len = file_info.file_len;
        file.file_info.my_id = file_info.my_id;
        file.file_info.send_to_id = file_info.send_to_id;
        if(Scroll_panel.select_button.id == group_id)
        {
            data.find_file(file.file_info.time).set_finished();
        }
    }
}
