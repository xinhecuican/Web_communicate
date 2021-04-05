package Main_window.Data;

import Server.Data.File_info;

import java.io.Serializable;

/**
 * @author: 李子麟
 * @date: 2021/4/2 14:34
 **/
public class File_panel_data implements Serializable
{
    public boolean is_finished;
    public File_info file_info;
    public File_panel_data(File_info file_info, boolean is_finished)
    {
        this.file_info = file_info;
        this.is_finished = is_finished;
    }
}
