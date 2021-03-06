package Main_window.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: 李子麟
 * @date: 2021/3/26 19:58
 * 要写回
 **/
public class Friend_list_data implements Serializable
{
    public List<Tree_data> data;
    public Friend_list_data()
    {
        data = new CopyOnWriteArrayList<>();
        add_tree("我的好友");
        add_tree("我的群聊");
    }

    public void add_friend(String tree_name, int id)
    {
        for(Tree_data data1 : data)
        {
            if(data1.getName().equals(tree_name))
            {

                data1.add_friend(id);
                return;
            }
        }
    }

    public void add_tree(String tree_name)
    {
        data.add(new Tree_data(tree_name));
    }

    public List<Tree_data> get_tree_data()
    {
        return data;
    }
}
