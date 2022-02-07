package com.example.nio.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

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
//                        nioSocketChannel.pipeline().addLast(new StringDecoder());
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buffer = (ByteBuf) msg;
                                String name = buffer.toString(Charset.defaultCharset());
                                // 可以把處理好的訊息傳給下個 handler, 如果不調用 鏈就會斷開
                                super.channelRead(ctx, name);
                            }
                        });
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(String.format("name : %s", msg));
                                // 有寫出才會觸發 ChannelOutboundHandlerAdapter 的 write 方法
                                nioSocketChannel.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
                            }
                        });
                        nioSocketChannel.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                // 綁定port
                .bind(9000);
    }
}
