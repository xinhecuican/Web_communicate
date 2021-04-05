package Main_window.User_Server;

import Common.UserDecoder;
import Common.UserEncoder;
import Main_window.Component.File_send_panel;
import Main_window.Data.*;
import Main_window.Login_window;
import Main_window.Main;
import Main_window.Pop_window.Add_friend_window;
import Main_window.Separate_panel.Scroll_panel;
import Main_window.Window;
import Server.Data.File_info;
import Server.Data.Login_back_data;
import Server.Data.Search_back_data;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.SocketUtils;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * @author: 李子麟
 * @date: 2021/3/18 9:24
 **/
public class User implements Serializable
{
    public static final String file_root_path = "User Data/";
    public String name;
    private int id;
    private List<User_friend> friends;
    private List<Friend_confirm_data> confirm_data;
    private Friend_list_data friend_list_data;
    private List<User_group> groups;
    private List<File_info> uploading_file;//正在上传的文件
    private transient Bootstrap message_bootstrap;
    private transient  Bootstrap login_bootstrap;
    private transient Channel send_message_channel;
    private  transient Channel login_channel;
    private transient Channel send_file_channel;


    public User()
    {
        friends = new ArrayList<User_friend>();
        confirm_data = new ArrayList<Friend_confirm_data>();
        friend_list_data = new Friend_list_data();
        groups = new ArrayList<>();
        uploading_file = new ArrayList<>();
        init_login_bootstrap();
        init_message_bootstrap();

    }

    public User(String name)
    {
        this.name = name;
        friends = new ArrayList<User_friend>();
        confirm_data = new ArrayList<Friend_confirm_data>();
        friend_list_data = new Friend_list_data();
        groups = new ArrayList<>();
        uploading_file = new ArrayList<>();
        init_login_bootstrap();
        init_message_bootstrap();
    }

    private void init_message_bootstrap()
    {
        EventLoopGroup group = new NioEventLoopGroup();
        message_bootstrap = new Bootstrap();
        message_bootstrap.group(group).channel(NioSocketChannel.class);
        message_bootstrap.handler(new ChannelInitializer<Channel>()
        {

            @Override
            protected void initChannel(Channel channel) throws Exception
            {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new IdleStateHandler(0, 60, 0,  TimeUnit.SECONDS));
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new UserDecoder());
                pipeline.addLast("encoder", new UserEncoder());
                pipeline.addLast("handler", new Client_handle());
            }
        });
        message_bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        try
        {
            send_file_channel = message_bootstrap.connect("192.168.137.1", 10088).sync().channel();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void init_login_bootstrap()
    {
        EventLoopGroup group1 = new NioEventLoopGroup();
        login_bootstrap = new Bootstrap();
        login_bootstrap.group(group1).channel(NioSocketChannel.class);
        login_bootstrap.handler(new ChannelInitializer<Channel>()
        {
            @Override
            protected void initChannel(Channel channel) throws Exception
            {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new IdleStateHandler(0, 60, 0,  TimeUnit.SECONDS));
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new UserDecoder());
                pipeline.addLast("encoder", new UserEncoder());
                pipeline.addLast("handler", new Login_message_handle());
            }
        });
        login_bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        try
        {
            login_channel = login_bootstrap.connect("192.168.137.1", 10087).sync().channel();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    public void send_file(File_info file)
    {
        Bootstrap file_bootstrap;
        file_bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        file_bootstrap = new Bootstrap();
        file_bootstrap.group(group).channel(NioSocketChannel.class);
        file_bootstrap.handler(new ChannelInitializer()
        {

            @Override
            protected void initChannel(Channel channel) throws Exception
            {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new UserDecoder());
                pipeline.addLast("encoder", new UserEncoder());
                pipeline.addLast("handler", new File_handle(file, false));
            }
        });
        file_bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        synchronized (this)
        {
            uploading_file.add(file);
        }
        try
        {
            add_file_message(file);
            ChannelFuture future = file_bootstrap.connect("192.168.137.1", 10089).sync();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    public void send_message(Send_data message)
    {
        try
        {
            if(message_bootstrap == null)
            {
                init_message_bootstrap();
            }
            message.my_id = id;
            send_file_channel.writeAndFlush(message);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "无法连接服务器",
                        "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    public void send_login_message(Login_data login_data)
    {
        if(login_bootstrap == null)
        {
            init_login_bootstrap();
        }
        //这里的sync是同步等待连接成功
        login_channel.writeAndFlush(login_data);
    }

    public void accept_login_data(Login_back_data back_data)
    {
        //接收注册和登录信息
        switch (back_data.type)
        {
            case Error:
                if(back_data.id == 1)
                    Main.login_window.set_text("用户名或密码错误");
                else if(back_data.id == 2)
                    Main.login_window.set_text("用户已在其他地方登录");
                break;
            case Friend_info:
                for(Send_data send_data : back_data.storage_data)
                {
                    friends.add(new User_friend(send_data.send_to_id, send_data.searched_user));
                }
                break;
            case Register:
                JOptionPane.showMessageDialog(null, "成功注册\n 账号id为"+back_data.id,
                        "消息", JOptionPane.INFORMATION_MESSAGE);
                this.name = back_data.name;
                this.id = back_data.id;
                break;
            case Login_And_Heart:
                java.util.Timer timer = new Timer();
                TimerTask task = new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        Send_data data = new Send_data();
                        data.data_type = Send_data.Data_type.None;
                        send_message(data);
                    }
                };
                timer.schedule(task, 0, 60 * 1000);
            case Login:
                load_from_data(back_data);
                for(Send_data data : back_data.storage_data)//加载下线期间发来的消息
                {
                    User_server_handle.handle_message(data);
                }
                Login_window.current.dispose();
                new Window("网络聊天室");
                break;
        }
    }

    public static Login_data get_login_data(String name, int user_id,
                                            String password, Login_data.Login_type type)
    {
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        //加密开始
        char[] charArray = password.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        assert md5 != null;
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++)
        {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        char[] a = hexValue.toString().toCharArray();
        for (int i = 0; i < a.length; i++)
        {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        //加密结束
        Login_data login_data = null;
        try
        {
            login_data = new Login_data(
                    InetAddress.getLocalHost().getHostAddress(),
                    User_server.receive_port, user_id, s, Main.main_user.name
            );
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        login_data.name = name;
        login_data.type = type;
        return login_data;
    }

    public synchronized void add_confirmed_data(Friend_confirm_data card)
    {
        confirm_data.add(card);
    }

    public int getId()
    {
        return id;
    }

    public void add_group(int id, String name)
    {
        int low = 0, high = groups.size() - 1;
        while(low <= high)
        {
            int mid = (low + high) / 2;
            int compare_id = groups.get(mid).getGroup_id();
            if(compare_id > id)
            {
                high = mid - 1;
            }
            else
            {
                low = mid + 1;
            }
        }
        synchronized (this)
        {
            friend_list_data.add_friend("我的群聊", id);
            groups.add(high+1, new User_group(id, name));
        }
    }

    public void add_friend(int id, String name)
    {
        //boolean is_success = false;
        int low = 0, high = friends.size() - 1;
        while(low <= high)
        {
            int mid = (low + high) / 2;
            int compare_id = friends.get(mid).getId();
            if(compare_id > id)
            {
                high = mid - 1;
            }
            else
            {
                low = mid + 1;
            }
        }
        synchronized (this)
        {
            friends.add(high + 1, new User_friend(id, name));
            friend_list_data.add_friend("我的好友", id);
        }
        /*for(int i=0; i<friends.size(); i++)
        {
            if(friends.get(i).getId() >= id)
            {
                friends.add(i, new User_friend(id, name));
                is_success = true;
                break;
            }
        }
        if(!is_success)
        {
            friends.add(new User_friend(id, name));
        }*/
    }

    public void remove_confirm_card(int id)
    {
        for(int i=0; i<confirm_data.size(); i++)
        {
            if(confirm_data.get(i).id == id)
            {
                confirm_data.remove(i);
                break;
            }
        }
    }

    public ArrayList<Friend_confirm_data> getConfirm_data()
    {
        return (ArrayList<Friend_confirm_data>)confirm_data;
    }

    public List<User_friend> get_list_friend()
    {
        List<User_friend> ans = new ArrayList<User_friend>();
        for(User_friend friend : friends)
        {
            if(friend.is_user_in_list)
            {
                ans.add(friend);
            }
        }
        return ans;
    }

    public List<User_friend> get_all_friends()
    {
        return friends;
    }

    public List<User_group> get_all_groups() { return groups;}

    public User_friend find_friend(int id)
    {
        //前面添加朋友是按序的，所以可以二分
        int low = 0;
        int high = friends.size() - 1;
        while(low <= high)
        {
            int mid = (low + high) / 2;
            int compare_id = friends.get(mid).getId();
            if(compare_id == id)
            {
                return friends.get(mid);
            }
            else if(compare_id > id)
            {
                high = mid - 1;
            }
            else
            {
                low = mid + 1;
            }
        }
        return null;
    }

    public User_group find_group(int id)
    {
        int low = 0;
        int high = groups.size() - 1;
        while(low <= high)
        {
            int mid = (low + high) / 2;
            int compare_id = groups.get(mid).getGroup_id();
            if(compare_id == id)
            {
                return groups.get(mid);
            }
            else if(compare_id > id)
            {
                high = mid - 1;
            }
            else
            {
                low = mid + 1;
            }
        }
        return null;
    }

    public void add_message(Send_data send_data)
    {
        User_friend friend = find_friend(send_data.my_id);
        friend.communicate_data.add(send_data.data);
        if(Scroll_panel.select_button != null && Scroll_panel.select_button.id == friend.getId())
        {
            Window.current.getRight_panel().add_piece_message(send_data.data);
        }
    }

    public void add_group_message(Send_data data)
    {
        User_group group = find_group(data.send_to_id);
        group.data.add(data.data);
        if(Scroll_panel.select_button != null && Scroll_panel.select_button.id == group.getGroup_id())
        {
            Window.current.getRight_panel().add_piece_message(data.data);
        }
    }

    public void add_file_message(Send_data send_data)//接收方
    {
        User_friend friend = find_friend(send_data.my_id);
        File file = new File(User.file_root_path + String.valueOf(id)  + "/Data/" + send_data.searched_user);
        File_info file_info = new File_info(file, send_data.my_id);
        file_info.my_id = send_data.my_id;
        file_info.send_to_id = send_data.send_to_id;
        file_info.time = Long.parseLong(send_data.data.message);
        file_info.file_len = Integer.parseInt(send_data.data.message_sender_name);
        file_info.file_name = send_data.searched_user;
        message_rightdata rightdata = new message_rightdata();
        rightdata.is_file = true;
        rightdata.time = Window.get_time();
        rightdata.message_sender_name = String.valueOf(file_info.my_id);
        rightdata.message = String.valueOf(file_info.time);
        friend.communicate_data.add(rightdata);
        File_send_panel file_send_panel = friend.communicate_data.add(file_info);
        if(Scroll_panel.select_button != null && Scroll_panel.select_button.id == friend.getId())
        {
            Window.current.getRight_panel().add_file_message(file_send_panel);
        }
    }

    public void add_file_message(File_info file_info)//发送方
    {
        User_friend friend = find_friend(file_info.send_to_id);
        File_send_panel send_panel = friend.communicate_data.add(file_info);
        message_rightdata rightdata = new message_rightdata();
        rightdata.is_file = true;
        rightdata.time = Window.get_time();
        rightdata.message_sender_name = String.valueOf(file_info.send_to_id);
        rightdata.message = String.valueOf(file_info.time);
        friend.communicate_data.add(rightdata);
        if(Scroll_panel.select_button != null && Scroll_panel.select_button.id == friend.getId())
        {
            Window.current.getRight_panel().add_file_message(send_panel);
        }
    }


    /**
     *
     * @param id
     * @param data
     * @return 如果是假，则表示是群，真为用户
     */
    public boolean add_message(int id, message_rightdata data)
    {
        User_friend friend;
        if((friend = find_friend(id)) == null)
        {
            find_group(id).data.add(data);
            return false;
        }
        else
        {
            friend.communicate_data.add(data);
            return true;
        }
    }

    public void write_to_directory()
    {
        File file = new File("User Data/");
        file.mkdir();
        File directory = new File("User Data/" + String.valueOf(id));
        directory.mkdir();
        try
        {
            File data_file = new File(file_root_path + String.valueOf(id) + "/" + String.valueOf(id));
            FileOutputStream fileOutputStream = new FileOutputStream(data_file);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void load_from_data(Login_back_data data)
    {
        int id = data.id;
        File file = new File(file_root_path + String.valueOf(id) +"/" + String.valueOf(id));
        if(!file.exists())
        {
            Main.main_user.id = id;
            Main.main_user.name = data.name;
            Login_data data2 = get_login_data(String.valueOf(id), 10, "",
                    Login_data.Login_type.Request_friend_message);
            Main.main_user.send_login_message(data2);
        }
        else
        {
            try
            {
                ObjectInputStream input = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
                Main.main_user = (User)input.readObject();
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public List<Tree_data> get_friend_list_data()
    {
        return friend_list_data.get_tree_data();
    }

    public void remove_finished_file(File_info file)
    {
        uploading_file.remove(file);
    }

    public boolean is_file_unfinished(File_info file)
    {
        return uploading_file.contains(file);
    }
}
