package Main_window.Data;

import java.io.Serializable;

/**
 * @author: 李子麟
 * @date: 2021/3/22 17:53
 **/
public class Friend_confirm_data implements Serializable
{
    public int id;
    public String name;
    public int mode;
    public boolean is_confirm;

    public Friend_confirm_data(int id, String name, int mode)
    {
        this.id = id;
        this.name = name;
        this.mode = mode;
        is_confirm = false;
    }
}
