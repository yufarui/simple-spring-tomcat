package yu.web;

import java.io.File;

/**
 * @date: 2020/6/28 10:35
 * @author: farui.yu
 */
public class ServerConstant {

    // shutdown command
    public static final String SHUTDOWN_COMMAND = "/close";

    public static final String SERVER_NAME = "127.0.0.1";

    public static final int SERVER_PORT = 8002;

    public static final int SERVER_NIO_PORT = 8003;

    public static final String WEB_PREFIX = System.getProperty("user.dir")
            + "\\simple-httpio"
            + "\\src\\main\\resources"
            + File.separator + "web"
            + File.separator + "views";

    public static final String WEB_SUFFIX = ".html";
}
