package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SimpleSocketClient {
    public static void main(String[] args) throws IOException {
//        连接的服务IP地址和端口号
        String host="127.0.0.1";
        int port=1227;
//        与服务端建立连接
        Socket socket=new Socket(host,port);
//        建立连接后获得输出流
        OutputStream outputStream=socket.getOutputStream();
        String msg="你好！！Hello World!";
        socket.getOutputStream().write(msg.getBytes("UTF-8"));
        outputStream.close();
        socket.close();
    }
}
