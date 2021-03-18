package Main_window.Data;

import Main_window.Data.color_data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/15 20:56
 **/
public class message_rightdata
{
        public String time;
        public String message;
        public List<color_data> color_dataList;
        public message_rightdata(String time, String message, color_data data)
        {
                this.time = time;
                this.message = message;
                color_dataList = new ArrayList<color_data>();
                color_dataList.add(data);
        }

        public message_rightdata()
        {
                time = "";
                message = "";
                color_dataList = new ArrayList<color_data>();
        }

        public message_rightdata(String time, String message)
        {
                this.time = time;
                this.message = message;
                color_dataList = new ArrayList<color_data>();
        }

}