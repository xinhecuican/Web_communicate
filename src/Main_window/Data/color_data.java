package Main_window.Data;

import java.awt.*;

/**
 * @author: 李子麟
 * @date: 2021/3/15 20:57
 **/
public class color_data implements java.io.Serializable
{
    private int begin_index;
    private int end_index;
    private Color color;

    public color_data(int begin, int end, Color color)
    {
        begin_index = begin;
        end_index = end;
        this.color = color;
    }

    public color_data()
    {
        begin_index = 0;
        end_index = 0;
        color = null;
    }
}
