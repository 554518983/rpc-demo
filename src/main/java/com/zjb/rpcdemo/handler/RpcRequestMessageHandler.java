package com.zjb.rpcdemo.handler;

import com.sun.xml.internal.ws.api.message.MessageWritable;
import com.zjb.rpcdemo.factory.ServicesFactory;
import com.zjb.rpcdemo.message.RpcRequestMessage;
import com.zjb.rpcdemo.message.RpcResponseMessage;
import com.zjb.rpcdemo.service.HelloService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

/**
 * @author Administrator
 * @date 2021/10/11 22:33
 **/
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        try {
            HelloService helloService = (HelloService) ServicesFactory.getInstance(Class.forName(msg.getInterfaceName()));
            Method method = helloService.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(helloService, msg.getParameterValue());
            rpcResponseMessage.setReturnValue(invoke);
        } catch (Exception e) {
            rpcResponseMessage.setExceptionValue(e.getCause());
        }
        ctx.writeAndFlush(rpcResponseMessage);
    }
}
