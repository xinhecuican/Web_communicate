package Common;

import io.netty.buffer.ByteBuf;

/**
 * @author: 李子麟
 * @date: 2021/3/30 12:24
 **/
public class ByteBufToBytes {

    public static byte[] read(ByteBuf datas) {
        byte[] bytes = new byte[datas.readableBytes()];
        datas.readBytes(bytes);
        return bytes;
    }
}
