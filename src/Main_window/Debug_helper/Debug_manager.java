package Main_window.Debug_helper;

import Main_window.Data.Login_data;
import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Main;
import Main_window.Separate_panel.Scroll_panel;
import Main_window.Window;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author: 李子麟
 * @date: 2021/4/7 7:54
 **/
public class Debug_manager
{
    public List<Integer> now_controlled_user;
    public int[] message_sum = new int[20000];
    public static  Debug_manager current;
    public int all_receive_sum=0;
    private static Pattern regex = Pattern.compile("\\s*");//匹配若干个空格
    public int main_user_id;
    int begin_id;
    public Debug_manager()
    {
        current = this;
        main_user_id = Main.main_user.getId();
        now_controlled_user = new ArrayList<>();
    }

    /**
     * 目的： 创建1000个用户并添加好友
     */
    public void create_thousands_user(int num)
    {
        for(int i=0; i<num; i++)
        {
            Login_data data = new Login_data("", 0, Main.main_user.getId(),"","");
            data.type = Login_data.Login_type.debug_create_user;
            Main.main_user.send_login_message(data);
        }
    }

    /**
     * 用来模拟真实情况的发送消息
     */
    public void debug_write_message()
    {
        String text = "test_message: 你好，用户1";
        for(int i=0; i<100; i++)
        {
            if (!(regex.matcher(text).matches() || text.equals(""))
                    && Scroll_panel.select_button != null)//不是空格并且选中一个选项卡
            {
                Window.current.getRight_panel().add_piece_message(new message_rightdata(Window.get_time(), text, Main.main_user.name));
                boolean is_user = Main.main_user.add_message(
                        Scroll_panel.select_button.id,
                        new message_rightdata(Window.get_time(), text, Main.main_user.name)
                );
                Send_data send_data = new Send_data(
                        Scroll_panel.select_button.id,
                        new message_rightdata(Window.get_time(), text, Main.main_user.name)
                );
                send_data.data_type = is_user ?
                        Send_data.Data_type.One_piece_message : Send_data.Data_type.Piece_group_message;
                Main.main_user.send_message(send_data);
                //给对方的，所以is_user = false
            }
        }
    }

    public void handle_send_hundred_message()
    {
        for(int i=0; i<now_controlled_user.size(); i+=100)
        {
            Debug_send_message_thread send_message_thread = new Debug_send_message_thread(i, main_user_id);
            new Thread(send_message_thread).start();
        }
        /*HashedWheelTimer timer = new HashedWheelTimer(1000, TimeUnit.SECONDS);
        TimerTask task = new TimerTask()
        {
            @Override
            public void run(Timeout timeout) throws Exception
            {
                System.out.println(1);
                Window.current.getRight_panel().add_piece_message(
                        new message_rightdata(Window.get_time(), String.valueOf(all_receive_sum), ""));
            }
        };
        timer.newTimeout(task,0, TimeUnit.SECONDS);
        timer.start();*/
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                message_rightdata rightdata = new message_rightdata(Window.get_time(), String.valueOf(all_receive_sum), "");
                Window.current.getRight_panel().add_piece_message(rightdata);
            }
        }, 0, 1,TimeUnit.SECONDS);

    }

    public void add_success(int id)
    {
        for(int i=0; i<now_controlled_user.size(); i++)
        {
            if(now_controlled_user.get(i) == id)
            {
                synchronized (this)
                {
                    all_receive_sum++;
                }
                message_sum[i]++;
                break;
            }
        }
    }
}
