package Server;

import Common.UserDecoder;
import Common.UserEncoder;
import Main_window.Data.Login_data;
import Server.Data.Login_back_data;
import Server.Data.channel_time_data;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.util.HashedWheelTimer;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author: 李子麟
 * @date: 2021/3/19 17:09
 **/
public class Server extends Thread
{
    public static String host = "192.168.137.1";
    public static int[] port = new int[]{10087, 10088, 10089};
    private int mode;
    //private ExecutorService executor;
    private EventLoopGroup boss_group ;
    private EventLoopGroup worker_group;
    private EventExecutorGroup business_group ;


    /**
     *
     * @param mode: mode = 0为登录信息，mode为1是消息， mode2是文件下载
     */
    public Server(int mode)
    {
        this.mode = mode;
        boss_group = new NioEventLoopGroup(1);
        worker_group = new NioEventLoopGroup();
        //business_group = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors()*2);
        //executor = new ThreadPoolExecutor(12, 20, 300,
                //TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
    }

    public void run()
    {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss_group, worker_group);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childHandler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception
                {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                            Integer.MAX_VALUE, 0, 4, 0, 4));
                    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                    pipeline.addLast("decoder", new UserDecoder());
                    pipeline.addLast("encoder", new UserEncoder());
                    pipeline.addLast("handle", new Server_handle(mode));
                }
            });
            //心跳检测的定时任务
            if(mode == 0)
            {
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                service.scheduleAtFixedRate(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Server_main.handle_heart_beat();
                    }
                }, 0, 3 * 60, TimeUnit.SECONDS);
            }
            ScheduledExecutorService service1 = Executors.newSingleThreadScheduledExecutor();
            service1.scheduleAtFixedRate(new Runnable()
            {
                @Override
                public void run()
                {
                    for(int i=Server_handle.temp_save_channel.size()-1; i>=0; i--)
                    {
                        channel_time_data data = Server_handle.temp_save_channel.get(i);
                        data.time_ticks--;
                        if(data.time_ticks == 0)
                        {
                            System.gc();
                            data.channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                            Server_handle.temp_save_channel.remove(i);
                        }
                    }
                }
            }, 0, 1 , TimeUnit.SECONDS);
            try
            {
                ChannelFuture f = b.bind("192.168.137.1", port[mode]).sync();
                f.channel().closeFuture().sync();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally
            {
                worker_group.shutdownGracefully();
                boss_group.shutdownGracefully();
            }

    }



}
