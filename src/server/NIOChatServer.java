package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;

public class NIOChatServer {
    private Selector selector = null;
    static final int port = 9999;
    private Charset charset = Charset.forName("UTF-8");
    //用来记录在线人数，以及昵称
    private static Map<String,SocketChannel> clientMap = new HashMap<>();
    private static String USER_EXIST = "system message: user exist, please change a name";

    public void init() throws IOException {
//        创建selector
        selector = Selector.open();
//        打开ServerSocketChannel
        ServerSocketChannel server = ServerSocketChannel.open();
//        ServerSocketChannel绑定指定端口
        server.bind(new InetSocketAddress(port));
//        设置为非阻塞的方式
        server.configureBlocking(false);
//        注册到选择器上，设置为监听状态
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server is listening ...");
        while (true) {
            int readyChannel = selector.select();
            if (readyChannel == 0) {
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                iterator.remove();
                dealWithSelectionKey(server, sk);
            }

        }
    }

    public void dealWithSelectionKey(ServerSocketChannel server, SelectionKey sk) throws IOException {
        if (sk.isAcceptable()) {
            SocketChannel sc = server.accept();
            sc.configureBlocking(false);
//           注册选择器，并设置为读取模式，收到一个连接请求，然后起一个SocketChannel，并注册到selector上，之后这个连接的数据，就由这个SocketChannel处理
            sc.register(selector, SelectionKey.OP_READ);
//            将此对应的channel设置为准备接受其他客户端请求
            sk.interestOps(SelectionKey.OP_ACCEPT);
            System.out.println("server is listen from client:" + sc.getRemoteAddress());
            sc.write(charset.encode("Please input your name:"));
        }
//        处理来来自客户端的数据读取请求
        if (sk.isReadable()) {
//            返回该SelectionKey对应的 Channel，其中有数据需要读取
            SocketChannel sc = (SocketChannel) sk.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder content = new StringBuilder();
            String serverContent=null;
            try {
                while (sc.read(buffer) > 0) {
                    buffer.flip();
                    content.append(charset.decode(buffer));
                    serverContent="Server is listening from client " + sc.getRemoteAddress() + " message " + content;
                }
//                System.out.println("Server is listening from client " + sc.getRemoteAddress() + " message " + content);
//                将此对应的channel设置为准备下一次接受数据
                sk.interestOps(SelectionKey.OP_READ);
            } catch (IOException io) {
                sk.cancel();
                if (sk.channel() != null) {
                    sk.channel().close();
                }
            }
            if (content.length() > 0) {
                String[] arrayContent = content.toString().split("#");
//                注册用户
                if (arrayContent != null && arrayContent.length == 1) {
                    String name = arrayContent[0];
                    if (clientMap.get(name)!=null) {
//                        sc.write(charset.encode("Username is exist"));
                        sc.write(charset.encode(USER_EXIST));
                    } else {
                        int num = OnlineNum(selector);
                        String message = "Welcom  " + name + " to chat romm! Online person:  " + num;
                        clientMap.put(name,sc);
                        BroadCast(selector, null, message);
                    }
                }
//                注册完成，发送消息
                else if (arrayContent != null && arrayContent.length > 1) {
                    String name = arrayContent[0];
                    String message = content.substring(name.length() + 1);
                    // 检验是否为私聊（格式：@昵称：内容）
                    if(message.startsWith("@")) {
                        int index = message.indexOf(":");
                        if(index >= 0) {
                            //获取昵称
                            String theName = message.substring(1, index);
                            String info = message.substring(index+1, message.length());
                            info =  name + "："+ info;
                            //将私聊信息发送出去
                            SendToOne(selector,clientMap,info,theName);
                        }
                    }else {
                        message = name + " say " + message;
                        System.out.println(message);
                        if (clientMap.get(name) != null) {
                            BroadCast(selector, sc, message);
                        }
//                        System.out.println(message);
                    }
                }
            }
        }
    }

    public static int OnlineNum(Selector selector) {
        int num = 0;
        for (SelectionKey key : selector.keys()) {
            Channel targetchannel = key.channel();
            if (targetchannel instanceof SocketChannel) {
                num++;
            }
        }
        return num;
    }

    public void BroadCast(Selector selector, SocketChannel except, String content) throws IOException {
//       广播数据到所有客户端
        for (SelectionKey key : selector.keys()) {
            Channel targetchannel = key.channel();
            //如果except不为空，不回发给发送此内容的客户端
            if (targetchannel instanceof SocketChannel && targetchannel != except) {
                SocketChannel dest = (SocketChannel) targetchannel;
                dest.write(charset.encode(content));
            }
        }
    }
    public void SendToOne(Selector selector, Map clientMap, String content,String name) throws IOException {
//       广播数据到指定客户端
        for (SelectionKey key : selector.keys()) {
            Channel targetchannel = key.channel();
            //如果except不为空，不回发给发送此内容的客户端
            SocketChannel onechannel= (SocketChannel) clientMap.get(name);
            if (targetchannel instanceof SocketChannel && targetchannel == onechannel) {
                SocketChannel dest = (SocketChannel) targetchannel;
                dest.write(charset.encode(content));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NIOChatServer().init();
    }
}



