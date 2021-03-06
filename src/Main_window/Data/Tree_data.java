package Main_window.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/26 20:01
 **/
public class Tree_data implements Serializable
{
    private List<Integer> list_id;
    private String name;
    public Tree_data(String name)
    {
        this.name = name;
        list_id = new ArrayList<>();
    }

    public void add_friend(int id)
    {
        list_id.add(id);
    }

    public List<Integer> supply_tree()
    {
        return list_id;
    }

    public String getName()
    {
        return name;
    }
}
