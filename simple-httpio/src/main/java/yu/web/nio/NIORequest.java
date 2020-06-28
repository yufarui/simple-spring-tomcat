package yu.web.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @date: 2020/6/28 14:01
 * @author: farui.yu
 */
public class NIORequest {

    private SocketChannel socketChannel;
    private String uri;

    private final int BUFFER_SIZE = 2048; // 缓冲区大小
    private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);  // 缓冲区Buffer

    public NIORequest(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void parse() throws IOException {

        // Read a set of characters from the socket
        StringBuffer request = new StringBuffer(2048);
        readBuffer.clear();
        int tmp = socketChannel.read(readBuffer);
        readBuffer.flip();
        byte[] bytes = new byte[readBuffer.remaining()];
        // 7.接收缓冲区数据
        readBuffer.get(bytes);
        request.append(new String(bytes));

        System.out.println("NIO Server request: " + request); // 不能用bytes.toString()
        uri = parseUri(request.toString());
    }

    private String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                return requestString.substring(index1 + 1, index2);
        }
        return null;
    }

    public String getUri() {
        return uri;
    }

}
