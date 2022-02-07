package com.example.nio.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class Server {

    public static void main(String[] args) {
        new ServerBootstrap()
                //      boss(負責連接)                      worker(負責讀寫)
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(4))
                // 選擇 ServerSocketChannel 實現
                .channel(NioServerSocketChannel.class)
                // channel 和客戶端進行數據讀寫的通道 Initializer 初始化, 負責添加別的 handler
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 將 ByteBuffer 轉為字串
                        nioSocketChannel.pipeline().addLast(new StringDecoder());
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                // 綁定port
                .bind(9000);
    }
}
