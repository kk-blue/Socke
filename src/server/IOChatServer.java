package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class IOChatServer extends Thread {
    ServerSocket serverSocket=null;
    Socket socket=null;

    public static void main(String[] args) {
        IOChatServer server=new IOChatServer(2323);
        server.start();
    }
    public IOChatServer(int port){
        try {
            serverSocket=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        System.out.println("Waiting client connect...");
        try {
            socket=serverSocket.accept();
            new sendMesstoClient().start();
            System.out.println(socket.getInetAddress().getHostAddress()+"  success connect!");
            InputStream inputStream=socket.getInputStream();
            int len=0;
            byte [] buf=new byte[1024];
            while( (len=inputStream.read(buf))!=-1){
                System.out.println("client:"+new String(buf,0,len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class sendMesstoClient extends Thread {
        @Override
        public void run() {
            super.run();
            Scanner scanner=null;
            OutputStream outputStream=null;
            try{
                if(socket!=null){
                    scanner=new Scanner(System.in);
                    outputStream=socket.getOutputStream();
                    String in="";
                    while (scanner.hasNextLine()){
                        in=scanner.nextLine();
                        outputStream.write(("server:"+in).getBytes());
                        outputStream.flush();
                    }
                    scanner.close();
                    outputStream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
