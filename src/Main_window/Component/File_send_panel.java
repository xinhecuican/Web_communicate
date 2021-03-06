package Main_window.Component;

import Interface.IDownload_listener;
import Main_window.Data.Send_data;
import Main_window.Main;
import Main_window.User_Server.User;
import Server.Data.File_info;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author: 李子麟
 * @date: 2021/3/31 20:50
 **/
public class File_send_panel extends JPanel implements IDownload_listener
{
    private File_info file;
    public transient JProgressBar progressBar;
    public JButton button_open;
    public boolean is_finished;
    public File_send_panel(File_info file_info, boolean is_finished)
    {
        file = file_info;
        this.is_finished = is_finished;
        setLayout(new GridBagLayout());
        setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel label_icon = new JLabel(FileSystemView.getFileSystemView().getSystemIcon(new File(file.total_path)));
        label_icon.setSize(20, 20);
        add(label_icon, constraints);
        JLabel file_name = new JLabel(file.file_name);
        constraints.gridwidth = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        add(file_name, constraints);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setValue((int)(100f * ((float)file_info.start_pos / (float)file.file_len)));
        progressBar.setOpaque(false);

        JPanel button_panel = new JPanel();
        button_panel.setOpaque(false);
        button_open = new JButton("打开");
        if(!is_finished)
        {
            button_open.setEnabled(false);
        }
        button_open.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                try
                {
                    Desktop.getDesktop().open(new File(file_info.total_path));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        JButton button_open_dictionary = new JButton("打开目录");
        button_open_dictionary.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                try
                {
                    int last_index = 0;
                    if((last_index = file.total_path.lastIndexOf("\\")) == -1)
                    {
                        last_index = file.total_path.lastIndexOf("/");
                    }
                    Desktop.getDesktop().open(new File(
                            file.total_path.substring(0, last_index).trim()));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        button_panel.add(button_open);
        button_panel.add(button_open_dictionary);
        if(is_finished)
        {
            if(file.my_id != Main.main_user.getId())
                progressBar.setString("下载已完成");
            else
                progressBar.setString("上传已完成");
        }
        if(file.my_id != Main.main_user.getId())
        {
            JButton button_download = new JButton("下载");
            if(is_finished)
            {
                button_download.setEnabled(false);
            }
            button_download.addActionListener(actionEvent ->
            {
                Send_data data = new Send_data();
                if(Main.main_user.find_group(file.send_to_id) != null)
                {
                    data.data_type = Send_data.Data_type.Request_group_file;
                    data.send_to_id = file.send_to_id;//群id
                }
                else
                {
                    data.data_type = Send_data.Data_type.Request_file;
                    data.send_to_id = file.my_id;//发出者id
                }
                data.searched_user = String.valueOf(file.time);
                Main.main_user.send_message(data);
                button_download.setEnabled(false);
            });
            button_panel.add(button_download);
        }
        add(button_panel, constraints);

        add(progressBar, constraints);
    }

    public void set_finished()
    {
        is_finished = true;
        button_open.setEnabled(true);
        if(file.my_id != Main.main_user.getId())
            progressBar.setString("下载已完成");
        else
            progressBar.setString("上传已完成");
        updateUI();
    }

    public String get_file_name()
    {
        return file.file_name;
    }

    public int get_my_id()
    {
        return file.my_id;
    }

    public int get_friend_id() { return file.send_to_id;}

    public long get_time()
    {
        return file.time;
    }

    @Override
    public void On_len_change(int now_len)
    {
        progressBar.setValue((int)(100f * ((float)now_len / (float)file.file_len)));
        progressBar.paintImmediately(0, 0, progressBar.getWidth(), progressBar.getHeight());
    }
}
