package Main_window.User_Server;

import Main_window.Pop_window.Voice_Window;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.*;

/**
 * @author: 李子麟
 * @date: 2021/4/3 20:28
 **/
public class Voice_sender_thread extends Thread
{
    private int port;
    private String host;
    private TargetDataLine dataLine;
    private AudioFormat format = Voice_Window.get_format();
    private DatagramSocket socket;
    private byte[] data;

    public Voice_sender_thread(String host, int port)
    {
        this.host = host;
        this.port = port;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try
        {
            dataLine = (TargetDataLine) AudioSystem.getLine(info);
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        try
        {
            dataLine.open(format, dataLine.getBufferSize());
            dataLine.start();
            //int length = (int) (format.getFrameSize() * format.getFrameRate() / 2);
            int length = 44100;
            try
            {
                socket = new DatagramSocket();
            }
            catch (SocketException e)
            {
                e.printStackTrace();
            }
            while(!isInterrupted())
            {
                data = new byte[length];
                dataLine.read(data, 0, data.length);
                send_data();
            }

        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    private void send_data()
    {
        try
        {
            DatagramPacket dp = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);
            socket.send(dp);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void thread_close()
    {
        dataLine.close();
        interrupt();
        socket.close();
    }
}
