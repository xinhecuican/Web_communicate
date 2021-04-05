package Server.Data;

import Main_window.Data.Login_data;
import Main_window.Data.Send_data;
import Server.Server_main;
import Server.User_message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/22 20:41
 **/
public class All_users implements Serializable
{
    public List<User_message> all_users;
    public List<Group_message> all_groups;
    private int max_number;
    public All_users()
    {
        all_users = new ArrayList<User_message>();
        all_groups = new ArrayList<>();
        max_number = 10000;
    }

    public synchronized int create_user(Login_data data)
    {
        int id = max_number + 1;
        max_number += 1;
        String s = get_password(data.password);
        all_users.add(new User_message(id, data.name, s,false, data.host, data.port));
        return id;
    }

    public synchronized int create_group(Send_data data)
    {
        int id = max_number + 1;
        max_number += 1;
        Group_message message = new Group_message(id, data.searched_user, data.my_id, data.data.message);
        message.group_users.add(data.my_id);
        all_groups.add(message);
        return id;
    }

    private static String get_password(String MD5)
    {
        char[] a = MD5.toCharArray();
        for (int i = 0; i < a.length; i++)
        {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;
    }

    public int getMax_number()
    {
        return max_number - 10000;
    }
}
