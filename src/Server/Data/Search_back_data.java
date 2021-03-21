package Server.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/20 21:10
 **/
public class Search_back_data implements Serializable
{
    public List<Integer> id;
    public List<String> name;
    public Search_back_data()
    {
        id = new ArrayList<Integer>();
        name = new ArrayList<String>();
    }

    public void add(int id, String name)
    {
        this.id.add(id);
        this.name.add(name);
    }
}