package Server;

import Common.UserDecoder;
import Common.UserEncoder;
import Main_window.Data.Login_data;
import Server.Data.Login_back_data;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
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
     * @param mode: mode = 0为登录信息，mode为1是单发消息，mode为2是群发消息
     */
    public Server(int mode)
    {
        this.mode = mode;
        boss_group = new NioEventLoopGroup();
        worker_group = new NioEventLoopGroup();
        business_group = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors()*2);
        //executor = new ThreadPoolExecutor(12, 20, 300,
                //TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
    }

    public void run()
    {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss_group, worker_group);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 2048);
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
                    pipeline.addLast(business_group, new Server_handle(mode));
                }
            });
            //心跳检测的定时任务
            HashedWheelTimer timer = new HashedWheelTimer(180, TimeUnit.SECONDS);
            TimerTask task = new TimerTask()
            {
                @Override
                public void run(Timeout timeout) throws Exception
                {
                    Server_main.handle_heart_beat();
                }
            };
            timer.newTimeout(task, 10, TimeUnit.SECONDS);
            timer.start();
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
