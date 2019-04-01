package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
        PrintWriter pwtoclient=new PrintWriter(socket.getOutputStream());
        pwtoclient.println("已成功连接到服务器！请你先发言");
        pwtoclient.flush();
        Scanner kbsc=new Scanner(System.in);
        Scanner insc=new Scanner(socket.getInputStream());
        while(insc.hasNextLine()){
            String indata=insc.nextLine();
            System.out.println("客户端："+indata);
            System.out.print("我（服务端）：");
            String kbdata=kbsc.nextLine();
            System.out.println("我（服务端）："+kbdata);
            pwtoclient.println(kbdata);
            pwtoclient.flush();
        }
//        System.out.println("Get message from Client:"+sb);
        kbsc.close();
        insc.close();
        pwtoclient.close();
        serverSocket.close();
        socket.close();
    }
}
