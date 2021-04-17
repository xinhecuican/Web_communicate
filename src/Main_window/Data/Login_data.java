package Main_window.Data;

import java.io.Serializable;

/**
 * @author: 李子麟
 * @date: 2021/3/20 7:37
 **/
public class Login_data implements Serializable
{
    public String host;
    public int port;
    public int id;
    public String password;
    public String name;
    public Login_type type;
    public Login_data(String host, int port, int id, String password, String name)
    {
        this.host = host;
        this.port = port;
        this.id = id;
        this.password = password;

    }

    public enum Login_type
    {
        None,
        Request_friend_message,
        Offline,
        Login,
        Register,
        debug_create_user
    }
}
