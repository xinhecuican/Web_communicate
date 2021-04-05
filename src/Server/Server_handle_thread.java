/*package Server;

import Main_window.Data.Login_data;
import Main_window.Data.Send_data;
import Server.Data.Group_message;
import Server.Data.Login_back_data;
import Server.Data.Search_back_data;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;*/

/**
 * @author: 李子麟
 * @date: 2021/3/19 21:46
 **/
/*
public class Server_handle_thread extends Thread
{
    private SelectionKey key;
    private int mode;
    ByteBuf buf ;
    public Server_handle_thread(SelectionKey key, int mode)
    {
        this.key = key;
        this.mode = mode;
        buf = Unpooled.buffer(256);
    }

    public void run()
    {
        ObjectDecoderInputStream input = null;
        SocketChannel channel = (SocketChannel)key.channel();
        ByteBuffer buffer=  ByteBuffer.allocate(1024);
        try//读取数据
        {
            while (channel.read(buffer) > 0)
            {
                buffer.flip();
                buf.writeBytes(buffer);
                buffer.clear();
            }
            System.out.println(buf.writerIndex());
            ByteBufInputStream inputStream = new ByteBufInputStream(buf);
            input = new ObjectDecoderInputStream(inputStream);
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
                    buf.clear();
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
                        write_back(channel, back_data);
                        break;
                    }
                    else if(data.id == 11)//下线
                    {
                        int user_id = Integer.parseInt(data.name);
                        Objects.requireNonNull(Server_main.search_user(user_id)).is_online = false;
                        break;
                    }

                    //登录及注册
                    int id = Server_main.Login(data);//包含了注册的情况，如果是注册，返回注册id
                    if(id == -1) //出错
                    {
                        Login_back_data back_data = new  Login_back_data(true, null);
                        back_data.id = 1;
                        write_back(channel, data);
                    }
                    else if(id == 0)//正常登录
                    {
                        User_message user_message = Server_main.search_user(data.id);
                        Login_back_data login_back_data = new Login_back_data(false, user_message.data);
                        if(Server_main.heart_test_user == null || !Server_main.heart_test_user.is_online)//心跳检测
                        {
                            Server_main.heart_test_user = user_message;
                            login_back_data.id = 10;
                        }
                        login_back_data.id = 0;
                        login_back_data.name = user_message.user_name;
                        write_back(channel, data);
                    }
                    else//注册
                    {

                        Login_back_data login_back_data = new Login_back_data(false, null);
                        login_back_data.name = data.name;
                        login_back_data.id = id;
                        //write_back(channel, data);
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
                    switch (data.data_type)
                    {
                        case Confirm_add_friend://确认添加好友,其中send_to_id是申请添加的人的id，my_id是好友id
                            String friend_name = Server_main.add_friend(data.send_to_id, data.my_id);
                            data.searched_user = friend_name;
                            Server_main.add_message(data.send_to_id, data);
                            break;
                        case Request_add_friend://请求添加好友,自己的名字在searched_user字段中
                            if (!Server_main.add_message(data.send_to_id, data))//处理未找到该用户的情况，找到了直接在函数中发送
                            {
                                data.data_type = Send_data.Data_type.Request_add_fail;
                                data.searched_user = Objects.requireNonNull(Server_main.search_user(data.send_to_id)).user_name;
                                User_message message = Server_main.search_user(data.my_id);
                                send_data(message.host, message.port, data);

                            }
                            break;
                        case Search_friend:
                            List<User_message> send_data = Server_main.search_user(data.searched_user);
                            List<Group_message> group_data = Server_main.search_group(data.searched_user);
                            if (send_data.size() > 0 || group_data.size() > 0)
                            {
                                Search_back_data search_back_data = new Search_back_data();
                                for (User_message message : send_data)
                                {
                                    search_back_data.add(message);
                                }
                                for(Group_message message : group_data)
                                {
                                    search_back_data.add(message.getGroup_id(), message.group_name, true);
                                }
                                write_back(channel, search_back_data);
                            }
                            else//没有搜索到
                            {
                                data.data_type = Send_data.Data_type.Search_fail;
                                Server_main.add_message(data.my_id, data);
                            }
                            break;
                        case One_piece_message://单个好友之间发送信息
                            Server_main.add_message(data.send_to_id, data);
                            break;
                        case Create_group_message:
                            int id = Server_main.create_group(data);
                            User_message main_user = Server_main.search_user(data.my_id);
                            data.send_to_id = id;
                            send_data(main_user.host, main_user.port, data);
                            break;
                        case Request_add_group:
                            Server_main.join_group(data.send_to_id, data);
                            data.data_type = Send_data.Data_type.Add_group_successful;
                            data.searched_user = Objects.requireNonNull(Server_main.search_group(data.send_to_id)).group_name;
                            Server_main.add_message(data.my_id, data);
                            break;
                        case Piece_group_message:
                            Server_main.add_group_message(data.send_to_id, data);
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
            channel.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void write_back(SocketChannel channel, Object data) throws IOException
    {
        buf.clear();
        ByteBufOutputStream outputStream = new ByteBufOutputStream(buf);
        ObjectEncoderOutputStream out = new ObjectEncoderOutputStream(outputStream);
        out.writeObject(data);
        channel.write(buf.nioBuffer());
    }

    public static void send_data(String host, int port, Object data) throws IOException
    {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(host, port));
        while(!channel.finishConnect());
        ByteBuf buf = Unpooled.buffer(256);
        ByteBufOutputStream outputStream = new ByteBufOutputStream(buf);
        ObjectEncoderOutputStream out = new ObjectEncoderOutputStream(outputStream);
        out.writeObject(data);
        ByteBuffer byteBuffer = buf.nioBuffer();
        while (byteBuffer.hasRemaining())
        {
            channel.write(byteBuffer);
        }
        channel.close();
    }
}
*/