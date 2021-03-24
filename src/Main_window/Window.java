package Main_window;

import Main_window.Component.Left_button;
import Main_window.Listener.Write_message_listener;
import Main_window.Pop_window.Add_friend_window;
import Main_window.Pop_window.Friend_list_window;
import Main_window.Pop_window.Pop_window;
import Main_window.Pop_window.Register_window;
import Main_window.Separate_panel.Left_panel;
import Main_window.Separate_panel.Right_panel;
import Main_window.User_Server.User_friend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Window extends JFrame
{
    private JPanel root_panel;
    private Right_panel right_panel;
    private Left_panel left_panel;
    public static Window current;


    public Window(String title)
    {
        super(title, null);
        try
        {
            current = this;
            set_menubar();
            set_panel();

            setMinimumSize(new Dimension(800, 600));
            setPreferredSize(new Dimension(800, 600));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setVisible(true);

            //test();
            Dimension dim=getToolkit().getScreenSize();
            int w=dim.width/2;
            int h=dim.height/2;
            setLocation(w - 500, h-300);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        addWindowListener(new WindowAdapter()//退出窗口时
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                for(Left_button left_button : left_panel.getData())
                {
                    Main.main_user.find_friend(left_button.id).is_user_in_list = true;
                }
                Main.main_user.send_login_message(String.valueOf(Main.main_user.getId()),
                        11, false, "");
                Main.main_user.write_to_directory();

            }
        });
    }

    private void set_panel()
    {
        root_panel = new JPanel();
        add(root_panel);
        //tab_panel = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
        left_panel = new Left_panel();
        right_panel = new Right_panel();
        root_panel.add(left_panel);
        root_panel.add(right_panel);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.ipadx = Main.LEFT_PANEL_WIDTH;
        constraints.weighty = 1;
        constraints.gridwidth = 200;
        layout.addLayoutComponent(left_panel, constraints);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.ipady = 800;
        layout.addLayoutComponent(right_panel, constraints);

        root_panel.setLayout(layout);
        for(User_friend friend : Main.main_user.get_list_friend())//加载上次在表中的好友
        {
            left_panel.add_card(friend);
            friend.is_user_in_list = false;
        }

    }

    /*public void test()
    {
        for(int i=0; i<15; i++)
        {
            left_panel.add_card(String.valueOf(i), new Message_data(new message_rightdata(get_time(), "hello world", false)));
        }
        //Main.main_user.send_message(new Send_data("user1", new message_rightdata(get_time(), "hello", false)));
    }*/


    public static String get_time()
    {
        SimpleDateFormat formatter= new SimpleDateFormat("MM-dd HH:mm:ss");
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    public Right_panel getRight_panel()
    {
        return current.right_panel;
    }

    public Left_panel getLeft_panel()
    {
        return current.left_panel;
    }

    private void set_menubar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setVisible(true);


        JMenu menu_setting = new JMenu("设置");
        JMenuItem socket_setting = new JMenuItem("端口设置");
        socket_setting.setActionCommand("socket_setting");
        socket_setting.addActionListener(actionEvent ->
        {
            if(actionEvent.getActionCommand().equals("socket_setting"))
            {
                Pop_window pop_window = new Pop_window(this);
            }
        });
        menu_setting.add(socket_setting);

        JMenu menu_friend = new JMenu("好友");
        JMenuItem friend_list = new JMenuItem("好友列表");
        friend_list.setActionCommand("list");
        JMenuItem button_add_friend = new JMenuItem("添加好友");
        button_add_friend.setActionCommand("join");
        button_add_friend.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if(actionEvent.getActionCommand().equals("join"))
                {
                    new Add_friend_window(current);
                }
            }
        });
        friend_list.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                new Friend_list_window(current);
            }
        });
        menu_friend.add(friend_list);
        menu_friend.add(button_add_friend);
        menuBar.add(menu_setting);
        menuBar.add(menu_friend);

        this.setJMenuBar(menuBar);
    }
}
