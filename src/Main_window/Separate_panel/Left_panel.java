package Main_window.Separate_panel;

import Main_window.Data.Message_data;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author: 李子麟
 * @date: 2021/3/15 20:41
 **/
public class Left_panel extends JPanel
{
    private JScrollPane scrollPane;
    private JPanel scroll_inner_panel;
    private ArrayList<Friend> data;
    public static Friend select_button;
    public Left_panel()
    {

        super();
        setLayout(new BorderLayout());
        scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        data = new ArrayList<Friend>();
        setAutoscrolls(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scroll_inner_panel = new JPanel();
        scroll_inner_panel.setLayout(new BoxLayout(scroll_inner_panel, BoxLayout.Y_AXIS));
        //scroll_inner_panel.setPreferredSize(new Dimension(Main.LEFT_PANEL_WIDTH, 400));
        scrollPane.setViewportView(scroll_inner_panel);
        add(scrollPane, BorderLayout.CENTER);
        Search_panel search_panel = new Search_panel();
        add(search_panel, BorderLayout.NORTH);
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



    public void add_card(String host, int port, String name, Message_data data)
    {
        Friend button = new Friend(host, port, name, data);
        button.setSize(200, 40);
        this.data.add(button);

        scroll_inner_panel.add(button);

        scroll_inner_panel.setSize(scroll_inner_panel.getWidth(), scroll_inner_panel.getHeight()+40);
        select_button = button;

        button.set_message();
        //button.setBounds(new Rectangle(10, 10));
    }

    public ArrayList<Friend> getData()
    {
        return data;
    }
}
