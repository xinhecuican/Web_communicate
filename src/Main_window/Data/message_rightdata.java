package Main_window.Data;

import Main_window.Data.color_data;

import javax.swing.text.StyledDocument;
import java.util.ArrayList;
import java.util.List;

/**单个要发送的信息
 * @author: 李子麟
 * @date: 2021/3/15 20:56
 **/
public class message_rightdata implements java.io.Serializable
{
        public String time;
        public String message;
        public List<color_data> color_dataList;
        public String message_sender_name;
        public boolean is_file;
        public message_rightdata(String time, String message, color_data data)
        {
                this.time = time;
                this.message = message;
                color_dataList = new ArrayList<color_data>();
                color_dataList.add(data);
                is_file = false;
        }

        public message_rightdata()
        {
                time = "";
                message = "";
                color_dataList = new ArrayList<color_data>();
                is_file = false;
        }

        public message_rightdata(String time, String message, String message_sender_name)
        {
                this.time = time;
                this.message = message;
                color_dataList = new ArrayList<color_data>();
                this.message_sender_name = message_sender_name;
                is_file = false;
        }

}
