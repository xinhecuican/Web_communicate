package Main_window.User_Server;
import Main_window.Data.Friend_confirm_data;
import Main_window.Data.Send_data;
import Main_window.Main;
import Main_window.Pop_window.Add_friend_window;
import Main_window.Component.Friend_confirm_card;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author: 李子麟
 * @date: 2021/3/18 12:32
 **/
public class User_Server_handle_thread extends Thread
{
    private SelectionKey key;
    private ByteBuf buf;
    public User_Server_handle_thread(SelectionKey socket)
    {
        this.key = key;
        buf = Unpooled.buffer(256);
    }
    @Override
    public void run()
    {
        super.run();
        ObjectInputStream input = null;
        Send_data input_data = new Send_data();
        SocketChannel channel = (SocketChannel)key.channel();
        ByteBuffer buffer=  ByteBuffer.allocate(1024);
        try//读取数据
        {
            while (true)
            {
                buffer.flip();
                if (!(channel.read(buffer) > 0))
                    break;
                buf.writeBytes(buffer);
                buffer.clear();

            }
            ByteBufInputStream inputStream = new ByteBufInputStream(buf);
            input = new ObjectInputStream(inputStream);
            input_data = (Send_data)input.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        handle_message(input_data);
        key.cancel();
    }

    public static void handle_message(Send_data input_data)
    {
        Friend_confirm_card card = null;
        switch(input_data.data_type)
        {
            case Confirm_add_friend:
                Main.main_user.add_friend(input_data.my_id, input_data.searched_user);
                Friend_confirm_data confirm_data = new Friend_confirm_data(0, input_data.searched_user, 2);
                card = new Friend_confirm_card(confirm_data);
                Main.main_user.add_confirmed_data(confirm_data);
                break;
            case Request_add_fail:
                Friend_confirm_data confirm_data1 = new Friend_confirm_data(
                        input_data.send_to_id, input_data.searched_user, 1);
                Main.main_user.add_confirmed_data(confirm_data1);
                card = new Friend_confirm_card(confirm_data1);
                break;
            case Request_add_friend:
                Friend_confirm_data confirm_data2 = new Friend_confirm_data(
                        input_data.my_id, input_data.searched_user, 0);
                Main.main_user.add_confirmed_data(confirm_data2);
                card = new Friend_confirm_card(confirm_data2);
                break;
            case Search_fail:
                if(Add_friend_window.current != null)
                {
                    Add_friend_window.current.set_tooltip_message("未找到对应用户");
                }
                break;
            case One_piece_message:
                Main.main_user.add_message(input_data);
                break;
            case Create_group_message://send_to_id是groupid，searched_user是group名
                Friend_confirm_data confirm_data3 = new
                        Friend_confirm_data(input_data.send_to_id, input_data.searched_user, 3);
                card = new Friend_confirm_card(confirm_data3);
                Main.main_user.add_group(input_data.send_to_id, input_data.searched_user);
                JOptionPane.showMessageDialog(null, "创建群聊" + input_data.searched_user + "成功");
                break;
            case Add_group_successful:
                Friend_confirm_data confirm_data4 = new
                        Friend_confirm_data(input_data.send_to_id, input_data.searched_user, 4);
                Main.main_user.add_group(input_data.send_to_id, input_data.searched_user);
                card = new Friend_confirm_card(confirm_data4);
                break;
            case Piece_group_message://send_to_id是groupid
                Main.main_user.add_group_message(input_data);
                break;

        }

        if(Add_friend_window.current != null && Add_friend_window.current.get_select_panel_index() == 1 && card != null)
        {
            Add_friend_window.current.add_confirm_message(card);
        }
    }
}
