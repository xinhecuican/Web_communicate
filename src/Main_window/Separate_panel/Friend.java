package Main_window.Separate_panel;

import Main_window.Data.Message_data;
import Main_window.Data.message_rightdata;
import Main_window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author: 李子麟
 * @date: 2021/3/15 21:09
 **/
public class Friend extends JButton
{
    private String name;
    private String nearest_message;
    private String time;
    private JPanel root_panel;
    public Message_data data;
    public int recent_port;
    public String recent_ip;
    public Friend()
    {
        name = "";
        set_style();
        data = new Message_data();
        setContentAreaFilled(false);
        setLayout(new GridBagLayout());
    }

    public Friend(String host, int port, String name, Message_data data)
    {
        this.name = name;
        nearest_message = "";
        time = "";
        recent_ip = host;
        recent_port = port;
        if(!data.is_empty())
        {
            nearest_message = data.recently_message().message;
            time = data.recently_message().time;
        }
        this.data = data;
        setContentAreaFilled(false);
        setLayout(new GridBagLayout());
        set_style();
    }

    public Friend(String name, String nearest_message, String time)
    {
        this.name = name;
        this.nearest_message = nearest_message;
        this.time = time;
        data = new Message_data(new message_rightdata(time, nearest_message, false));
        setContentAreaFilled(false);
        setLayout(new GridBagLayout());
        set_style();
    }

    private void set_style()
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
        addActionListener(new Write_message_listener());
    }

    private class Write_message_listener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            Left_panel.select_button = (Friend)actionEvent.getSource();
            set_message();

        }
    }

    public void set_message()
    {
        ArrayList<message_rightdata> rightdataList = (ArrayList<message_rightdata>) data.get_datalist();
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
