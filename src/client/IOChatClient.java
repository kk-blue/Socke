package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class IOChatClient extends Thread{
    Socket socket=null;

    public static void main(String[] args) {
        IOChatClient client=new IOChatClient("127.0.0.1",2323);
        client.start();
    }
    public IOChatClient (String host,int port){
        try {
            socket=new Socket(host,port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        new sendMesstoServer().start();
        super.run();
        try {
            InputStream inputStream=socket.getInputStream();
            byte[] buf=new byte[1024];
            int len=0;
            while ((len=inputStream.read(buf))!=-1){
                System.out.println(new String(buf,0,len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class sendMesstoServer extends  Thread{
        @Override
        public void run() {
            super.run();
            Scanner scanner=null;
            OutputStream outputStream=null;
            try {
                scanner=new Scanner(System.in);
                outputStream=socket.getOutputStream();
                String in="";
                while (scanner.hasNextLine()){
                    in=scanner.nextLine();
                    outputStream.write(in.getBytes());
                    outputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            scanner.close();
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
