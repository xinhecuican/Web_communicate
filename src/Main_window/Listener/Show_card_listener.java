package Main_window.Listener;

import Main_window.Component.Base_button_card;
import Main_window.Main;
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
        Base_button_card button = (Base_button_card)actionEvent.getSource();
        Window.current.getScroll_panel().add_card(Main.main_user.find_friend(button.id), true);
        Window.current.requestFocus();
    }
}
