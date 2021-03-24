package Main_window.User_Server;
import Main_window.Data.Friend_confirm_data;
import Main_window.Data.Send_data;
import Main_window.Main;
import Main_window.Pop_window.Add_friend_window;
import Main_window.Component.Friend_confirm_card;

import java.io.*;
import java.net.Socket;

/**
 * @author: 李子麟
 * @date: 2021/3/18 12:32
 **/
public class User_Server_handle_thread extends Thread
{
    private Socket socket;
    public User_Server_handle_thread(Socket socket)
    {
        this.socket = socket;
    }
    @Override
    public void run()
    {
        super.run();
        Send_data input_data = new Send_data();
        try
        {
            InputStream stream = socket.getInputStream();
            ObjectInputStream inputStream = new ObjectInputStream(stream);
            input_data = (Send_data)inputStream.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(input_data.send_to_id == 2)//请求添加好友
        {
            Friend_confirm_card card;
            if (input_data.my_id == 2)//未搜索到好友的返回信息
            {
                card = new Friend_confirm_card(0, input_data.searched_user, 1);
                Main.main_user.add_confirmed_data(new Friend_confirm_data(0, input_data.searched_user, 1));
            }
            else//给好友的信息
            {
                card = new Friend_confirm_card(input_data.my_id, input_data.searched_user, 0);
                Main.main_user.add_confirmed_data(new Friend_confirm_data(input_data.my_id, input_data.searched_user, 0));
            }
            if(Add_friend_window.current != null)
            {
                Add_friend_window.current.add_confirm_message(card);
            }
        }
        else if(input_data.send_to_id == 3)//成功添加好友
        {
            Main.main_user.add_friend(input_data.my_id, input_data.searched_user);
            Friend_confirm_card card = new Friend_confirm_card(0, input_data.searched_user, 2);
            Main.main_user.add_confirmed_data(new Friend_confirm_data(0, input_data.searched_user, 2));
            if(Add_friend_window.current != null)
            {
                Add_friend_window.current.add_confirm_message(card);
            }
        }
        else//正常信息
        {
            Main.main_user.add_message(input_data);
            if(!input_data.data.is_user)
            {
                input_data.data.is_user = true;
                Main.main_user.send_message(new Send_data(input_data.my_id, input_data.data));
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
}
