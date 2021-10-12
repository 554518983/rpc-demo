package com.zjb.rpcdemo.server;

import com.zjb.rpcdemo.handler.RpcRequestMessageHandler;
import com.zjb.rpcdemo.protocol.MessageCodecSharable;
import com.zjb.rpcdemo.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * @author Administrator
 * @date 2021/10/11 22:11
 **/
public class RPCServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();

        MessageCodecSharable messageSharableCodec = new MessageCodecSharable();
        RpcRequestMessageHandler requestMessageHandler = new RpcRequestMessageHandler();

        ServerBootstrap bs = new ServerBootstrap();
        bs.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProcotolFrameDecoder());
                        ch.pipeline().addLast(messageSharableCodec);
                        ch.pipeline().addLast(requestMessageHandler);
                    }
                });
        try {
            ChannelFuture channelFuture = bs.bind(8080).sync();
            channelFuture.channel().closeFuture().addListener(future -> {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("服务端已启动。。。");
    }
}
