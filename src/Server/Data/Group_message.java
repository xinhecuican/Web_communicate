package Server.Data;

import Server.Server;

import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: 李子麟
 * @date: 2021/3/20 7:09
 **/
public class Group_message implements Serializable
{
    public String group_name;
    private int group_id;
    public List<Integer> group_users;
    public int group_admin_id;
    public String introduction_message;
    public List<File_info> files;
    public Group_message(int group_id, String group_name, int group_admin_id, String introduction_message)
    {
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_admin_id = group_admin_id;
        this.introduction_message = introduction_message;
        group_users = new ArrayList<Integer>();
        files = new CopyOnWriteArrayList<>();
    }

    public int getGroup_id()
    {
        return group_id;
    }
}
