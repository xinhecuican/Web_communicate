package Main_window.Listener;

import Main_window.Data.message_rightdata;
import Main_window.Separate_panel.Left_button;
import Main_window.Separate_panel.Left_panel;
import Main_window.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static Main_window.Separate_panel.Left_button.set_message;

/**
 * @author: 李子麟
 * @date: 2021/3/21 7:16
 **/
public class Write_message_listener implements ActionListener
{

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        Left_panel.select_button = (Left_button)actionEvent.getSource();
        set_message(Left_panel.select_button);
    }


}