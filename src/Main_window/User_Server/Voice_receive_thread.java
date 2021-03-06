package Main_window.User_Server;

import Main_window.Pop_window.Voice_Window;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author: 李子麟
 * @date: 2021/4/3 20:40
 **/
public class Voice_receive_thread extends Thread
{
    private AudioFormat format = Voice_Window.get_format();
    private SourceDataLine line;
    private byte[] data;
    public int port = 0;
    private DatagramSocket socket;
    private byte[] mTemp;
    public Voice_receive_thread()
    {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        try
        {
            socket = new DatagramSocket(0);
            port = socket.getLocalPort();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        try
        {
            line = (SourceDataLine) AudioSystem.getLine(info);
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        //int length=(int)(format.getFrameSize()*format.getFrameRate());
        int length = 44100;
        try
        {
            line.open(format);
            line.start();
            while(!isInterrupted())
            {
                data=new byte[length];
                DatagramPacket dp=new DatagramPacket(data, length);

                socket.receive(dp);
                //data = movingAverageFilter(data);
                line.write(data,0,length);
            }
        }
        catch (LineUnavailableException | IOException e)
        {

        }

    }

    public void thread_close()
    {
        socket.close();
        line.close();
        interrupt();
    }

    public byte[] movingAverageFilter(byte[] buf) {
        int bufoutSub = 0;
        int winArraySub = 0;
        byte[] winArray = new byte[10];

        if (mTemp == null) {
            byte[] mBufout = new byte[buf.length - 10 + 1];
            for (int i = 0; i < buf.length; i++) {
                if ((i + 10) > buf.length) {
                    break;
                } else {
                    for (int j = i; j < (10 + i); j++) {
                        winArray[winArraySub] = buf[j];
                        winArraySub = winArraySub + 1;
                    }

                    mBufout[bufoutSub] = mean(winArray);
                    bufoutSub = bufoutSub + 1;
                    winArraySub = 0;
                }
            }
            mTemp = new byte[10 - 1];
            System.arraycopy(buf, buf.length - 10 + 1, mTemp, 0,
                    10 - 1);
            return mBufout;
        } else {
            byte[] bufadd = new byte[buf.length + mTemp.length];
            byte[] mBufout = new byte[bufadd.length - 10 + 1];
            System.arraycopy(mTemp, 0, bufadd, 0, mTemp.length);
            System.arraycopy(buf, 0, bufadd, mTemp.length, buf.length); // 将temp和buf拼接到一块
            for (int i = 0; i < bufadd.length; i++) {
                if ((i + 10) > bufadd.length)
                    break;
                else {
                    for (int j = i; j < (10 + i); j++) {
                        winArray[winArraySub] = bufadd[j];
                        winArraySub = winArraySub + 1;
                    }
                    mBufout[bufoutSub] = mean(winArray);
                    bufoutSub = bufoutSub + 1;
                    winArraySub = 0;
                    System.arraycopy(bufadd, bufadd.length - 10 + 1,
                            mTemp, 0, 10 - 1);
                }
            }
            return mBufout;
        }
    }

    public byte mean(byte[] array) {
        long sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return (byte) (sum / array.length);
    }

}
