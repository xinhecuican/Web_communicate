package Common;

import Main_window.User_Server.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.Serializable;

/**
 * @author: 李子麟
 * @date: 2021/3/30 12:26
 **/
public class UserEncoder extends MessageToByteEncoder<Serializable>
{
    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable user, ByteBuf out) throws Exception {
        byte[] datas = ByteObjConverter.objectToByte(user);
        out.writeBytes(datas);
        datas = null;
        ctx.flush();
    }

}
