package Main_window.Data;

import Main_window.Data.message_rightdata;

import java.io.Serializable;

/**
 * @author: 李子麟
 * @date: 2021/3/18 13:00
 **/
public class Send_data implements Serializable
{
    /**
     * id = (因为id大于10000）
     *  1: 搜索好友
     *  2: 请求添加好友
     *  3： 好友确认
     */
    public String searched_user = null;
    public int send_to_id;
    public int my_id;//发送方的id
    public message_rightdata data;


    public Send_data()
    {
        send_to_id = -1;
        data = null;
    }

    public Send_data(int id, message_rightdata data)
    {

        send_to_id = id;
        this.data = data;
    }

}
