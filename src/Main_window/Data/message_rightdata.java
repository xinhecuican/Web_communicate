package Main_window.Data;

import Main_window.Data.color_data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/15 20:56
 **/
public class message_rightdata implements java.io.Serializable
{
        public String time;
        public String message;
        public List<color_data> color_dataList;
        public boolean is_user;
        public message_rightdata(String time, String message, color_data data, boolean is_user)
        {
                this.time = time;
                this.message = message;
                color_dataList = new ArrayList<color_data>();
                color_dataList.add(data);
                this.is_user = is_user;
        }

        public message_rightdata()
        {
                time = "";
                message = "";
                color_dataList = new ArrayList<color_data>();
                is_user = false;
        }

        public message_rightdata(String time, String message, boolean is_user)
        {
                this.time = time;
                this.message = message;
                color_dataList = new ArrayList<color_data>();
                this.is_user = is_user;
        }

}
