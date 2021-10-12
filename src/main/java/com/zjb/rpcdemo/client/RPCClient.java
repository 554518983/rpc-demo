package com.zjb.rpcdemo.client;

import com.zjb.rpcdemo.handler.RpcResponseMessageHandler;
import com.zjb.rpcdemo.message.RpcRequestMessage;
import com.zjb.rpcdemo.protocol.MessageCodecSharable;
import com.zjb.rpcdemo.protocol.ProcotolFrameDecoder;
import com.zjb.rpcdemo.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Administrator
 * @date 2021/10/11 22:38
 **/
public class RPCClient {

    private static AtomicInteger sequenceId = new AtomicInteger(0);
    private static volatile Channel channel = null;
    private static final Object local = new Object();

    public static void main(String[] args) {
        HelloService service = (HelloService) getProxy(HelloService.class);
        System.out.println(service.sayHi("tom"));
    }

    public static Channel getChannel() {
        if (channel == null) {
            synchronized (local) {
                if (channel == null) {
                    init();
                }
            }
        }
        return channel;
    }

    public static Object getProxy(Class<?> serviceClass) {
        Class<?>[] classes = new Class<?>[]{serviceClass};
        Object o = Proxy.newProxyInstance(serviceClass.getClassLoader(), classes, (proxy, method, args) -> {
            int id = sequenceId.getAndIncrement();
            RpcRequestMessage message = new RpcRequestMessage(id, serviceClass.getName(), method.getName(), method.getReturnType(), method.getParameterTypes(), args);
            getChannel().writeAndFlush(message);
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.promiseMap.put(id, promise);
            promise.await();
            if (promise.isSuccess()) {
                return promise.getNow();
            }
            throw new RuntimeException(promise.cause());
        });
        return o;
    }

    private static void init() {
        NioEventLoopGroup work = new NioEventLoopGroup();

        Bootstrap bs = new Bootstrap();
        bs.group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProcotolFrameDecoder());
                        ch.pipeline().addLast(new MessageCodecSharable());
                        ch.pipeline().addLast(new RpcResponseMessageHandler());
                    }
                });
        try {
            channel = bs.connect(new InetSocketAddress("localhost", 8080)).sync().channel();
            channel.closeFuture().addListener(future -> {
                work.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
