package Main_window.Pop_window;

import Common.ImgUtils;
import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Main;
import Main_window.Separate_panel.Scroll_panel;
import Main_window.User_Server.Voice_receive_thread;
import Main_window.User_Server.Voice_sender_thread;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: 李子麟
 * @date: 2021/4/3 12:06
 **/
public class Voice_Window extends Pop_window
{
    private MediaPlayer mediaPlayer;
    private Media sound ;
    public JLabel info_label;
    private boolean is_user;
    private int send_id;
    private int send_to_id;
    public static Voice_Window current;
    private Voice_sender_thread sender_thread;
    private Voice_receive_thread receive_thread;
    public boolean is_connect;
    public Voice_Window(JFrame parent, boolean is_user, int sender_id, int send_to_id)
    {
        super(parent);
        if(Scroll_panel.select_button == null)
        {
            dispose();
            return;
        }
        is_connect = false;
        current = this;
        this.is_user = is_user;
        this.send_to_id = send_to_id;
        send_id = sender_id;
        final JFXPanel fxPanel = new JFXPanel();
        setLayout(new BorderLayout());
        JPanel root_panel = new JPanel();
        root_panel.setLayout(new BoxLayout(root_panel, BoxLayout.Y_AXIS));
        add(root_panel);
        sound =  new Media(new File("Resource/Sounds/waiting_sound.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(new Runnable()
        {
            @Override
            public void run()
            {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            }
        });

        JPanel main_panel = new JPanel();

        ImageIcon image = ImgUtils.getIcon("voice_panel.jpg");
        image.setImage(image.getImage().getScaledInstance(75, 100,
                Image.SCALE_AREA_AVERAGING));//这里设置图片的大小和窗口的大小相等

        JLabel picture_label = new JLabel(image);
        picture_label.setSize(75, 100);
        main_panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(20, 40, 20, 40);
        constraints.gridwidth = 0;
        main_panel.add(picture_label, constraints);
        constraints.insets.top = 0;
        constraints.insets.bottom = 0;
        info_label = new JLabel();
        if(is_user)
        {
            info_label.setText("正在等待对方接受");
        }
        else
        {
            info_label.setText(Main.main_user.find_friend(send_id).getName() + "请求与您进行语音通话，是否接受?");
        }
        main_panel.add(info_label, constraints);

        root_panel.add(main_panel);
        JPanel button_panel = new JPanel();
        JButton button_refuse = new JButton("拒绝");
        button_refuse.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                before_close(false);
                dispose();
            }
        });
        button_panel.add(button_refuse);
        if(!is_user)
        {
            JButton button_confirm = new JButton("接受");
            button_confirm.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent actionEvent)
                {
                    receive_thread = new Voice_receive_thread();
                    receive_thread.start();
                    Send_data send_data = new Send_data(send_id, null);
                    send_data.searched_user = String.valueOf(receive_thread.port);
                    send_data.data = new message_rightdata();
                    try
                    {
                        send_data.data.message = InetAddress.getLocalHost().getHostAddress();
                    }
                    catch (UnknownHostException e)
                    {
                        e.printStackTrace();
                    }
                    send_data.data_type = Send_data.Data_type.Accept_voice_call;
                    Main.main_user.send_message(send_data);
                    button_confirm.setEnabled(false);
                }
            });
            button_panel.add(button_confirm);
        }
        root_panel.add(button_panel);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                before_close(false);
            }
        });

        setSize(200, 400);
    }

    public void ban_sound()
    {
        mediaPlayer.stop();
    }

    public void set_info(String text){info_label.setText(text);}

    public void receive_accept_data(Send_data data)
    {
        if(data.my_id != send_id)//朋友发给自己的
        {
            receive_thread = new Voice_receive_thread();
            receive_thread.start();
            sender_thread = new Voice_sender_thread(data.data.message, Integer.parseInt(data.searched_user));
            sender_thread.start();
            set_info("");

            Send_data back_data = new Send_data();
            back_data.data_type = Send_data.Data_type.Accept_voice_call;
            back_data.send_to_id = send_to_id;
            back_data.searched_user = String.valueOf(receive_thread.port);
            back_data.data = new message_rightdata();
            try
            {
                back_data.data.message = InetAddress.getLocalHost().getHostAddress();
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            Main.main_user.send_message(back_data);
            ban_sound();
            is_connect = true;
        }
        else
        {
            sender_thread = new Voice_sender_thread(data.data.message, Integer.parseInt(data.searched_user));
            sender_thread.start();
            set_info("");
            ban_sound();
            is_connect = true;
        }
    }

    public static boolean is_active()
    {
        return current != null && current.isVisible();
    }

    public static AudioFormat get_format()
    {
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED ,
                44100f, 16, 1, 2, 44100f, false);
    }

    public void before_close(boolean is_receive)
    {
        if(is_connect)
        {
            sender_thread.thread_close();
            receive_thread.thread_close();
        }
        ban_sound();
        if(!is_receive)
        {
            Send_data data = new Send_data();
            data.data_type = Send_data.Data_type.Cancel_voice_call;
            if (is_user)
                data.send_to_id = send_to_id;
            else
                data.send_to_id = send_id;
            Main.main_user.send_message(data);
        }
    }
}
