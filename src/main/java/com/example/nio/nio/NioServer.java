package com.example.nio.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws IOException {
//      創建NIO ServerSocketChannel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));
//      設置為非阻塞
        serverSocket.configureBlocking(false);
//      打開Selector處理Channel
        Selector selector = Selector.open();
//      註冊該事件 監聽連接事件
        SelectionKey selectionKey = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服務創建成功");

        while (true) {
//          阻塞等待需要處理的事件發生
            selector.select();
//          獲取SelectorKey中註冊的全部事件的SelectorKey實例
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel)key.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
//                  註冊該事件 監聽READ事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客戶端連接成功");
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel)key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    int len = socketChannel.read(byteBuffer);

                    if (len > 0) {
                        System.out.println(String.format("接收到消息:%s", new String(byteBuffer.array())));
                    } else if (len == -1) {
                        System.out.println("客戶端斷開連線");
                        socketChannel.close();
                    }
                }

                iterator.remove();
            }
        }
    }
}
