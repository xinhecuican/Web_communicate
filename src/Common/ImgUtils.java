package Common;

import javax.swing.*;

/**
 * @author: 李子麟
 * @date: 2021/3/25 17:07
 **/
public class ImgUtils
{
    public static ImageIcon getIcon(String name)
    {
        ImageIcon icon = new ImageIcon("Resource/Picture/" + name);
        return icon;
    }
}
