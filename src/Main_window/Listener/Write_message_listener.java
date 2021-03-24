package Main_window.Listener;

import Main_window.Component.Left_button;
import Main_window.Separate_panel.Left_panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static Main_window.Component.Left_button.set_message;

/**
 * @author: 李子麟
 * @date: 2021/3/21 7:16
 * 位于主列表中，点击后切换到这个朋友的信息
 **/
public class Write_message_listener implements ActionListener
{

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        Left_panel.select_button_change((Left_button)actionEvent.getSource());
        set_message(Left_panel.select_button);
    }


}
