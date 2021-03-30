package Main_window.User_Server;

import Main_window.Data.*;
import Main_window.Login_window;
import Main_window.Main;
import Main_window.Pop_window.Add_friend_window;
import Main_window.Separate_panel.Scroll_panel;
import Main_window.Window;
import Server.Data.Login_back_data;
import Server.Data.Search_back_data;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.*;
import java.util.Timer;

/**
 * @author: 李子麟
 * @date: 2021/3/18 9:24
 **/
public class User implements Serializable
{
    public static final String file_root_path = "./User Data/";
    public String name;
    private int id;
    private List<User_friend> friends;
    private List<Friend_confirm_data> confirm_data;
    private Friend_list_data friend_list_data;
    private List<User_group> groups;

    public User()
    {
        friends = new ArrayList<User_friend>();
        confirm_data = new ArrayList<Friend_confirm_data>();
        friend_list_data = new Friend_list_data();
        groups = new ArrayList<>();
    }

    public User(String name)
    {
        this.name = name;
        friends = new ArrayList<User_friend>();
        confirm_data = new ArrayList<Friend_confirm_data>();
        friend_list_data = new Friend_list_data();
        groups = new ArrayList<>();
    }

    public void send_message(Send_data message)
    {
        new Thread(()->
        {
            try
            {
                SocketChannel channel = SocketChannel.open();
                channel.configureBlocking(false);
                Selector selector = Selector.open();
                //channel.register(selector, SelectionKey.OP_CONNECT);
                channel.connect(new InetSocketAddress("192.168.137.1", 10088));
                ;
                channel.write(ByteBuffer.wrap("hello".getBytes()));
                while(selector.select() > 0)
                {
                    Iterator iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext())
                    {
                        SelectionKey key = (SelectionKey) iterator.next();
                        iterator.remove();
                        if (key.isConnectable())
                        {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            if (socketChannel.isConnectionPending())
                            {
                                channel.finishConnect();
                            }
                            ByteBuf buf = Unpooled.buffer(10);
                            ByteBufOutputStream outputStream = new ByteBufOutputStream(buf);
                            ObjectEncoderOutputStream out = new ObjectEncoderOutputStream(outputStream);
                            out.writeObject(message);
                            ByteBuffer byteBuffer = buf.nioBuffer();
                            while (byteBuffer.hasRemaining())
                            {
                                channel.write(byteBuffer);
                            }
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                        else if(key.isReadable())
                        {
                            if(message.data_type == Send_data.Data_type.Search_friend)
                            {
                                ByteBuf buf = Unpooled.buffer(10);
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                while (true)
                                {
                                    if (!(channel.read(buffer) > 0))
                                        break;
                                    buffer.flip();
                                    buf.readBytes(buffer);
                                    buffer.clear();
                                }
                                ByteBufInputStream inputStream = new ByteBufInputStream(buf);
                                ObjectDecoderInputStream input = new ObjectDecoderInputStream(inputStream);
                                Search_back_data back_data = (Search_back_data) input.readObject();
                                if(Add_friend_window.current != null)
                                {
                                    Add_friend_window.current.add_friend_card(back_data);
                                }
                            }
                        }
                    }
                }
                channel.close();
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "无法连接服务器",
                        "错误", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }).start();
    }

    /**
     *
     * @param user_id
     * @param is_register: 如果是，为注册信息
     * @param password
     */
    public void send_login_message(String name, int user_id, boolean is_register, String password)
    {
        new Thread(()->
        {
            try
            {
                SocketChannel channel = SocketChannel.open();
                channel.configureBlocking(false);
                Selector selector = Selector.open();

                //channel.register(selector, SelectionKey.OP_CONNECT);
                channel.connect(new InetSocketAddress("192.168.137.1", 10087));
                while (!channel.finishConnect());
                write_login_message(channel, name, user_id, is_register, password);//向服务器写信息

                while(selector.select() > 0)
                {
                    Iterator iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext())
                    {
                        SelectionKey key = (SelectionKey) iterator.next();
                        iterator.remove();
                        if(key.isConnectable())
                        {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            if(socketChannel.isConnectionPending())
                            {
                                channel.finishConnect();
                            }

                            socketChannel.configureBlocking(false);
                            //socketChannel.register(selector, SelectionKey.OP_READ);
                            write_login_message(socketChannel, name, user_id, is_register, password);//向服务器写信息
                        }
                        else if(key.isReadable())
                        {
                            ByteBuf buf = Unpooled.buffer(10);
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            while (true)
                            {
                                buffer.flip();
                                if (!(channel.read(buffer) > 0))
                                    break;
                                buf.writeBytes(buffer);
                                buffer.clear();
                            }
                            ByteBufInputStream inputStream = new ByteBufInputStream(buf);
                            ObjectDecoderInputStream input = new ObjectDecoderInputStream(inputStream);
                            Login_back_data back_data = (Login_back_data)input.readObject();
                            //接收注册和登录信息
                            if(back_data.is_error)
                            {
                                if(id == 10)//正常返回用户好友信息,本地信息缺失的情况下
                                {
                                    for(Send_data send_data : back_data.storage_data)
                                    {
                                        friends.add(new User_friend(send_data.send_to_id, send_data.searched_user));
                                    }
                                }
                                else
                                {
                                    Main.login_window.set_text("用户名或密码错误");
                                }
                            }
                            else//成功
                            {
                                if(is_register)
                                {
                                    JOptionPane.showMessageDialog(null, "成功注册\n 账号id为"+back_data.id,
                                            "消息", JOptionPane.INFORMATION_MESSAGE);
                                    this.name = back_data.name;
                                    id = back_data.id;
                                }
                                else//登录成功
                                {
                                    if(back_data.id == 10)
                                    {
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
                                    }
                                    load_from_data(user_id);
                                    for(Send_data data : back_data.storage_data)//加载下线期间发来的消息
                                    {
                                        User_Server_handle_thread.handle_message(data);
                                    }
                                    Login_window.current.dispose();
                                    new Window("网络聊天室");
                                }
                            }
                        }
                    }
                }
                channel.close();
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(null, "无法连接服务器",
                        "错误", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
            catch(ClassNotFoundException e2)
            {
                e2.printStackTrace();
            }
        }).start();
    }

    private void write_login_message(SocketChannel channel, String name, int user_id, boolean is_register, String password)
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
        try
        {
            Login_data login_data = new Login_data(
                    InetAddress.getLocalHost().getHostAddress(),
                    User_server.receive_port, user_id, s, Main.main_user.name
            );
            login_data.name = name;
            login_data.is_regesiter = is_register;
            ByteBuf buf = Unpooled.buffer(256);
            ByteBufOutputStream outputStream = new ByteBufOutputStream(buf);
            ObjectEncoderOutputStream out = new ObjectEncoderOutputStream(outputStream);
            out.writeObject(login_data);
            ByteBuffer byteBuffer = buf.nioBuffer();
            while (byteBuffer.hasRemaining())
            {
                channel.write(byteBuffer);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
        File file = new File("User Data");
        file.mkdir();
        try
        {
            File data_file = new File(file_root_path + String.valueOf(id));
            FileOutputStream fileOutputStream = new FileOutputStream(data_file);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void load_from_data(int id)
    {
        File file = new File(file_root_path + String.valueOf(id));
        if(!file.exists())
        {
            Main.main_user.id = id;
            /**
             * TODO: 从服务器加载好友列表
             */
            Main.main_user.send_login_message(String.valueOf(id), 10, false, "");
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
}
