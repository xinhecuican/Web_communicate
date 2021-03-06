package Main_window.User_Server;

import Main_window.Data.User_group;
import Main_window.Main;
import Server.Data.File_info;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.FutureListener;

import java.io.RandomAccessFile;

/**
 * @author: 李子麟
 * @date: 2021/3/31 19:00
 **/
public class File_handle extends ChannelInboundHandlerAdapter
{
    private File_info info;
    private boolean is_server;
    private RandomAccessFile io;
    private int lastLength;
    public File_handle(File_info file, boolean is_server)
    {
        this.info = file;
        this.is_server = is_server;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        io = new RandomAccessFile(info.total_path, "r");
        io.seek(info.start_pos);
        lastLength = (int) io.length() / 10;
        byte[] bytes = new byte[lastLength];
        int byteRead;
        if ((byteRead = io.read(bytes)) != -1)
        {
            info.end_pos = byteRead;
            info.bytes = bytes;
            ctx.writeAndFlush(info);
        }
        else
        {
            /**
             * TODO: 文件上传完成操作
             */

            io.close();
            if(!is_server)
            {
                Main.main_user.find_friend(info.send_to_id).communicate_data.find_file(info.time).On_len_change(info.file_len);
                Main.main_user.remove_finished_file(info);
                Main.main_user.find_friend(info.send_to_id).set_file_finished(info);
            }
            info.end_pos = -1;
            ctx.writeAndFlush(info).addListener(ChannelFutureListener.CLOSE);
            ctx.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception
    {
        //传输 过来的是这次文件需要开始读的位置
        if(o instanceof Integer)
        {
            Integer start = (Integer)o;
            info.start_pos = start;
            if(!is_server)
            {
                User_friend friend;
                if((friend = Main.main_user.find_friend(info.send_to_id)) != null)
                {
                    friend.communicate_data.find_file(info.time).On_len_change(start);
                }
                else
                {
                    Main.main_user.find_group(info.send_to_id).data.find_file(info.time).On_len_change(start);
                }
            }
            io.seek(start);

            int a = (int) (io.length() - start);
            int b = (int) (io.length() / 10);
            if (a < b) {
                lastLength = a;
            }
            byte[] bytes = new byte[lastLength];
            int byteRead;
            if ((byteRead = io.read(bytes)) != -1 && (io.length() - start) > 0)
            {
                info.end_pos = byteRead;
                info.bytes = bytes;
                try
                {
                    channelHandlerContext.writeAndFlush(info);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {

                io.close();
                if (!is_server)
                {
                    User_friend friend;
                    if(!info.is_group)
                    {
                        friend = Main.main_user.find_friend(info.send_to_id);
                        friend.communicate_data.find_file(info.time).On_len_change(start);
                        friend.set_file_finished(info);
                    }
                    else
                    {
                        User_group group = Main.main_user.find_group(info.send_to_id);
                        group.data.find_file(info.time).On_len_change(start);
                        group.set_file_finished(info);
                    }
                    Main.main_user.remove_finished_file(info);
                }
                info.end_pos = -1;
                channelHandlerContext.writeAndFlush(info).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

}
