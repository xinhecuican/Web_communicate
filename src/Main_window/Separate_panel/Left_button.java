package Main_window.Separate_panel;

import Main_window.Data.Message_data;
import Main_window.Data.message_rightdata;
import Main_window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

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
    private Message_data data;
    public Left_button()
    {
        name = "";
        set_style();
        data = new Message_data();
    }

    public Left_button(String name, Message_data data)
    {
        this.name = name;
        nearest_message = "";
        time = "";
        this.data = data;
        set_style();
    }

    public Left_button(String name, String nearest_message, String time)
    {
        this.name = name;
        this.nearest_message = nearest_message;
        this.time = time;
        data = new Message_data(name);
        set_style();
    }

    public Left_button(Message_data data)
    {
        name = data.name;
        message_rightdata right_data = data.recently_message();
        nearest_message = right_data.message;
        time = right_data.time;
        this.data = data;
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
            ArrayList<message_rightdata> rightdataList = (ArrayList<message_rightdata>) data.get_datalist();
            for(int i=0; i<rightdataList.size(); i++)
            {
                Window.current.getRight_panel().add_piece_message(rightdataList.get(i));
            }
        }
    }
}