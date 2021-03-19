package Main_window;

import Main_window.Data.Message_data;
import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Separate_panel.Left_panel;
import Main_window.Separate_panel.Right_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            set_menubar();
            set_panel();

            setMinimumSize(new Dimension(800, 600));
            setPreferredSize(new Dimension(800, 600));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setVisible(true);
            current = this;
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
        //tab_panel.setOpaque(true);
        //tab_panel.setUI(new Tabpanel_UI("#ffffff", "#000000"));

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
        menu_setting.add(socket_setting);
        socket_setting.addActionListener(actionEvent ->
        {
            if(actionEvent.getActionCommand().equals("socket_setting"))
            {
                new Pop_window(getX(), getY());
            }
        });
        menu_setting.add(socket_setting);

        menuBar.add(menu_setting);

        this.setJMenuBar(menuBar);
    }






}
