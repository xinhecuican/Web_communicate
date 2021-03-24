package Server;

import Main_window.Data.Login_data;
import Main_window.Data.Send_data;
import Server.Data.Login_back_data;
import Server.Data.Search_back_data;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: 李子麟
 * @date: 2021/3/19 21:46
 **/
public class Server_handle_thread extends Thread
{
    private Socket socket;
    private int mode;
    public Server_handle_thread(Socket socket, int mode)
    {
        this.socket = socket;
        this.mode = mode;
    }

    public void run()
    {
        ObjectInputStream input = null;
        try
        {
            InputStream inputStream = socket.getInputStream();
            input = new ObjectInputStream(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        switch (mode)
        {
            case 0://登录信息
                try
                {
                    assert input != null;
                    Login_data data = (Login_data)input.readObject();
                    if(data.id == 10)//申请获得好友信息
                    {
                        //返回id为0，并且is_error = true
                        int user_id = Integer.parseInt(data.name);
                        User_message message = Server_main.search_user(user_id);
                        assert message != null;
                        Login_back_data back_data = new Login_back_data(true, new ArrayList<Send_data>());
                        back_data.id = 10;
                        for(Integer integer : message.friend_id)
                        {
                            Send_data send_data = new Send_data();
                            send_data.send_to_id = integer;
                            send_data.searched_user = Objects.requireNonNull(Server_main.search_user(integer)).user_name;
                            back_data.storage_data.add(send_data);
                        }
                        ObjectOutputStream out = decorate_stream(socket.getOutputStream());
                        out.writeObject(back_data);
                        out.flush();
                        break;
                    }
                    else if(data.id == 11)//下线
                    {
                        int user_id = Integer.parseInt(data.name);
                        Objects.requireNonNull(Server_main.search_user(user_id)).is_online = false;
                        break;
                    }
                    int id = Server_main.Login(data);//包含了注册的情况，如果是注册，返回注册id
                    ObjectOutputStream out = decorate_stream(socket.getOutputStream());
                    if(id == -1) //出错
                    {
                        Login_back_data back_data = new  Login_back_data(true, null);
                        back_data.id = 1;
                        out.writeObject(back_data);
                        out.flush();
                    }
                    else if(id == 0)//正常登录
                    {
                        User_message user_message = Server_main.search_user(data.id);
                        Login_back_data login_back_data = new Login_back_data(false, user_message.data);
                        login_back_data.name = user_message.user_name;
                        out.writeObject(login_back_data);
                        out.flush();
                    }
                    else//注册
                    {

                        Login_back_data login_back_data = new Login_back_data(false, null);
                        login_back_data.id = id;
                        out.writeObject(login_back_data);
                        out.flush();
                    }
                    break;
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            case 1: //单人发送信息
                try
                {
                    assert input != null;
                    Send_data data = (Send_data)input.readObject();
                    if(data.send_to_id > 10000)//正常消息
                    {

                        Server_main.add_message(data.send_to_id, data);
                    }
                    else
                    {
                        if (data.send_to_id == 1)//搜索好友
                        {
                            List<User_message> send_data = Server_main.search_user(data.searched_user);
                            if (send_data.size() > 0)
                            {
                                Search_back_data search_back_data = new Search_back_data();
                                for (User_message message : send_data)
                                {
                                    search_back_data.add(message.get_id(), message.user_name);
                                }
                                ObjectOutputStream out = decorate_stream(socket.getOutputStream());
                                out.writeObject(search_back_data);
                            }
                            else//没有搜索到
                            {
                                User_message message = Server_main.search_user(data.my_id);
                                Socket socket = new Socket(message.host, message.port);
                                data.my_id = 1;
                                ObjectOutputStream out = decorate_stream(socket.getOutputStream());
                                out.writeObject(data);
                            }
                        }
                        else if (data.send_to_id == 2)//请求添加好友，好友id在data.searched_user字段中
                        {
                            int friend_id = Integer.parseInt(data.searched_user);
                            User_message message = Objects.requireNonNull(Server_main.search_user(data.my_id));
                            data.searched_user = message.user_name;
                            if (!Server_main.add_message(friend_id, data))//处理未找到该用户的情况，找到了直接在函数中发送
                            {
                                data.my_id = 2;
                                data.searched_user = Objects.requireNonNull(Server_main.search_user(friend_id)).user_name;
                                Socket socket = new Socket(message.host, message.port);
                                ObjectOutputStream out = decorate_stream(socket.getOutputStream());
                                out.writeObject(data);
                                socket.close();
                            }
                        }
                        else if (data.send_to_id == 3)//确认添加好友,其中searched_user是请求的id，my_id是目的id
                        {
                            int sender_id = Integer.parseInt(data.searched_user);
                            String friend_name = Server_main.add_friend(sender_id, data.my_id);
                            data.searched_user = friend_name;
                            /*User_message user_message = Server_main.search_user(sender_id);
                            assert user_message != null;
                            Socket socket = new Socket(user_message.host, user_message.port);
                            OutputStream outputStream = socket.getOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(outputStream);
                            out.writeObject(data);*/
                            Server_main.add_message(sender_id, data);
                            socket.close();
                        }
                    }
                    break;
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
        }
        try
        {
            socket.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static ObjectOutputStream decorate_stream(OutputStream outputStream)
    {
        try
        {
            ObjectOutputStream outputStream1 = new ObjectOutputStream(outputStream);
            outputStream1.flush();
            return outputStream1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
