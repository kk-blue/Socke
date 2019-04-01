package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TwoSocketClient {
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
//        通过shutdownOutput告诉服务器已经发送完数据，后续只接收数据
        socket.shutdownOutput();

        InputStream inputStream=socket.getInputStream();
        byte[] bytes=new byte[1024];
        int len;
        StringBuilder sb=new StringBuilder();
        while((len=inputStream.read(bytes))!=-1){
//            注意指定编码格式，发送，接受格式要统一
            sb.append(new String(bytes,0,len,"UTF-8"));
        }
        System.out.println("Get message from Server:"+sb);
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}
