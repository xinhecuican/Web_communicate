package Main_window.User_Server;

import Main_window.Data.Heart_beat_info;
import Main_window.Data.Send_data;
import Main_window.Main;
import Main_window.Pop_window.Add_friend_window;
import Server.Data.Search_back_data;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.ByteBuffer;

/**
 * @author: 李子麟
 * @date: 2021/3/30 17:14
 **/
public class Client_handle extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception
    {
        Search_back_data back_data = (Search_back_data)o;
        if(Add_friend_window.current != null)
        {
            Add_friend_window.current.add_friend_card(back_data);
        }
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
            ctx.channel().writeAndFlush(String.valueOf(Main.main_user.getId()));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        System.out.println("inactive");
    }
}
