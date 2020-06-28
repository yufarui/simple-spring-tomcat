package yu.web.bIo;

import yu.web.ServerConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 使用BIO简单地建立 和浏览器 的通信
 */
public class HttpBIOServer {

    // the shutdown command received
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpBIOServer server = new HttpBIOServer();
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(ServerConstant.SERVER_PORT, 1, InetAddress.getByName(ServerConstant.SERVER_NAME));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Loop waiting for a request
        while (!shutdown) {
            Socket socket;
            InputStream input;
            OutputStream output;
            try {
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // create Request object and parse
                BIORequest request = new BIORequest(input);
                request.parse();

                // create Response object
                BIOResponse response = new BIOResponse(output);
                response.setRequest(request);
                response.sendStaticResource();

                // Close the socket
                socket.close();

                //check if the previous URI is a shutdown command
                shutdown = request.getUri().equals(ServerConstant.SHUTDOWN_COMMAND);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
