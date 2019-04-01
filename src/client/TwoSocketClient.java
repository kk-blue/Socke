package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TwoSocketClient {
    public static void main(String[] args) throws IOException {
//        连接的服务IP地址和端口号
        String host="127.0.0.1";
        int port=1227;
//        与服务端建立连接
        Socket socket=new Socket(host,port);
//        建立连接后获得输出流
        Scanner insc=new Scanner(socket.getInputStream());
        System.out.println(insc.nextLine());
        PrintWriter pwtoserver=new PrintWriter(socket.getOutputStream());
        System.out.print("我（客户端）：");
        Scanner kbsc=new Scanner(System.in);
        while(kbsc.hasNextLine()){
            String kbdata=kbsc.nextLine();
            System.out.println("我（客户端）："+kbdata);
            pwtoserver.println(kbdata);
            pwtoserver.flush();
            String indata=insc.nextLine();
            System.out.println("服务端："+indata);
            System.out.print("我（客户端）：");
        }
//        System.out.println("Get message from Client:"+sb);
        kbsc.close();
        insc.close();
        pwtoserver.close();
        socket.close();
    }
}
