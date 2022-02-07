package com.example.nio.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                // 客戶端 channel 實現
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override // 在連接建立後被調用
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 異步非阻塞
                .connect(new InetSocketAddress("localhost", 9000));
        // 若不設置同步 會導致下一步取出的channel尚未連接(因連接會花個1秒左右)
        channelFuture.sync();
        Channel channel = channelFuture.channel();
        // 向服務發送訊息
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String str = scanner.nextLine();
                if (str.equals("q")) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(str);
            }
        }, "input").start();
    }
}
