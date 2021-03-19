package Main_window;

import Main_window.Server.Server;
import Main_window.Server.User;

public class Main
{
    public static final int LEFT_PANEL_WIDTH = 200;
    public static  User main_user ;
    public static boolean need_reset;
    public static Server main_server;
    public static void main(String[] args)
    {
        need_reset = false;
        main_server = new Server();
        main_user = new User("name");
        main_server.start();
        new Window("title");
    }
}
