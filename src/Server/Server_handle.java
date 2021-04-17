package Server;

import Common.UserDecoder;
import Common.UserEncoder;
import Main_window.Data.*;
import Main_window.User_Server.File_handle;
import Main_window.Window;
import Server.Data.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: 李子麟
 * @date: 2021/3/30 12:50
 **/
public class Server_handle extends ChannelInboundHandlerAdapter
{
    private int mode;
    public static Bootstrap send_message_bootstrap = create_send_data_bootstrap();
    public static List<channel_time_data> temp_save_channel = new CopyOnWriteArrayList<>();
    private static EventLoopGroup event_group;
    public Server_handle(int mode)
    {
        this.mode = mode;


    }


    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        ctx.close();
    }
    /**
     *
     * @param channelHandlerContext
     * @param o
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception
    {
        Channel channel = channelHandlerContext.channel();
        if(o instanceof String)//心跳检测
        {
            String heart_beat_message = (String)o;
            System.out.println(heart_beat_message);
            Objects.requireNonNull(Server_main.search_user(Integer.parseInt(heart_beat_message))).heart_beat_test = true;
            return;
        }
        switch (mode)
        {
            case 0://登录信息
                try
                {
                    Login_data data = (Login_data)o;
                    switch(data.type)
                    {
                        case Request_friend_message:
                            //返回id为0，并且is_error = true
                            int user_id = Integer.parseInt(data.name);
                            User_message message = Server_main.search_user(user_id);
                            assert message != null;
                            Login_back_data back_data = new Login_back_data(
                                    Login_back_data.Login_type.Friend_info, new ArrayList<Send_data>());
                            for (Integer integer : message.friend_id)
                            {
                                Send_data send_data = new Send_data();
                                send_data.send_to_id = integer;
                                send_data.searched_user = Objects.requireNonNull(Server_main.search_user(integer)).user_name;
                                back_data.storage_data.add(send_data);
                            }
                            write_back(channel, back_data);
                            break;
                        case Offline:
                            int user_id1 = Integer.parseInt(data.name);
                            Server_main.offline(user_id1);
                            break;
                        case Register:
                        case Login:
                            //登录及注册
                            int id = Server_main.Login(data);//包含了注册的情况，如果是注册，返回注册id
                            if (id == -1) //出错
                            {
                                Login_back_data back_data1 = new Login_back_data(
                                        Login_back_data.Login_type.Error, null);
                                back_data1.id = 1;
                                write_back(channel, back_data1);
                            }
                            else if(id == -2)//已经登录
                            {
                                Login_back_data back_data1 = new Login_back_data(
                                        Login_back_data.Login_type.Error, null);
                                back_data1.id = 2;
                                write_back(channel, back_data1);
                            }
                            else if (id == 0)//正常登录
                            {
                                User_message user_message = Server_main.search_user(data.id);
                                user_message.heart_beat_test = true;
                                Login_back_data login_back_data = new Login_back_data(
                                        Login_back_data.Login_type.Login, user_message.data);
                                if (Server_main.heart_test_user == null || !Server_main.heart_test_user.is_online)//心跳检测
                                {
                                    Server_main.heart_test_user = user_message;
                                    login_back_data.type = Login_back_data.Login_type.Login_And_Heart;
                                }
                                login_back_data.id = data.id;
                                login_back_data.name = user_message.user_name;
                                write_back(channel, login_back_data);
                            }
                            else//注册
                            {
                                Login_back_data login_back_data = new Login_back_data(
                                        Login_back_data.Login_type.Register, null);
                                login_back_data.name = data.name;
                                login_back_data.id = id;
                                write_back(channel, login_back_data);
                            }
                            break;
                        case debug_create_user:
                            int debug_id = Server_main.Login(data);
                            Login_back_data back_data1 = new Login_back_data(Login_back_data.Login_type.debug_create_back
                            ,null);
                            back_data1.name = String.valueOf(debug_id);
                            back_data1.id = debug_id;
                            write_back(channel, back_data1);
                            break;
                    }
                    break;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            case 1: //单人发送信息
                try
                {
                    Send_data data = (Send_data)o;
                    switch (data.data_type)
                    {
                        case Confirm_add_friend://确认添加好友,其中send_to_id是申请添加的人的id，my_id是好友id
                            String friend_name = Server_main.add_friend(data.send_to_id, data.my_id);
                            data.searched_user = friend_name;
                            Server_main.add_message(data.send_to_id, data);
                            break;
                        case Request_add_friend://请求添加好友,自己的名字在searched_user字段中
                            if (!Server_main.add_message(data.send_to_id, data))//处理未找到该用户的情况，找到了直接在函数中发送
                            {
                                data.data_type = Send_data.Data_type.Request_add_fail;
                                data.searched_user = Objects.requireNonNull(Server_main.search_user(data.send_to_id)).user_name;
                                User_message message = Server_main.search_user(data.my_id);
                                assert message != null;
                                Server_main.add_message(message.get_id(), data);
                            }
                            break;
                        case Search_friend:
                            List<User_message> send_data = Server_main.search_user(data.searched_user);
                            List<Group_message> group_data = Server_main.search_group(data.searched_user);
                            if (send_data.size() > 0 || group_data.size() > 0)
                            {
                                Search_back_data search_back_data = new Search_back_data();
                                for (User_message message : send_data)
                                {
                                    search_back_data.add(message);
                                }
                                for(Group_message message : group_data)
                                {
                                    search_back_data.add(message.getGroup_id(), message.group_name, true);
                                }
                                write_back(channel, search_back_data);
                            }
                            else//没有搜索到
                            {
                                data.data_type = Send_data.Data_type.Search_fail;
                                Server_main.add_message(data.my_id, data);
                            }
                            break;
                        case One_piece_message://单个好友之间发送信息
                            Server_main.add_message(data.send_to_id, data);
                            break;
                        case Create_group_message:
                            int id = Server_main.create_group(data);
                            User_message main_user = Server_main.search_user(data.my_id);
                            data.send_to_id = id;
                            assert main_user != null;
                            Server_main.add_message(main_user.get_id(), data);
                            break;
                        case Request_add_group:
                            Server_main.join_group(data.send_to_id, data);
                            data.data_type = Send_data.Data_type.Add_group_successful;
                            data.searched_user = Objects.requireNonNull(Server_main.search_group(data.send_to_id)).group_name;
                            Server_main.add_message(data.my_id, data);
                            break;
                        case Piece_group_message:
                            Server_main.add_group_message(data.send_to_id, data);
                            break;
                        case Request_file:
                            User_message user = Server_main.search_user(data.send_to_id);
                            long time = Long.parseLong(data.searched_user);
                            for(int i=user.files.size()-1; i>=0; i--)
                            {
                                if(user.files.get(i).time == time)
                                {
                                    Server_main.add_message(data.my_id, user.files.get(i));
                                    break;
                                }
                            }
                            break;
                        case Request_group_file:
                            Group_message group = Server_main.search_group(data.send_to_id);
                            long time_group = Long.parseLong(data.searched_user);
                            for(File_info file : group.files)
                            {
                                if(file.time == time_group)
                                {
                                    File_info file_info = new File_info(file);
                                    Server_main.add_message(data.my_id, file_info);
                                    break;
                                }
                            }
                            break;
                        case Request_voice_call:
                            User_message search_user = Server_main.search_user(data.send_to_id);
                            if(!search_user.is_online)
                            {
                                Send_data data1 = new Send_data();
                                data1.data_type = Send_data.Data_type.Request_voice_fail;
                                Server_main.add_message(data.my_id, data1);
                                break;
                            }
                            Server_main.add_message(data.send_to_id, data);
                            break;
                        case Accept_voice_call:
                        case Cancel_voice_call:
                            Server_main.add_message(data.send_to_id, data);
                            break;
                        case debug_send_message:
                            Server_main.add_message(data.send_to_id, data);
                            //data = null;
                            break;
                    }
                    break;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            case 2://负责文件传输
                if(o instanceof File_info)
                {
                    File_info file = (File_info)o;


                    byte[] bytes = file.bytes;

                    String file_path = Server_main.Server_root_path + String.valueOf((file.is_group ? file.send_to_id : file.my_id))
                            + "/" + String.format("%013d", file.time) + file.file_name;
                    if(file.end_pos == -1)//文件传输完成
                    {

                        file.total_path = file_path;
                        file.start_pos = 0;
                        file.end_pos = 0;
                        file.bytes = null;

                        Send_data send_data = new Send_data();
                        send_data.my_id = file.my_id;
                        send_data.send_to_id = file.send_to_id;
                        send_data.searched_user = file.file_name;
                        send_data.data = new message_rightdata("", String.valueOf(file.time), String.valueOf(file.file_len));
                        send_data.data_type = Send_data.Data_type.File_arrive;

                        User_message user;
                        if(file.is_group)//发送群组消息
                        {
                            send_data.data_type = Send_data.Data_type.Group_file_arrive;
                            Group_message group = Server_main.search_group(file.send_to_id);
                            assert group != null;
                            group.files.add(file);
                            Server_main.add_group_message(file.send_to_id, send_data);
                            break;
                        }
                        user = Server_main.search_user(file.my_id);
                        user.files.add(file);
                        User_message friend = Server_main.search_user(file.send_to_id);
                        assert friend != null;
                        Server_main.add_message(friend.get_id(), send_data);
                        break;
                    }
                    File download_file_directory = new File(Server_main.Server_root_path +
                            String.valueOf(file.is_group ? file.send_to_id : file.my_id));
                    if(!download_file_directory.isDirectory())
                    {
                        download_file_directory.mkdirs();
                    }
                    /*File download_file = new File(file_path);
                    if(!download_file.exists())
                    {
                        download_file.createNewFile();
                    }*/
                    RandomAccessFile io = new RandomAccessFile(file_path, "rw");
                    io.seek(file.start_pos);
                    io.write(bytes);
                    io.close();
                    channel.writeAndFlush(file.start_pos + file.end_pos);


                }
                break;
        }
        ReferenceCountUtil.release(o);
    }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ChannelFuture channelFuture = ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    private static void write_back(Channel channel, Object data) throws InterruptedException
    {
        channel.writeAndFlush(data).addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception
            {
                if(data instanceof Login_back_data)
                {
                    Login_back_data back_data = (Login_back_data)data;
                    if (back_data.type == Login_back_data.Login_type.Login && back_data.storage_data != null)
                    {
                        back_data.storage_data.clear();
                    }
                }
            }
        });
    }

    public static void send_data(String host, int port, Object data)
    {
        if(send_message_bootstrap == null)
        {
            send_message_bootstrap = create_send_data_bootstrap();
        }
        Channel channel = null;
        /*try
        {
            channel = send_message_bootstrap.connect(host, port).sync().channel();
            ChannelFuture future = channel.writeAndFlush(data);
            Channel finalChannel = channel;
            future.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception
                {
                    finalChannel.close();
                }
            });
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            channel.close();
        }
        */
        find_channel(host, port).writeAndFlush(data);

            /*ChannelFuture future = channel.writeAndFlush(data);
            future.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception
                {
                    channelFuture.channel().close();
                }
            });*/
    }

    public static void send_file(String host, int port, Object data)
    {
        File_info file = (File_info)data;
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new UserDecoder());
                pipeline.addLast("encoder", new UserEncoder());
                pipeline.addLast("handler", new File_handle(file, true));
            }
        });
        b.option(ChannelOption.SO_KEEPALIVE, true);
        Channel channel = null;
        try
        {
            channel = b.connect(host, port).sync().channel();
            channel.closeFuture().sync();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            group.shutdownGracefully();
        }
    }

    public static Bootstrap create_send_data_bootstrap()
    {
        event_group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(event_group).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new UserDecoder());
                pipeline.addLast("encoder", new UserEncoder());
            }
        });
        b.option(ChannelOption.SO_KEEPALIVE, true);
        return b;
    }

    private static synchronized Channel find_channel(String host, int port)
    {
        for (channel_time_data data : temp_save_channel)
        {
            if (data.host == host && data.port == port)
            {
                data.time_ticks = 2;
                return data.channel;
            }
        }
        Channel channel = null;
        try
        {
            channel = send_message_bootstrap.connect(host, port).sync().channel();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        temp_save_channel.add(new channel_time_data(2, channel, host, port));
        return channel;
    }

    public static void debug_release_channel()
    {
        for(channel_time_data data : temp_save_channel)
        {
            data.channel.close();
        }
        temp_save_channel.clear();
        event_group.shutdownGracefully();
        System.gc();
    }
}
