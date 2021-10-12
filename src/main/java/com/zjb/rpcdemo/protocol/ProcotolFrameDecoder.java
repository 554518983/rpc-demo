package com.zjb.rpcdemo.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author Administrator
 * @date 2021/10/08 23:13
 **/
public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProcotolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProcotolFrameDecoder(
            int maxFrameLength,
            int lengthFieldOffset,
            int lengthFieldLength,
            int lengthAdjustment,
            int initialBytesToStrip)
    {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,lengthAdjustment, initialBytesToStrip);
    }
}
