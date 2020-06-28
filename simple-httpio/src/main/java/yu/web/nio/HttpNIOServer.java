package yu.web.nio;

import yu.web.ServerConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @date: 2020/6/28 11:22
 * @author: farui.yu
 */
public class HttpNIOServer {

    private Selector selector;              // 多路复用器，NIO编程的基础，负责管理通道Channel
    private ServerSocketChannel channel;
    // the shutdown command received
    private boolean shutdown = false;

    public HttpNIOServer() {
        init();
    }

    private void init() {
        try {
            // 1.开启多路复用器
            selector = Selector.open();
            // 2.打开服务器通道(网络读写通道)
            channel = ServerSocketChannel.open();
            // 3.设置服务器通道为非阻塞模式，true为阻塞，false为非阻塞
            channel.configureBlocking(false);
            // 4.绑定端口
            channel.socket().bind(new InetSocketAddress(ServerConstant.SERVER_NIO_PORT));
            // 5.把通道注册到多路复用器上，并监听阻塞事件
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 需要一个线程负责Selector的轮询
    public void run() {
        while (!shutdown) {
            try {
                // 1.多路复用器监听阻塞
                selector.select();
                // 2.多路复用器已经选择的结果集
                Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                // 3.不停的轮询
                while (selectionKeys.hasNext()) {
                    // 4.获取一个选中的key
                    SelectionKey key = selectionKeys.next();
                    // 5.获取后便将其从容器中移除
                    selectionKeys.remove();
                    // 6.只获取有效的key
                    if (!key.isValid()) {
                        continue;
                    }
                    // 阻塞状态处理
                    if (key.isAcceptable()) {
                        accept(key);
                    }
                    // 可读状态处理
                    if (key.isReadable()) {
                        read(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 设置阻塞，等待Client请求。在传统IO编程中，用的是ServerSocket和Socket。在NIO中采用的ServerSocketChannel和SocketChannel
    private void accept(SelectionKey selectionKey) {
        try {
            // 1.获取通道服务
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            // 2.执行阻塞方法
            SocketChannel socketChannel = serverSocketChannel.accept();
            // 3.设置服务器通道为非阻塞模式，true为阻塞，false为非阻塞
            socketChannel.configureBlocking(false);
            // 4.把通道注册到多路复用器上，并设置读取标识
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        NIORequest request = new NIORequest(socketChannel);
        request.parse();

        NIOResponse response = new NIOResponse(socketChannel);
        response.setRequest(request);
        response.sendStaticResource();

        shutdown = request.getUri().equals(ServerConstant.SHUTDOWN_COMMAND);

        socketChannel.socket().close();
    }

    public static void main(String[] args) {
        new HttpNIOServer().run();
    }
}
