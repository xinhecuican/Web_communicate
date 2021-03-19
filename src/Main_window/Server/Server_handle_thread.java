package Main_window.Server;

import Main_window.Data.Message_data;
import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Main;
import Main_window.Separate_panel.Friend;
import Main_window.Window;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author: 李子麟
 * @date: 2021/3/18 12:32
 **/
public class Server_handle_thread extends Thread
{
    private Socket socket;
    public Server_handle_thread(Socket socket)
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


        ArrayList<Friend> data = Window.current.getLeft_panel().getData();
        boolean is_inlist = false;
        for(int i=0; i<data.size(); i++)
        {
            if(data.get(i).get_name().equals(input_data.name))
            {
                input_data.data.is_user = false;
                synchronized (this)
                {
                    data.get(i).data.get_datalist().add(input_data.data);
                }
                is_inlist = true;
                break;
            }
        }
        if(!is_inlist)
        {
            try
            {
                Socket send_to_socket = new Socket(input_data.my_host, input_data.my_port);
                OutputStream outToServer = send_to_socket.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outToServer);
                Send_data output_data = new Send_data(Main.main_user.get_name(), input_data.data,
                        input_data.my_host, input_data.my_port);
                output_data.my_host = InetAddress.getLocalHost().getHostAddress();
                output_data.my_port = Server.receive_port;
                out.writeObject(output_data);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            Window.current.getLeft_panel().add_card(input_data.my_host, input_data.my_port, input_data.name, new Message_data(input_data.data));
        }
        try
        {
            socket.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}