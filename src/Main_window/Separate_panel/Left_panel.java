package Main_window.Separate_panel;

import Main_window.Component.Friend_confirm_card;
import Main_window.Component.Left_button;
import Main_window.Data.Message_data;
import Main_window.User_Server.User_friend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static Main_window.Component.Left_button.set_message;

/**
 * @author: 李子麟
 * @date: 2021/3/15 20:41
 **/
public class Left_panel extends JPanel
{
    private JScrollPane scrollPane;
    private JPanel scroll_inner_panel;
    private ArrayList<Left_button> data;
    public static Left_button select_button;
    public Left_panel()
    {

        super();
        setLayout(new BorderLayout());
        scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        data = new ArrayList<Left_button>();
        setAutoscrolls(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scroll_inner_panel = new JPanel();
        scroll_inner_panel.setLayout(new BoxLayout(scroll_inner_panel, BoxLayout.Y_AXIS));
        //scroll_inner_panel.setPreferredSize(new Dimension(Main.LEFT_PANEL_WIDTH, 400));
        scrollPane.setViewportView(scroll_inner_panel);
        add(scrollPane, BorderLayout.CENTER);

        /*JButton search_button = new JButton();
        search_button.setToolTipText("搜索");
        JTextField textField = new JTextField();*/

        /*scroll_inner_panel.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                super.componentResized(e);
                scroll_inner_panel.setPreferredSize(new Dimension(scroll_inner_panel.getWidth(), scroll_inner_panel.getHeight()));
                scroll_inner_panel.revalidate();
            }
        });*/
    }

    public void add_search_panel(JTextField textField, JButton button)
    {
        Search_panel search_panel = new Search_panel();
        search_panel.set_text_and_button(textField, button);
        add(search_panel, BorderLayout.NORTH);
    }

    public void add_card(User_friend friend)
    {
        Left_button button = new Left_button(friend);
        this.data.add(button);
        select_button_change(button);
        set_message(button);
        scroll_inner_panel.add(button);
        scroll_inner_panel.setSize(scroll_inner_panel.getWidth(), scroll_inner_panel.getHeight()+40);
    }



    public void add_card(int id, String name, Message_data data, ActionListener listener)
    {
        Left_button button = new Left_button(name, data, listener);
        button.id = id;
        //button.setSize(200, 40);
        scroll_inner_panel.add(button);
        scroll_inner_panel.setSize(scroll_inner_panel.getWidth(), scroll_inner_panel.getHeight()+40);
        //button.setBounds(new Rectangle(10, 10));
    }

    public void add_card(Friend_confirm_card card)
    {
        scroll_inner_panel.add(card);
        scroll_inner_panel.setSize(scroll_inner_panel.getWidth(), scroll_inner_panel.getHeight()+40);
    }

    public static void select_button_change(Left_button new_button)
    {
        select_button = new_button;
    }

    public ArrayList<Left_button> getData()
    {
        return data;
    }
}
