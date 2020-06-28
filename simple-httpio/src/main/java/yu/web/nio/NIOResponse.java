package yu.web.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static yu.web.ServerConstant.WEB_PREFIX;
import static yu.web.ServerConstant.WEB_SUFFIX;

/**
 * @date: 2020/6/28 14:12
 * @author: farui.yu
 */
public class NIOResponse {

    private static final int BUFFER_SIZE = 1024;
    private NIORequest request;
    private SocketChannel socketChannel;
    private ByteBuffer writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);  // 缓冲区Buffer

    public NIOResponse(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void setRequest(NIORequest request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            File file = new File(WEB_PREFIX, request.getUri() + WEB_SUFFIX);
            System.out.println(file.getName());
            if (file.exists()) {
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    writeBuffer.put(bytes, 0, BUFFER_SIZE);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
                writeBuffer.flip();
                socketChannel.write(writeBuffer);
                writeBuffer.clear();
            } else {
                // file not found
                String errorMessage =
                        "HTTP/1.1 404 File Not Found\r\n" +
                                "Content-Type: text/html\r\n" +
                                "Content-Length: 23\r\n" +
                                "\r\n" +
                                "<h1>File Not Found</h1>";
                writeBuffer.put(errorMessage.getBytes());
                writeBuffer.flip();
                socketChannel.write(writeBuffer);
                writeBuffer.clear();
            }
        } catch (Exception e) {
            // thrown if cannot instantiate a File object
            System.out.println(e.toString());
        } finally {
            if (fis != null)
                fis.close();
        }
    }
}
