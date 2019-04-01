package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleSocketServer {
    public static void main(String[] args) throws IOException {
        int port=1227;
        ServerSocket serverSocket=new ServerSocket(port);
        System.out.println("waiting......");
        Socket socket=serverSocket.accept();
        InputStream inputStream=socket.getInputStream();
        byte[] bytes=new byte[1024];
        int len;
        StringBuilder sb=new StringBuilder();
        while((len=inputStream.read(bytes))!=-1){
            sb.append(new String(bytes,0,len,"UTF-8"));
        }
        System.out.println("Get message from Client:"+sb);
        inputStream.close();
        serverSocket.close();
        socket.close();
    }
}
