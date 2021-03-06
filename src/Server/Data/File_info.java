package Server.Data;

import Interface.IDownload_listener;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/31 18:06
 **/
public class File_info implements Serializable
{
    public String total_path;
    public String file_name;
    public int start_pos;
    public int end_pos;
    public byte[] bytes;
    public long time;
    public int my_id;
    public int file_len;
    public int send_to_id;
    public boolean is_group;


    public File_info(File file, int my_id)
    {
        this.my_id = my_id;
        total_path = file.getAbsolutePath();
        start_pos = 0;
        end_pos = 0;
        file_name = file.getName();
        time = System.currentTimeMillis();
        file_len = (int)file.length();
        is_group = false;
    }

    public File_info(File_info file_info)
    {
        my_id = file_info.my_id;
        send_to_id = file_info.send_to_id;

        total_path = file_info.total_path;
        start_pos = 0;
        end_pos = 0;
        file_name = file_info.file_name;
        time = file_info.time;
        file_len = file_info.file_len;
        is_group = file_info.is_group;
    }


}
