package Main_window.User_Server;

import Main_window.Data.Friend_confirm_data;
import Main_window.Data.Login_data;
import Main_window.Data.Send_data;
import Main_window.Login_window;
import Main_window.Main;
import Main_window.Pop_window.Add_friend_window;
import Main_window.Component.Friend_confirm_card;
import Main_window.Separate_panel.Left_panel;
import Main_window.Window;
import Server.Data.Login_back_data;
import Server.Data.Search_back_data;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    public User()
    {
        friends = new ArrayList<User_friend>();
        confirm_data = new ArrayList<Friend_confirm_data>();
    }

    public User(String name)
    {
        this.name = name;
    }

    public void send_message(Send_data message)
    {
        new Thread(()->
        {
            Socket client;
            try
            {
                client = new Socket("192.168.137.1", 10088);
                OutputStream outToServer = client.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outToServer);
                message.my_id = id;
                out.writeObject(message);
                if (message.send_to_id == 1) //搜索用户的返回信息
                {
                    client.setSoTimeout(30 * 1000);
                    if(message.my_id == 1)
                    {
                        if(Add_friend_window.current != null)
                        {
                            Add_friend_window.current.set_tooltip_message("未找到对应用户");
                        }
                    }
                    InputStream inputStream = client.getInputStream();
                    ObjectInputStream input_stream = new ObjectInputStream(inputStream);
                    Search_back_data back_data = (Search_back_data) input_stream.readObject();
                    if (Add_friend_window.current != null)
                    {
                        Add_friend_window.current.add_friend_card(back_data);
                    }
                }
                client.close();
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
                Socket socket = new Socket("192.168.137.1", 10087);
                OutputStream outToServer = socket.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outToServer);
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
                Login_data login_data = new Login_data(
                        InetAddress.getLocalHost().getHostAddress(),
                        User_server.receive_port, user_id, s, Main.main_user.name);
                login_data.name = name;
                login_data.is_regesiter = is_register;
                out.flush();
                out.writeObject(login_data);
                out.flush();
                socket.setSoTimeout(30 *  1000);
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream input = new ObjectInputStream(inputStream);
                Login_back_data back_data = (Login_back_data)input.readObject();
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
                        id = back_data.id;
                    }
                    else//登录成功
                    {
                        load_from_data(user_id);
                        for(Send_data data : back_data.storage_data)
                        {
                            if(data.send_to_id == 2)
                            {
                                if(data.my_id == 2)
                                {
                                    Main.main_user.add_confirmed_data(new Friend_confirm_data(0, data.searched_user, 1));
                                }
                                else
                                {
                                    Main.main_user.add_confirmed_data(new Friend_confirm_data(data.my_id, data.searched_user, 0));
                                }
                            }
                            else if(data.send_to_id == 3)
                            {
                                Main.main_user.add_confirmed_data(new Friend_confirm_data(0, data.searched_user, 2));
                            }
                            else
                            {
                                Main.main_user.add_message(data);
                            }
                        }
                        Login_window.current.dispose();
                        new Window("网络聊天室");
                    }

                }
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



    public synchronized void add_confirmed_data(Friend_confirm_data card)
    {
        confirm_data.add(card);
    }

    public int getId()
    {
        return id;
    }

    public synchronized void add_friend(int id, String name)
    {
        friends.add(new User_friend(id, name));
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

    public User_friend find_friend(int id)
    {
        for(User_friend friend : friends)
        {
            if(id == friend.getId())
            {
                return friend;
            }
        }
        return null;
    }

    public void add_message(Send_data send_data)
    {
        User_friend friend = find_friend(send_data.my_id);
        friend.communicate_data.add(send_data.data);
        if(Left_panel.select_button.id == friend.getId())
        {
            Window.current.getRight_panel().add_piece_message(send_data.data);
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
}
