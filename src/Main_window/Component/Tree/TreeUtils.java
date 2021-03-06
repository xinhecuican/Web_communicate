package Main_window.Component.Tree;

import Main_window.Component.Tree.MyTreeNode;

import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeModel;
import java.awt.Color;

/**
 * Created by SongFei on 2017/11/6.
 */
public class TreeUtils {

    /**
     * 设置Node背景颜色
     *
     * @param model 模型，需要用它来刷新jtree
     * @param node  自定义的MyTreeNode节点
     * @param color 颜色
     */
    public static void setNodeColor(DefaultTreeModel model, MyTreeNode node, Color color) {
        if (model == null || node == null) {
            return;
        }

        JPanel panel = null;

        if (node.getLevel() == 1) {
            panel = (JPanel) node.getGroupView();
        }

        if (node.getLevel() == 2) {
            panel = (JPanel) node.getBuddyView();
        }

        if (panel != null) {
            panel.setBackground(color);
            model.reload(node);
        }
    }

    /**
     * 重置Node节点的Color
     *
     * @param model 模型
     * @param node  自定义Node
     */
    public static void restoreNodeColor(DefaultTreeModel model, MyTreeNode node) {
        if (model == null || node == null) {
            return;
        }
        if (node.getLevel() == 1) {
            node.getGroupView().setBackground(null);
        }
        if (node.getLevel() == 2) {
            node.getBuddyView().setBackground(null);
        }
        model.reload(node);
    }

}