package Main_window.User_Server;

import Main_window.Data.Message_data;
import Main_window.Data.Tree_data;

import java.io.Serializable;

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

}
