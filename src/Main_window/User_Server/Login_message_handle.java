package Main_window.User_Server;

import Main_window.Data.Heart_beat_info;
import Main_window.Data.Send_data;
import Main_window.Login_window;
import Main_window.Main;
import Main_window.Window;
import Server.Data.Login_back_data;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: 李子麟
 * @date: 2021/3/30 17:19
 **/
public class Login_message_handle extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception
    {
        Login_back_data back_data = (Login_back_data)o;
        Main.main_user.accept_login_data(back_data);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        ctx.close();
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        if(evt instanceof IdleStateEvent)
        {
            ctx.writeAndFlush(new Heart_beat_info(Main.main_user.getId()));
        }
    }
}
