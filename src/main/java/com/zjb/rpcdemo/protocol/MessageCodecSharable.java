package com.zjb.rpcdemo.protocol;

import com.zjb.rpcdemo.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author Administrator
 * @date 2021/10/08 22:59
 **/
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();

        out.writeBytes(new byte[]{1, 2, 3, 4}); // 4字节的 魔数
        out.writeByte(1);                    // 1字节的 版本
        out.writeByte(0);                    // 1字节的 序列化方式 0-jdk,1-json
        out.writeByte(msg.getMessageType()); // 1字节的 指令类型
        out.writeInt(msg.getSequenceId());   // 4字节的 请求序号 【大端】
        out.writeByte(0xff);                 // 1字节的 对其填充，只为了非消息内容 是2的整数倍

        // 处理内容 用对象流包装字节数组 并写入
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); // 访问数组
        ObjectOutputStream oos = new ObjectOutputStream(bos);    // 用对象流 包装
        oos.writeObject(msg);

        byte[] bytes = bos.toByteArray();

        // 写入内容 长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);

        /**
         * 加入List 方便传递给 下一个Handler
         */
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();        // 大端4字节的 魔数
        byte version = in.readByte();       // 版本
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();

        int length = in.readInt();
        final byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 处理内容
        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        final ObjectInputStream ois = new ObjectInputStream(bis);

        // 转成 Message类型
        Message message = (Message) ois.readObject();

        /**
         * 加入List 方便传递给 下一个Handler
         */
        out.add(message);
    }
}
