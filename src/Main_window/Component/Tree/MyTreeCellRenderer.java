package Main_window.Component.Tree;

import Common.ImgUtils;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

/**
 * Created by SongFei on 2017/11/6.
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -3617708634867111249L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        MyTreeNode node = (MyTreeNode) value;

        // 根节点从0开始，依次往下
        // 一级节点
        if (node.getLevel() == 1) {
            if (expanded) {
                node.getIconLabel().setIcon(ImgUtils.getIcon("arrow_down.png"));
            } else {
                node.getIconLabel().setIcon(ImgUtils.getIcon("arrow_left.png"));
            }
            return node.getGroupView();
        }

        // 二级节点
        if (node.getLevel() == 2) {
            node.getIconLabel().setIcon(ImgUtils.getIcon("avatar.png"));
            return node.getBuddyView();
        }

        return this;
    }

}
