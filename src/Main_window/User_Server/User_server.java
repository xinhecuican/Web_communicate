package Main_window.User_Server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: 李子麟
 * @date: 2021/3/18 12:21
 **/
public class User_server extends Thread
{
    public static int receive_port;
    @Override
    public void run()
    {
        super.run();
        try
        {
            ServerSocket socket = new ServerSocket(0);
            receive_port = socket.getLocalPort();
            while(true)
            {
                Socket communicate_socket = socket.accept();
                User_Server_handle_thread handle = new User_Server_handle_thread(communicate_socket);
                handle.start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
