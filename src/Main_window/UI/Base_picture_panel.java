package Main_window.UI;

import Common.ImgUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author: 李子麟
 * @date: 2021/4/12 18:37
 **/
public class Base_picture_panel extends JPanel
{
    ImageIcon image;
    public Base_picture_panel(String picture_name)
    {
        image = ImgUtils.getIcon(picture_name);
    }
    public void paintComponent(Graphics g)
    {
        image.setImage(image.getImage().getScaledInstance(this.getWidth(),this.getHeight(),
                Image.SCALE_AREA_AVERAGING));//这里设置图片的大小和窗口的大小相等
        g.drawImage(image.getImage(),0,0,this);//在窗口上画出该图片
    }
}
