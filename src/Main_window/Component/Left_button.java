package Main_window.Component;

import Main_window.Data.Message_data;
import Main_window.Data.message_rightdata;
import Main_window.User_Server.User_friend;
import Main_window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author: 李子麟
 * @date: 2021/3/15 21:09
 **/
public class Left_button extends JButton
{
    private String name;
    private String nearest_message;
    private String time;
    private JPanel root_panel;
    public Message_data data;
    public int id;
    public Left_button()
    {
        name = "";
        data = new Message_data();
        setContentAreaFilled(false);
        setLayout(new GridBagLayout());
    }

    public Left_button(String name, Message_data data, ActionListener listener)
    {
        this.name = name;
        nearest_message = "";
        time = "";
        if(!data.is_empty())
        {
            nearest_message = data.recently_message().message;
            time = data.recently_message().time;
        }
        this.data = data;
        setContentAreaFilled(false);
        setLayout(new GridBagLayout());
        set_style(listener);
    }

    public Left_button(String name, String id, ActionListener listener)
    {
        this.name = name;
        this.nearest_message = id;
        setContentAreaFilled(false);
        setLayout(new GridBagLayout());
        set_style(listener);
    }

    public Left_button(User_friend friend)
    {
        id = friend.getId();
        name = friend.name;
        data = friend.communicate_data;
        nearest_message = !data.is_empty() ? data.recently_message().message : "";
        time = !data.is_empty() ? data.recently_message().time : "";
    }

    private void set_style(ActionListener listener)
    {
        setMaximumSize(new Dimension(220, 60));
        root_panel = new JPanel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        add(root_panel, constraints);
        GridBagLayout layout = new GridBagLayout();
        root_panel.setLayout(layout);
        constraints.gridwidth= 4;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        root_panel.add(new JLabel(name), constraints);
        constraints.weightx = 0;
        constraints.gridwidth = 0;
        constraints.fill= GridBagConstraints.NONE;
        root_panel.add(new JLabel(time), constraints);
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        root_panel.add(new JLabel(nearest_message), constraints);
        addActionListener(listener);
    }

    public static void set_message(Left_button select_button)
    {
        ArrayList<message_rightdata> rightdataList = (ArrayList<message_rightdata>) select_button.data.get_datalist();
        Window.current.getRight_panel().clear();
        for(int i=0; i<rightdataList.size(); i++)
        {
            Window.current.getRight_panel().add_piece_message(rightdataList.get(i));
        }
    }


    public String get_name()
    {
        return name;
    }

    public void add_message(message_rightdata right_data)
    {
        data.add(right_data);
    }
}
