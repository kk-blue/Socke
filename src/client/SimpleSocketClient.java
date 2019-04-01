package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SimpleSocketClient {
    public static void main(String[] args) throws IOException {
        String host="127.0.0.1";
        int port=1227;
        Socket socket=new Socket(host,port);
        OutputStream outputStream=socket.getOutputStream();
        String msg="你好！！Hello World!";
        socket.getOutputStream().write(msg.getBytes("UTF-8"));
        outputStream.close();
        socket.close();
    }
}
