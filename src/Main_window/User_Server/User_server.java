package Main_window.User_Server;



import Common.UserDecoder;
import Common.UserEncoder;
import Server.Server;
import Server.Server_handle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: 李子麟
 * @date: 2021/3/18 12:21
 **/
public class User_server extends Thread
{
    public static int receive_port;
    private EventLoopGroup boss_group ;
    private EventLoopGroup worker_group;
    private EventExecutorGroup business_group ;

    public User_server()
    {

        try
        {
            ServerSocket serverSocket = new ServerSocket(0);
            receive_port = serverSocket.getLocalPort();
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        boss_group = new NioEventLoopGroup();
        worker_group = new NioEventLoopGroup();
    }
    @Override
    public void run()
    {
        ServerBootstrap b = new ServerBootstrap();
        b.group(boss_group, worker_group);
        b.channel(NioServerSocketChannel.class);
        b.option(ChannelOption.SO_BACKLOG, 8192);
        b.childHandler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>()
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
                pipeline.addLast("handle", new User_server_handle());
            }
        });
        try
        {
            ChannelFuture f = b.bind(receive_port).sync();
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
