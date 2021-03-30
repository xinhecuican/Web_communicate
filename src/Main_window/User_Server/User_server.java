package Main_window.User_Server;

import Server.Server_handle_thread;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
    private ExecutorService executor;

    public User_server()
    {
        executor = new ThreadPoolExecutor(12, 20, 300,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024));
    }
    @Override
    public void run()
    {
        super.run();
        try
        {
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.bind(new InetSocketAddress(0));
            Selector selector = Selector.open();
            SelectionKey register = socketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while(selector.select() > 0)
            {
                Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                while(selectionKeys.hasNext())
                {
                    SelectionKey key = selectionKeys.next();
                    selectionKeys.remove();
                    if(key.isAcceptable())
                    {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(key.selector(), SelectionKey.OP_READ);
                    }
                    else if(key.isReadable())
                    {
                        executor.execute(new User_Server_handle_thread(key));
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
