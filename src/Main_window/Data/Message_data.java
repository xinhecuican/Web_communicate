package Main_window.Data;

import Main_window.Component.File_send_panel;
import Main_window.Separate_panel.Scroll_panel;
import Main_window.Window;
import Server.Data.File_info;

import java.util.*;

/**
 * @author: 李子麟
 * @date: 2021/3/15 20:47
 **/
public class Message_data implements java.io.Serializable
{
    private List<message_rightdata> dataList;
    private List<File_panel_data> downloading_files;
    public transient List<File_send_panel> panel_data;


    public Message_data()
    {
        dataList = new ArrayList<message_rightdata>();
        downloading_files = new ArrayList<File_panel_data>();
        panel_data = new ArrayList<>();
    }

    public Message_data(message_rightdata data)
    {
        dataList = new ArrayList<message_rightdata>();
        downloading_files = new ArrayList<>();
        dataList.add(data);
        panel_data = new ArrayList<>();
    }

    public void add(message_rightdata data)
    {
        dataList.add(data);
    }

    public boolean is_empty()
    {
        return dataList.isEmpty();
    }

    public message_rightdata recently_message()
    {
        return dataList.get(dataList.size() - 1);
    }

    public boolean remove(int index)
    {
        if (dataList.isEmpty())
        {
            return false;
        }
        dataList.remove(index);
        return true;
    }

    public  boolean remove(message_rightdata data)
    {
        if (dataList.isEmpty())
        {
            return false;
        }
        dataList.remove(data);
        return true;
    }

    public List<message_rightdata> get_datalist()
    {
        return dataList;
    }

    public File_send_panel add(File_info info)
    {
        File_send_panel file_send_panel = new File_send_panel(info, false);
        if(panel_data == null)
        {
            panel_data = new ArrayList<>();
        }
        downloading_files.add(new File_panel_data(info, false));
        panel_data.add(file_send_panel);
        return file_send_panel;
    }

    public void set_file_finished(File_info finished)
    {
        for (File_panel_data file : downloading_files)
        {
            if(file.file_info.time == finished.time)
            {
                file.is_finished = true;
                file.file_info.file_name = finished.file_name;
                file.file_info.file_len = finished.file_len;
                break;
            }
        }
    }

    public File_send_panel find_file(long time)
    {
        for(File_send_panel file : panel_data)
        {
            if(file.get_time() == time)
            {
                return file;
            }
        }
        return null;
    }

    public File_panel_data find_data(long time)
    {
        for(File_panel_data file : downloading_files)
        {
            if(file.file_info.time == time)
            {
                return file;
            }
        }
        return null;
    }

    public File_send_panel create_file_panel(long time)
    {
        if(panel_data == null)
        {
            panel_data = new ArrayList<>();
        }
        File_panel_data data = find_data(time);
        File_send_panel send_panel = new File_send_panel(data.file_info, data.is_finished);
        panel_data.add(send_panel);
        return new File_send_panel(data.file_info, data.is_finished);
    }

}
