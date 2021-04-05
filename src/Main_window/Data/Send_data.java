package Main_window.Data;

import Main_window.Data.message_rightdata;

import java.io.Serializable;

/**
 * @author: 李子麟
 * @date: 2021/3/18 13:00
 **/
public class Send_data implements Serializable, Cloneable
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
    public Data_type data_type;


    public Send_data()
    {
        send_to_id = -1;
        data = null;
        data_type = Data_type.None;
    }

    public Send_data(int id, message_rightdata data)
    {

        send_to_id = id;
        this.data = data;
    }

    public enum Data_type
    {
        None,
        Search_friend,
        Request_add_friend,
        Confirm_add_friend,
        Request_add_fail,
        Search_fail,

        One_piece_message,//一条朋友之间的消息
        Create_group_message,
        Add_group_successful,
        Request_add_group,
        Piece_group_message,//一条群发消息

        File_arrive,
        Request_file,

        Request_voice_call,
        Request_voice_fail,
        Cancel_voice_call,
        Accept_voice_call
    }
}
