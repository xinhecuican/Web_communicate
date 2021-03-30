package Server;

import Main_window.Data.Login_data;
import Main_window.User_Server.test_thread;
import Server.Data.Login_back_data;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;

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
import java.nio.channels.SocketChannel;
import java.util.Iterator;
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
    private EventLoopGroup boss_group = new NioEventLoopGroup();
    private EventLoopGroup worker_group = new NioEventLoopGroup();


    /**
     *
     * @param mode: mode = 0为登录信息，mode为1是单发消息，mode为2是群发消息
     */
    public Server(int mode)
    {
        this.mode = mode;
        //executor = new ThreadPoolExecutor(12, 20, 300,
                //TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
    }

    public void run()
    {
        try
        {
            //ServerSocket socket = new ServerSocket(port[mode]);
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.bind(new InetSocketAddress(port[mode]));
            Selector selector = Selector.open();
            SelectionKey register = socketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while(selector.select() > 0)
            {
                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                while(selectionKeyIterator.hasNext())
                {
                    SelectionKey key = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    if(key.isAcceptable())
                    {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        clientChannel.configureBlocking(false);
                        /**
                         * 成功进行连接，为了可以接收到客户端消息，要给通道读权限
                         */
                        clientChannel.register(selector, SelectionKey.OP_READ, new test_thread());
                    }
                    else if(key.isValid() && key.isReadable())
                    {
                        /*ObjectDecoderInputStream input = null;
                        SocketChannel channel = (SocketChannel)key.channel();
                        ByteBuffer buffer =  ByteBuffer.allocate(1024);
                        ByteBuf buf = Unpooled.buffer(10);
                        while (channel.read(buffer) > 0)
                        {
                            buffer.flip();
                            buf.writeBytes(buffer);
                            buffer.clear();
                        }
                        ByteBufInputStream inputStream = new ByteBufInputStream(buf);
                        input = new ObjectDecoderInputStream(inputStream);
                        Login_data data = null;
                        try
                        {
                            data = (Login_data)input.readObject();
                        }
                        catch (ClassNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                        System.out.println(data.id);*/
                        new test_thread(key).start();
                        //new Server_handle_thread(key, mode).start();
                    }
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
