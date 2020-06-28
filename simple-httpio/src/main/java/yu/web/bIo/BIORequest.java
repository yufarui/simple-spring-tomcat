package yu.web.bIo;

import java.io.IOException;
import java.io.InputStream;

public class BIORequest {

    private InputStream input;
    private String uri;

    public BIORequest(InputStream input) {
        this.input = input;
    }

    public void parse() throws IOException {

        int available = input.available();
        // Read a set of characters from the socket
        StringBuffer request = new StringBuffer(available);

        byte[] buffer = new byte[available];
        input.read(buffer);
        request.append(new String(buffer));
        System.out.print("BIO request: \r\n" + request);
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