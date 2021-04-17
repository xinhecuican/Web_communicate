package Common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/30 12:25
 **/
public class UserDecoder extends ByteToMessageDecoder
{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object obj = ByteObjConverter.byteToObject(ByteBufToBytes.read(in));
        out.add(obj);
    }

}
