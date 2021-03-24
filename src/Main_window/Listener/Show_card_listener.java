package Main_window.Listener;

import Main_window.Component.Left_button;
import Main_window.Separate_panel.Left_panel;
import Main_window.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: 李子麟
 * @date: 2021/3/21 19:25
 * 朋友列表中点击，在主列表中显示
 **/
public class Show_card_listener implements ActionListener
{
    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        Left_button button = (Left_button)actionEvent.getSource();
        Window.current.getLeft_panel().add_card(button.id,
                button.get_name(), button.data, new Write_message_listener());
        Window.current.requestFocus();
    }
}
