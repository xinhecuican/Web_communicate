package Common;

import Main_window.User_Server.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author: 李子麟
 * @date: 2021/3/30 12:26
 **/
public class UserEncoder<T> extends MessageToByteEncoder<T>
{

    @Override
    protected void encode(ChannelHandlerContext ctx, T user, ByteBuf out) throws Exception {
        byte[] datas = ByteObjConverter.objectToByte(user);
        out.writeBytes(datas);
        ctx.flush();
    }

}
