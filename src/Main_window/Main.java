package Main_window;

import Main_window.User_Server.User_server;
import Main_window.User_Server.User;

public class Main
{
    public static final int LEFT_PANEL_WIDTH = 200;
    public static  User main_user ;
    public static boolean need_reset;
    public static User_server main_userserver;
    public static Login_window login_window;
    public static void main(String[] args)
    {
        main_userserver = new User_server();
        main_userserver.start();
        need_reset = false;
        main_user = new User();
        login_window = new Login_window("网络聊天室");
    }
}
