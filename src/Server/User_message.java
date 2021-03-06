package Server;

import Main_window.Data.Send_data;
import Server.Data.File_info;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: 李子麟
 * @date: 2021/3/20 7:09
 **/
public class User_message implements java.io.Serializable
{
    private int user_id;
    public String user_name;
    public boolean is_online;
    public String host;
    public int port;
    protected List<Integer> friend_id;//线程安全
    public List<Send_data> data;//线程安全
    public List<File_info> files;
    private String password;
    /**
     *  = 1正常
     *  = 0 清空状态，如果下一次检测时还是0就会把连接移除
     */
    public boolean heart_beat_test;
    public User_message(int id, String name, String password, boolean is_online, String host, int port)
    {
        user_id = id;
        user_name = name;
        this.is_online = is_online;
        this.host = host;
        this.port = port;
        this.password = password;
        friend_id = new CopyOnWriteArrayList<Integer>();
        List<Send_data> temp_list = new ArrayList<Send_data>();
        data = Collections.synchronizedList(temp_list);
        files = new CopyOnWriteArrayList<>();
        heart_beat_test = false;
    }

    public int get_id()
    {
        return user_id;
    }

    public boolean compare(String compare_password)
    {
        return password.equals(compare_password);
    }

}
