package Main_window.Data;

import java.io.Serializable;

/**
 * @author: 李子麟
 * @date: 2021/3/18 13:00
 **/
public class Send_data implements Serializable
{
    public String name;
    public message_rightdata data;
    public String send_host;
    public int send_port;
    public String my_host;
    public int my_port;
    public Send_data(String name, message_rightdata data, String host, int port)
    {
        this.name = name;
        this.data = data;
        this.send_host = host;
        this.send_port = port;
    }

    public Send_data()
    {
        data = new message_rightdata();
        name = "";
        send_host = "";
        send_port = 0;
    }

}