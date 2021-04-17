package Main_window.Component;

import Main_window.Data.Friend_confirm_data;
import Main_window.Data.Send_data;
import Main_window.Main;
import Main_window.Pop_window.Add_friend_window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author: 李子麟
 * @date: 2021/3/21 8:05
 **/
public class Friend_confirm_card extends JPanel
{
    public Friend_confirm_card current;
    public Friend_confirm_card()
    {
        current = this;
    }
    private int id;

    /**
     * 请求添加好友的信息及拒绝添加好友信息
     * mode为0是请求信息，mode为1未搜索到好友的错误信息，2是成功添加好友的提示信息
     */
    public Friend_confirm_card(Friend_confirm_data confirm_data)
    {
        current = this;
        this.addMouseMotionListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                super.mouseEntered(e);
                current.getBackground().darker();
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                super.mouseExited(e);
                current.getBackground().brighter();
            }
        });
        current = this;
        this.id = confirm_data.id;
        setPreferredSize(new Dimension(200, 40));
        setLayout(new BorderLayout());
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isMetaDown()) {
                    showPopupMenu(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        switch(confirm_data.mode)
        {
            case 0:
                JLabel textarea = new JLabel();
                textarea.setText(confirm_data.name + "请求添加你为好友" + "(id:" + String.valueOf(id) + ")");
                JButton button = new JButton("确认");//确认添加好友
                if(confirm_data.is_confirm)
                {
                    button.setEnabled(false);
                }
                button.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent)
                    {
                        Main.main_user.add_friend(id, confirm_data.name);
                        Send_data data = new Send_data(id, null);
                        data.searched_user = Main.main_user.name;
                        data.data_type = Send_data.Data_type.Confirm_add_friend;
                        Main.main_user.send_message(data);
                        confirm_data.is_confirm = true;
                        button.setEnabled(false);
                    }
                });
                button.setOpaque(false);
                add(textarea, BorderLayout.CENTER);
                add(button, BorderLayout.EAST);
                break;
            case 1:
                JLabel label = new JLabel("未搜索到" + confirm_data.name);
                add(label, BorderLayout.CENTER);
                break;
            case 2:
                JLabel label1 = new JLabel("你成功添加" + confirm_data.name + "为好友");
                add(label1, BorderLayout.CENTER);
                break;
            case 3:
                JLabel label2 = new JLabel("成功创建" + confirm_data.name + "(id:" + confirm_data.id + ")");
                add(label2, BorderLayout.CENTER);
            case 4:
                JLabel label3 = new JLabel("成功加入" + confirm_data.name);
                add(label3, BorderLayout.CENTER);
                break;
        }

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
                Add_friend_window.current.remove(current);
                Main.main_user.remove_confirm_card(current.id);
            }
        });
        popupMenu.show(component, x, y);
    }
}
