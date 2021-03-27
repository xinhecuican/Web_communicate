package Server;

import Main_window.Data.Login_data;
import Main_window.Data.Send_data;
import Main_window.Tools;
import Server.Data.All_users;
import Server.Data.Group_message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Main_window.User_Server.User.file_root_path;

/**
 * @author: 李子麟
 * @date: 2021/3/20 7:27
 **/
public class Server_main
{
    private static All_users all_users;
    public static User_message heart_test_user;

    public static int create_group(Send_data data)
    {
        return all_users.create_group(data);
    }

    /**
     *
     * @param data
     * @return 返回>10000表示id
     */
    public static int Login(Login_data data)
    {
        if(data.is_regesiter)
        {
            int id = register_new_user(data);
            return id;
        }
        User_message user;
        if((user = search_user(data.id)) != null && user.compare(get_password(data.password)) && !user.is_online)
        {
            user.is_online = true;
            user.host = data.host;
            user.port = data.port;
            if(!data.name.equals(""))
            {
                user.user_name = data.name;
            }
            return 0;
        }
        return -1;
    }

    private static int register_new_user(Login_data data)
    {
        int id;
        synchronized(Server_main.class)
        {
            id = all_users.create_user(data);
        }
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

    public static User_message search_user(int id)
    {
        int low = 0;
        int high = all_users.all_users.size() - 1;
        while(low <= high)
        {
            int mid = (low + high) / 2;
            int compare_id = all_users.all_users.get(mid).get_id();
            if(compare_id == id)
            {
                return all_users.all_users.get(mid);
            }
            else if(compare_id < id)
            {
                low = mid + 1;
            }
            else
            {
                high = mid - 1;
            }
        }
        return null;
    }

    public static Group_message search_group(int id)
    {
        int low = 0;
        int high = all_users.all_groups.size() - 1;
        while(low <= high)
        {
            int mid = (low + high) / 2;
            int compare_id = all_users.all_groups.get(mid).getGroup_id();
            if(compare_id == id)
            {
                return all_users.all_groups.get(mid);
            }
            else if(compare_id < id)
            {
                low = mid + 1;
            }
            else
            {
                high = mid - 1;
            }
        }
        return null;
    }

    public static List<User_message> search_user(String name)
    {
        List<User_message> messages = new ArrayList<User_message>();
        if(!Tools.isNumeric(name))
        {
            for (User_message message : all_users.all_users)
            {
                if(message.user_name.equals(name))
                {
                    messages.add(message);
                }
            }
        }
        else
        {
            for(User_message message : all_users.all_users)
            {
                if(message.user_name.equals(name) || message.get_id() == Integer.parseInt(name))
                {
                    messages.add(message);
                }
            }
        }
        return messages;
    }

    public static List<Group_message> search_group(String name)
    {
        List<Group_message> group_messages = new ArrayList<>();
        if(!Tools.isNumeric(name))
        {
            for (Group_message message : all_users.all_groups)
            {
                if(message.group_name.equals(name))
                {
                    group_messages.add(message);
                }
            }
        }
        else
        {
            for(Group_message message : all_users.all_groups)
            {
                if(message.group_name.equals(name) || message.getGroup_id() == Integer.parseInt(name))
                {
                    group_messages.add(message);
                }
            }
        }
        return group_messages;
    }

    /**
     * 如果没登录则记录信息，如果登录则发送
     * @param id
     * @param data
     * @return 未找到该用户为假
     */
    public static boolean add_message(int id, Send_data data)
    {
        User_message user;
        if((user = search_user(id)) == null)
        {
            return false;
        }
        if(!user.is_online)
        {
            user.data.add(data);
        }
        else
        {
            try
            {
                Socket socket = new Socket(user.host, user.port);
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outputStream);
                out.writeObject(data);
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean add_group_message(int id, Send_data data)
    {
        Group_message message;
        if((message = search_group(id)) == null)
        {
            return false;
        }
        for(Integer user_id : message.group_users)
        {
            if(user_id != data.my_id)
            {
                add_message(user_id, data);
            }
        }
        return true;
    }

    public static boolean join_group(int id, Send_data data)
    {
        Group_message group;
        if((group = search_group(id)) == null)
        {
            return false;
        }
        binary_add(data.my_id, group.group_users);
        return true;
    }

    /**
     *
     * @param my_id
     * @param friend_id
     * @return 返回朋友名字
     */
    public static String add_friend(int my_id, int friend_id)
    {
        User_message message = search_user(friend_id);
        assert message != null;
        binary_add(my_id, message.friend_id);
        binary_add(friend_id, Objects.requireNonNull(search_user(my_id)).friend_id);
        //message.friend_id.add(my_id);
        //Objects.requireNonNull(search_user(my_id)).friend_id.add(friend_id);
        return message.user_name;
    }

    /**
     * 二分插入
     * @param insert_id: 要插入的id号
     * @param list： 要插入的list
     */
    public static void binary_add(int insert_id, List<Integer> list)
    {
        int low = 0, high = list.size() - 1;
        while(low <= high)
        {
            int mid = (low + high) / 2;
            int compare_id = list.get(mid);
            if(compare_id > insert_id)
            {
                high = mid - 1;
            }
            else
            {
                low = mid + 1;
            }
        }
        list.add(high+1, insert_id);
    }

    public static void main(String[] args)
    {
        all_users = new All_users();
        File file = new File(file_root_path + "Server Data");
        if(file.exists())
        {
            ObjectInputStream input = null;
            try
            {
                input = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
                all_users = (All_users) input.readObject();
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }

        }

        Runtime.getRuntime().addShutdownHook(new Thread(()->{//钩子，程序结束前调用
            //将数据写回
            File file2 = new File("User Data");
            file2.mkdir();
            File data_file = new File(file_root_path + "Server Data");
            FileOutputStream fileOutputStream = null;
            try
            {
                fileOutputStream = new FileOutputStream(data_file);
                ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
                outputStream.writeObject(all_users);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }));
        for(int i=0; i<3; i++)
        {
            Server server = new Server(i);
            server.start();
        }
    }
}
