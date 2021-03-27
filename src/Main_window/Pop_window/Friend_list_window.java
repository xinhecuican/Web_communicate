package Main_window.Pop_window;

import Common.ImgUtils;
import Main_window.Component.Tree.MyTreeNode;
import Main_window.Component.Tree.MyTreeUI;
import Main_window.Component.Tree.TreeUtils;
import Main_window.Data.Tree_data;
import Main_window.Data.User_group;
import Main_window.Listener.Show_card_listener;
import Main_window.Main;
import Main_window.Separate_panel.Scroll_panel;
import Main_window.User_Server.User_friend;
import Main_window.Window;
import Server.Data.Group_message;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/21 19:09
 **/
public class Friend_list_window extends Pop_window
{
    /*public Friend_list_window(JFrame frame)
    {
        super(frame);
        Scroll_panel root_panel = new Scroll_panel();
        add(root_panel);
        List<User_friend> all_friends = Main.main_user.get_all_friends();
        for(User_friend friend : all_friends)
        {
            root_panel.add_card(friend.getId(), friend.name, friend.communicate_data, new Show_card_listener());
        }
        setPreferredSize(new Dimension(200, 600));
        pack();
    }*/
    /**
     * 鼠标滑过
     */
    private Color HOVER_COLOR = new Color(200, 200, 200, 100);

    /**
     * 鼠标点击
     */
    private Color SELECT_COLOR = new Color(160, 160, 160, 100);

    private JPanel jPanel;

    private JTree jTree;

    private DefaultMutableTreeNode root;
    private DefaultTreeModel model;

    private MyTreeNode selectNode;
    private MyTreeNode hoverNode;

    public Friend_list_window(JFrame parent)
    {
        super(parent);

        initGUI();
    }

    private void initGUI() {
        setSize(350, 575);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        //setUndecorated(true);
        //setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        jPanel = new JPanel();
        getContentPane().add(jPanel, BorderLayout.CENTER);

        root = new DefaultMutableTreeNode();
        model = new DefaultTreeModel(root);

        supply_tree();

        jTree = new JTree(model);
        jTree.setUI(new MyTreeUI());

        jTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverNode == null) {
                    return;
                }
                if (hoverNode != selectNode) {
                    TreeUtils.restoreNodeColor(model, hoverNode);
                }
                hoverNode = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                TreePath path = jTree.getSelectionPath();
                if (path == null) {
                    return;
                }
                MyTreeNode node = (MyTreeNode) path.getLastPathComponent();
                if (node == null) {
                    return;
                }
                // 除了好友节点，其他节点都没有点击选中功能
                if (node.getLevel() != 2) {
                    return;
                }
                // 避免重复点击
                if (node == selectNode) {
                    User_friend friend;
                    if((friend = Main.main_user.find_friend(node.getId())) != null)
                    {
                        Window.current.getScroll_panel().add_card(friend);
                    }
                    else
                    {
                        Window.current.getScroll_panel().add_card(Main.main_user.find_group(node.getId()));
                    }
                    Window.current.requestFocus();
                    return;
                }
                TreeUtils.restoreNodeColor(model, selectNode);
                TreeUtils.setNodeColor(model, node, SELECT_COLOR);
                selectNode = node;
            }
        });

        jTree.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                TreePath path = jTree.getPathForLocation(e.getX(), e.getY());
                if (path == null) {
                    return;
                }
                MyTreeNode node = (MyTreeNode) path.getLastPathComponent();
                if (node == null) {
                    return;
                }
                // 同一个节点，避免重复hover
                if (node == hoverNode) {
                    return;
                }
                // 点击了一个节点之后，select == hover，此时再次滑动时就会导致将select的颜色变成hover的颜色了
                // select的权重高，已经select了，就不再hover
                if (hoverNode != selectNode) {
                    TreeUtils.restoreNodeColor(model, hoverNode);
                }
                // hover到了select节点，就不再hover
                if (node == selectNode) {
                    hoverNode = node;// 必须记录一下，否则重复滑动不了
                    return;
                }
                TreeUtils.setNodeColor(model, node, HOVER_COLOR);
                hoverNode = node;
            }
        });

        jPanel.add(jTree);
    }

    public void supply_tree()
    {
        List<Tree_data> data = Main.main_user.get_friend_list_data();
        for(Tree_data tree_data : data)
        {
            MyTreeNode cate = new MyTreeNode(ImgUtils.getIcon("arrow_left.png"), tree_data.getName());
            for(Integer list_id : tree_data.supply_tree())
            {
                User_friend friend  ;
                if((friend = Main.main_user.find_friend(list_id)) != null)
                {
                    cate.add(new MyTreeNode(friend.getId(), ImgUtils.getIcon("avatar.png"), friend.getName(), friend.nick));
                }
                else
                {
                    User_group group = Main.main_user.find_group(list_id);
                    cate.add(new MyTreeNode(group.getGroup_id(), ImgUtils.getIcon("avatar.png"), group.getGroup_name(), ""));
                }
            }
            root.add(cate);
        }
    }
}
