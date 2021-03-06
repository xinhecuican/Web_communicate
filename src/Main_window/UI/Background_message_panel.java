package Main_window.UI;

import Common.ImgUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author: 李子麟
 * @date: 2021/3/31 20:41
 **/
public class Background_message_panel extends JScrollPane
{
    public Background_message_panel(int verticalScrollbarAlways, int horizontalScrollbarNever)
    {
        super(verticalScrollbarAlways, horizontalScrollbarNever);
    }

    public void paintComponent(Graphics g)
    {
        ImageIcon image = ImgUtils.getIcon("back20.jpg");
        image.setImage(image.getImage().getScaledInstance(this.getWidth(),this.getHeight(),
                Image.SCALE_AREA_AVERAGING));//这里设置图片的大小和窗口的大小相等
        g.drawImage(image.getImage(),0,0,this);//在窗口上画出该图片
    }
}
