package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleSocketServer {
    public static void main(String[] args) throws IOException {
//        监听指定端口
        int port=1227;
        ServerSocket serverSocket=new ServerSocket(port);
//        server等待连接的到来
        System.out.println("waiting......");
        Socket socket=serverSocket.accept();
//        建立好连接，从socket中获取输入流，建立缓冲区进行读取
        InputStream inputStream=socket.getInputStream();
        byte[] bytes=new byte[1024];
        int len;
        StringBuilder sb=new StringBuilder();
        while((len=inputStream.read(bytes))!=-1){
//            注意指定编码格式，发送，接受格式要统一
            sb.append(new String(bytes,0,len,"UTF-8"));
        }
        System.out.println("Get message from Client:"+sb);
        inputStream.close();
        serverSocket.close();
        socket.close();
    }
}
