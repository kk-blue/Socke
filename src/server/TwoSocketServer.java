package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TwoSocketServer {
    public static void main(String[] args) throws IOException {
//        监听指定端口
        int port=1227;
        ServerSocket serverSocket=new ServerSocket(port);
//        server等待连接的到来
        System.out.println("waiting......");
        Socket socket=serverSocket.accept();
        System.out.println("成功建立连接");
//        建立好连接，从socket中获取输入流，建立缓冲区进行读取
        Scanner sc=new Scanner(System.in);
        InputStream inputStream=socket.getInputStream();
        byte[] bytes=new byte[1024];
        int len;
        StringBuilder sb=new StringBuilder();
        while((len=inputStream.read(bytes))!=-1){
//            注意指定编码格式，发送，接受格式要统一
            sb.append(new String(bytes,0,len,"UTF-8"));
        }
        System.out.println("Get message from Client:"+sb);

        OutputStream outputStream=socket.getOutputStream();
        outputStream.write("Hello Client!我已经收到你的消息了".getBytes("UTF-8"));

        inputStream.close();
        outputStream.close();
        serverSocket.close();
        socket.close();
    }
}
