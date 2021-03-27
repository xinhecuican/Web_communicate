package Server.Data;

import Server.User_message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/20 21:10
 **/
public class Search_back_data implements Serializable
{
    public List<one_piece_search_data> data;
    public Search_back_data()
    {
        data = new ArrayList<one_piece_search_data>();
    }

    public void add(int id, String name, boolean is_group)
    {
        one_piece_search_data one_piece_search_data = new one_piece_search_data(id, name, is_group);
        data.add(one_piece_search_data);
    }

    public void add(User_message message)
    {
        data.add(new one_piece_search_data(message));
    }
}
