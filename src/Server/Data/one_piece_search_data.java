package Server.Data;

import Server.User_message;

import java.io.Serializable;

/**
 * @author: 李子麟
 * @date: 2021/3/25 14:36
 **/
public class one_piece_search_data implements Serializable
{
    public int id;
    public String name;
    public boolean is_group;

    public one_piece_search_data(int id, String name, boolean is_group)
    {
        this.is_group = is_group;
        this.id = id;
        this.name = name;
    }

    public one_piece_search_data(User_message message)
    {
        id = message.get_id();
        name = message.user_name;
        is_group = false;
    }
}
