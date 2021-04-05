package Main_window.User_Server;

import Main_window.Component.File_send_panel;
import Main_window.Data.File_panel_data;
import Main_window.Data.Message_data;
import Main_window.Data.Tree_data;
import Main_window.Separate_panel.Scroll_panel;
import Server.Data.File_info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/20 21:34
 **/
public class User_friend implements Serializable
{
    private int id;
    private String name;
    public Message_data communicate_data;
    public boolean is_user_in_list;
    public String nick;


    public User_friend()
    {
        communicate_data = new Message_data();
    }

    public User_friend(int id, String name)
    {
        this.id = id;
        this.name = name;
        communicate_data= new Message_data();

    }

    public int getId()
    {
        return id;
    }

    public String getName() {return name;}

    public void set_file_finished(File_info file_info)
    {
        File_panel_data file = communicate_data.find_data(file_info.time);
        file.is_finished = true;
        file.file_info.file_name = file_info.file_name;
        file.file_info.total_path = file_info.total_path;
        file.file_info.file_len = file_info.file_len;
        if(Scroll_panel.select_button.id == this.id)
        {
            communicate_data.find_file(file.file_info.time).set_finished();
        }
    }


}
