package Main_window.Debug_helper;

import Common.UserDecoder;
import Common.UserEncoder;
import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Main;
import Main_window.User_Server.Client_handle;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: 李子麟
 * @date: 2021/4/7 8:15
 **/
public class Debug_send_message_thread implements Runnable
{
    public int begin_id;
    public ArrayList<Integer> user_list;
    public int main_user_id;
    private Channel channels;
    public Debug_send_message_thread(int i, int main_user_id)
    {
        begin_id = i;
        this.main_user_id = main_user_id;
        user_list = new ArrayList<>();
        for(int k=0; k<100; k++)
        {
            user_list.add(Debug_manager.current.now_controlled_user.get(i+k));
        }


    }
    @Override
    public void run()
    {
        Bootstrap message_bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        message_bootstrap.group(group).channel(NioSocketChannel.class);
        message_bootstrap.handler(new ChannelInitializer<Channel>()
        {
            @Override
            protected void initChannel(Channel channel) throws Exception
            {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new UserDecoder());
                pipeline.addLast("encoder", new UserEncoder());
                pipeline.addLast("handler", new Client_handle());
            }
        });
        message_bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        try
        {

            channels = message_bootstrap.connect("192.168.137.1", 10088).sync().channel();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        for(int i=0; i<100; i++)
        {
            for(int k=0; k<100; k++)
            {
                Send_data data = new Send_data(main_user_id,
                        new message_rightdata("", "你好，用户1", ""));
                data.my_id = user_list.get(i);
                data.data_type = Send_data.Data_type.debug_send_message;
                channels.writeAndFlush(data);
            }
            try
            {
                Thread.sleep((long) (Math.random() * 20 + 100));
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            channels.close();
        }
        finally
        {
            group.shutdownGracefully();
        }
    }
}
