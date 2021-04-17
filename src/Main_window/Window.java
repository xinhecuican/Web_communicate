package Main_window;

import Common.ImgUtils;
import Interface.IComponent;
import Main_window.Component.Base_button_card;
import Main_window.Data.Login_data;
import Main_window.Data.User_group;
import Main_window.Debug_helper.Debug_manager;
import Main_window.Pop_window.Add_friend_window;
import Main_window.Pop_window.Create_group_window;
import Main_window.Pop_window.Friend_list_window;
import Main_window.Pop_window.Pop_window;
import Main_window.Separate_panel.Scroll_panel;
import Main_window.Separate_panel.Right_panel;
import Main_window.User_Server.User;
import Main_window.User_Server.User_friend;
import Server.Server_handle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;
import java.util.Timer;

public class Window extends JFrame
{
    private JPanel root_panel;
    private Right_panel right_panel;
    private Scroll_panel scroll_panel;
    public static Window current;
    private ArrayList<IComponent> after_initialize;
    private static SimpleDateFormat formatter= new SimpleDateFormat("MM-dd HH:mm:ss");
    private static SimpleDateFormat file_formatter = new SimpleDateFormat("MM-dd HH_mm_ss");


    public Window(String title)
    {
        super(title, null);
        try
        {
            current = this;
            after_initialize = new ArrayList<>();
            set_menubar();
            set_panel();

            setIconImage(ImgUtils.getIcon("favicon.png").getImage());


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
                for(Base_button_card basecard : scroll_panel.getData())
                {
                    User_friend friend;
                    if((friend = Main.main_user.find_friend(basecard.id)) != null)
                    {
                        friend.is_user_in_list = true;
                    }
                    else
                    {
                        Main.main_user.find_group(basecard.id).is_group_in_list = true;
                    }
                }
                Login_data data = User.get_login_data(String.valueOf(Main.main_user.getId()),
                        11, "", Login_data.Login_type.Offline);
                Main.main_user.send_login_message(data);
                Main.main_user.write_to_directory();

            }
        });
        Timer timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                for(IComponent component : after_initialize)
                {
                    try
                    {
                        component.after_initialize();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.schedule(task, 1000);
    }

    private void set_panel()
    {
        root_panel = new JPanel();
        add(root_panel);
        //tab_panel = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
        scroll_panel = new Scroll_panel();
        right_panel = new Right_panel();
        root_panel.add(scroll_panel);
        root_panel.add(right_panel);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.ipadx = Main.LEFT_PANEL_WIDTH;
        constraints.weighty = 1;
        constraints.gridwidth = 200;
        layout.addLayoutComponent(scroll_panel, constraints);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.ipady = 800;
        layout.addLayoutComponent(right_panel, constraints);

        root_panel.setLayout(layout);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for(User_friend friend : Main.main_user.get_list_friend())//加载上次在表中的好友
                {
                    scroll_panel.add_card(friend, true);
                    friend.is_user_in_list = false;
                }
                for(User_group group : Main.main_user.get_all_groups())
                {
                    scroll_panel.add_card(group, true);
                    group.is_group_in_list = false;
                }
            }
        }).start();


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

        return formatter.format(new Date(System.currentTimeMillis()));
    }

    public static String get_format_time(long time)
    {
        return file_formatter.format(new Date(time));
    }

    public Right_panel getRight_panel()
    {
        return current.right_panel;
    }

    public Scroll_panel getScroll_panel()
    {
        return current.scroll_panel;
    }

    private void set_menubar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setVisible(true);


        JMenu menu_setting = new JMenu("设置");

        JMenu menu_friend = new JMenu("好友");
        JMenuItem friend_list = new JMenuItem("好友列表");
        friend_list.setActionCommand("list");
        JMenuItem button_add_friend = new JMenuItem("添加好友");
        button_add_friend.setActionCommand("join");
        JMenuItem button_create_group = new JMenuItem("创建群聊");
        button_create_group.setActionCommand("create_group");
        button_create_group.addActionListener((actionEvent)->
        {
            if(actionEvent.getActionCommand().equals("create_group"))
            {
                new Create_group_window(current);
            }
        });
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
                if(actionEvent.getActionCommand().equals("list"))
                {
                    new Friend_list_window(current);
                }
            }
        });
        menu_friend.add(friend_list);
        menu_friend.add(button_add_friend);
        menu_friend.add(button_create_group);

        JMenu menu_debug = new JMenu("调试");
        JMenuItem item_add_message = new JMenuItem("发消息");
        item_add_message.setActionCommand("add_message");
        item_add_message.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if(actionEvent.getActionCommand().equals("add_message"))
                    Debug_manager.current.debug_write_message();
            }
        });
        JMenuItem debug_create_user = new JMenuItem("创建用户");
        debug_create_user.setActionCommand("create_user");
        debug_create_user.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if(actionEvent.getActionCommand().equals("create_user"))
                {
                    String number = JOptionPane.showInputDialog(null, "输入用户量");
                    Debug_manager.current.create_thousands_user(Integer.parseInt(number));
                }
            }
        });
        JMenuItem debug_users_send = new JMenuItem("好友发送消息");
        debug_users_send.setActionCommand("users_send");
        debug_users_send.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if(actionEvent.getActionCommand().equals("users_send"))
                {
                    Debug_manager.current.handle_send_hundred_message();
                }
            }
        });

        JMenuItem debug_release_ram = new JMenuItem("释放内存");
        debug_release_ram.setActionCommand("release_ram");
        debug_release_ram.addActionListener((e)->{
            Server_handle.debug_release_channel();
        });
        menu_debug.add(item_add_message);
        menu_debug.add(debug_create_user);
        menu_debug.add(debug_users_send);
        menu_debug.add(debug_release_ram);

        menuBar.add(menu_setting);
        menuBar.add(menu_friend);
        menuBar.add(menu_debug);

        this.setJMenuBar(menuBar);
    }

    public void register_after_initialize(IComponent component)
    {
        after_initialize.add(component);
    }
}
