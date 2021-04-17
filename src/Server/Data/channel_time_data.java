package Server.Data;

import io.netty.channel.Channel;

/**
 * @author: 李子麟
 * @date: 2021/4/7 15:32
 **/
public class channel_time_data
{
    public int time_ticks;
    public String host;
    public int port;
    public Channel channel;
    public channel_time_data(int time_ticks, Channel channel, String host, int port)
    {
        this.time_ticks = time_ticks;
        this.channel = channel;
        this.host = host;
        this.port = port;
    }
}
