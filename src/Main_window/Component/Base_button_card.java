package Main_window.Component;

import Common.ImgUtils;
import Main_window.Data.Message_data;
import Main_window.Data.User_group;
import Main_window.Data.message_rightdata;
import Main_window.Main;
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
public class Base_button_card extends JButton
{
    private JLabel time_label;
    private JLabel message_label;
    private String name;
    private String nearest_message;
    private String time;
    private JPanel root_panel;
    private JLabel message_sum_label;
    public Message_data data;
    public int id;
    private int message_sum;
    public Base_button_card()
    {
        name = "";
        data = new Message_data();
    }

    public Base_button_card(String name, Message_data data, ActionListener listener)
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
        set_style(listener);
    }

    public Base_button_card(String name, String id, ActionListener listener)
    {
        this.name = name;
        this.nearest_message = id;
        set_style(listener);
    }

    public Base_button_card(User_friend friend, ActionListener listener)
    {
        id = friend.getId();
        name = friend.getName();
        data = friend.communicate_data;
        message_sum = friend.get_message_sum();
        nearest_message = !data.is_empty() ? data.recently_message().message : "";
        time = !data.is_empty() ? data.recently_message().time : "";

        set_style(listener);
    }

    public Base_button_card(User_group group, ActionListener listener)
    {
        id = group.getGroup_id();
        name = group.getGroup_name();
        data = group.data;
        nearest_message = !data.is_empty() ? data.recently_message().message : "";
        time = !data.is_empty() ? data.recently_message().time : "";
        set_style(listener);
    }

    private void set_style(ActionListener listener)
    {
        setContentAreaFilled(false);
        setLayout(new GridBagLayout());
        //setMaximumSize(new Dimension(220, 60));
        root_panel = new JPanel();
        root_panel.setOpaque(false);
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
        time_label = new JLabel(time);
        root_panel.add(time_label, constraints);
        message_sum_label = new JLabel((message_sum != 0 ? String.valueOf(message_sum) : ""));
        message_sum_label.setBorder(BorderFactory.createLineBorder(new Color(98, 193, 255), 3, true));
        if(message_sum == 0)
        {
            message_sum_label.setVisible(false);
        }
        constraints.gridwidth = 1;
        constraints.ipadx = 5;

        constraints.weightx = 0;
        root_panel.add(message_sum_label, constraints);
        root_panel.add(new JLabel(""), constraints);
        constraints.gridwidth = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        message_label = new JLabel(nearest_message);
        root_panel.add(message_label, constraints);


        addActionListener(listener);
    }

    public static void set_message(Base_button_card select_button)
    {
        ArrayList<message_rightdata> rightdataList = (ArrayList<message_rightdata>) select_button.data.get_datalist();
        Window.current.getRight_panel().clear();
        for(int i=0; i<rightdataList.size(); i++)
        {
            if(!rightdataList.get(i).is_file)
            {
                Window.current.getRight_panel().add_piece_message(rightdataList.get(i));
            }
            else
            {
                User_friend friend ;
                int message_id = Integer.parseInt(rightdataList.get(i).message_sender_name);
                if((friend = Main.main_user.find_friend(message_id)) != null)
                {
                    Window.current.getRight_panel().add_file_message(
                            friend.communicate_data.create_file_panel(Long.parseLong(rightdataList.get(i).message)));
                }
                else
                {
                    User_group group = Main.main_user.find_group(message_id);
                    Window.current.getRight_panel().add_file_message(
                            group.data.create_file_panel(Long.parseLong(rightdataList.get(i).message)));
                }
            }
        }
    }

    public void message_sum_add()
    {
        message_sum++;
        message_sum_label.setText(String.valueOf(message_sum));

        message_label.setText(data.recently_message().message);
        time_label.setText(data.recently_message().time);
        message_sum_label.setVisible(true);
    }

    public void message_sum_clear()
    {
        message_sum = 0;
        message_sum_label.setText("");
        message_sum_label.setVisible(false);
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
