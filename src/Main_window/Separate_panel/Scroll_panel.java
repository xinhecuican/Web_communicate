package Main_window.Separate_panel;

import Common.ImgUtils;
import Main_window.Component.Friend_confirm_card;
import Main_window.Component.Base_button_card;
import Main_window.Data.Message_data;
import Main_window.Data.User_group;
import Main_window.Listener.Write_message_listener;
import Main_window.Main;
import Main_window.Pop_window.Add_friend_window;
import Main_window.User_Server.User_friend;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static Main_window.Component.Base_button_card.set_message;

/**
 * @author: 李子麟
 * @date: 2021/3/15 20:41
 **/
public class Scroll_panel extends JPanel
{
    private JScrollPane scrollPane;
    private JPanel scroll_inner_panel;
    private ArrayList<Base_button_card> data;
    private ArrayList<Integer> card_id;
    public static Base_button_card select_button;
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JPanel scroll_out_panel;
    private static final Color SELECT_COLOR = new Color(250, 255, 150, 100);
    public Scroll_panel()
    {
        super();
        setBackground(new Color(255, 255, 255));
        setLayout(new BorderLayout());
        scroll_out_panel = new JPanel();
        scroll_out_panel.setOpaque(false);
        scroll_out_panel.setLayout(new BorderLayout());
        scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        data = new ArrayList<Base_button_card>();
        card_id = new ArrayList<>();
        setAutoscrolls(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scroll_inner_panel = new JPanel();
        scroll_inner_panel.setOpaque(false);
        layout = new GridBagLayout();
        scroll_inner_panel.setLayout(layout);
        constraints = new GridBagConstraints();
        constraints.gridwidth = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        //constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.ipady = 50;
        //scroll_inner_panel.setLayout(new BoxLayout(scroll_inner_panel, BoxLayout.Y_AXIS));
        //scroll_inner_panel.setPreferredSize(new Dimension(Main.LEFT_PANEL_WIDTH, 400));
        scrollPane.setViewportView(scroll_inner_panel);
        scroll_out_panel.add(scrollPane, BorderLayout.PAGE_START);
        add(scroll_out_panel, BorderLayout.CENTER);

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

    private void showPopupMenu(Component component, int x, int y)
    {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyMenuItem = new JMenuItem("移除");
        popupMenu.add(copyMenuItem);
        copyMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                Component component1;
                if((component1 = scroll_inner_panel.getComponentAt(x, y)) != null)
                {
                    scroll_inner_panel.remove(component1);
                }

            }
        });
        popupMenu.show(component, x, y);
    }

    public void add_search_panel(JTextField textField, JButton button)
    {
        Search_panel search_panel = new Search_panel();
        search_panel.set_text_and_button(textField, button);
        add(search_panel, BorderLayout.NORTH);
    }

    private boolean is_in_list(int search_id)
    {
        for(Base_button_card base_button_card : this.data)
        {
            if(base_button_card.id == search_id)//在列表中存在时直接切换
            {
                select_button_change(base_button_card);
                /**
                 * TODO: 在scroll_inner_panel中切换到对应位置
                 */
                scroll_inner_panel.getComponentZOrder(base_button_card);
                return true;
            }
        }
        return  false;
    }

    public void add_card(User_friend friend, boolean need_change_button)
    {
        if(is_in_list(friend.getId()))
        {
            return;
        }
        Base_button_card button = new Base_button_card(friend, new Write_message_listener());
        this.data.add(button);
        add_card_id(friend.getId());
        if(need_change_button)
        {
            select_button_change(button);
        }
        scroll_inner_panel.add(button, constraints, 0);
        scroll_inner_panel.setSize(scroll_inner_panel.getWidth(), scroll_inner_panel.getHeight()+40);
        updateUI();
    }

    public void add_card(User_group group, boolean need_change_button)
    {
        if(is_in_list(group.getGroup_id()))
        {
            return;
        }
        Base_button_card button = new Base_button_card(group, new Write_message_listener());
        this.data.add(button);
        add_card_id(group.getGroup_id());
        if(need_change_button)
            select_button_change(button);
        scroll_inner_panel.add(button, constraints, 0);
        scroll_inner_panel.setSize(scroll_inner_panel.getWidth(), scroll_inner_panel.getHeight()+40);
        updateUI();
    }

    public void add_component(JComponent component)
    {
        scroll_inner_panel.add(component, constraints, 0);
        scroll_inner_panel.setSize(scroll_inner_panel.getWidth(), scroll_inner_panel.getHeight()+40);
        updateUI();
    }

    public void add_card(Friend_confirm_card card)
    {
        scroll_inner_panel.add(card, constraints, 0);
        scroll_inner_panel.setSize(scroll_inner_panel.getWidth(), scroll_inner_panel.getHeight()+40);
        updateUI();
    }

    public void remove_all_cards()
    {
        scroll_inner_panel.removeAll();
    }

    private static Color last_time_background;

    public static void select_button_change(Base_button_card new_button)
    {
        if(new_button == select_button)
        {
            User_friend friend;
            if((friend = Main.main_user.find_friend(new_button.id)) != null)
            {
                friend.message_sum_clear();
                new_button.message_sum_clear();
            }
            else
            {
                User_group group = Main.main_user.find_group(new_button.id);
                group.message_sum_clear();
                new_button.message_sum_clear();
            }
            return;
        }
        if(select_button != null)
        {
            User_friend friend;
            select_button.setIcon(null);
            if((friend = Main.main_user.find_friend(new_button.id)) != null)
            {
                friend.message_sum_clear();
                new_button.message_sum_clear();
            }
            else
            {
                User_group group = Main.main_user.find_group(new_button.id);
                group.message_sum_clear();
                new_button.message_sum_clear();
            }
        }
        new_button.setIcon(ImgUtils.getIcon("back7.jpg"));
        select_button = new_button;
        set_message(Scroll_panel.select_button);
    }

    public ArrayList<Base_button_card> getData()
    {
        return data;
    }

    private void add_card_id(int id)
    {
        int low = 0, high = card_id.size() - 1;
        while(low <= high)
        {
            int mid = (low + high) / 2;
            int compare_id = card_id.get(mid);
            if(compare_id > id)
            {
                high = mid - 1;
            }
            else
            {
                low = mid + 1;
            }
        }
        synchronized (this)
        {
            card_id.add(high + 1, id);
        }
    }

    public boolean is_user_in_list(int id)
    {
        int low = 0;
        int high = card_id.size() - 1;
        while(low <= high)
        {
            int mid = (low + high) / 2;
            int compare_id = card_id.get(mid);
            if(compare_id == id)
            {
                return true;
            }
            else if(compare_id > id)
            {
                high = mid - 1;
            }
            else
            {
                low = mid + 1;
            }
        }
        return false;
    }

    public void data_arrive(int id)
    {
        for(Base_button_card card : data)
        {
            if(card.id == id)
            {
                card.message_sum_add();
                break;
            }
        }
    }
}
