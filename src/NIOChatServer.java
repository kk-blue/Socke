import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class NIOChatServer {
    private Selector selector = null;
    private ServerSocketChannel server=null;
    static final int port = 9999;
    private Charset charset = Charset.forName("UTF-8");
    //用来记录用户名字和用户对应的SocketChannel
    private static Map<String,SocketChannel> clientMap = new HashMap<>();
    private static String USER_EXIST = "system message: user exist, please change a name";

    public void init() throws IOException {
//        创建selector
        selector = Selector.open();
//        打开ServerSocketChannel
         server = ServerSocketChannel.open();
//        ServerSocketChannel绑定指定端口
        server.bind(new InetSocketAddress(port));
//        设置为非阻塞的方式
        server.configureBlocking(false);
//        注册到选择器上，设置为监听状态
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server is listening ...");
//        new Thread(new ServerThread()).start();
        while (true) {
            int readyChannel = selector.select();
            if (readyChannel == 0) {
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                dealWithSelectionKey(server, sk);
//                将处理过的事件移除
                iterator.remove();
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
            sk.interestOps(SelectionKey.OP_ACCEPT|SelectionKey.OP_READ);
            System.out.println("server is listen from client:" + sc.getRemoteAddress());
            sc.write(charset.encode("请输入你的昵称:"));
        }
//        处理来来自客户端的数据读取请求
        if (sk.isReadable()) {
            readMsg(sk);
        }
    }
    public void  readMsg(SelectionKey sk) throws IOException {
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
                    String message = "欢迎  " + name + " 来到聊天室!私聊格式：@昵称:内容     当前人数：" + num;
                    clientMap.put(name,sc);
                    SendToAll(selector, null, message);
                }
            }
//                注册完成，发送消息
            else if (arrayContent != null && arrayContent.length > 1) {
                String name = arrayContent[0];
                String message = content.substring(name.length() + 1);
//                     检验是否为私聊（格式：@昵称：内容）
                if(message.startsWith("@")) {
                    int index = message.indexOf(":");
                    if(index >= 0) {
//                            获取昵称
                        String name1 = message.substring(1, index);
                        String info = message.substring(index+1, message.length());
                        info =  name + "："+ info;
//                            将私聊信息发送出去
                        SendToOne(selector,clientMap,info,name1);
                    }
                }else if(message.startsWith("-")){
                    int index = message.indexOf(".exit");
                    if(index >= 0) {
//                            获取昵称
                        String name2 = message.substring(1, index);
                        String info = message.substring(index + 1, message.length());
                        info = name + "." + info;
                        ExitOne(selector, clientMap, info, name2);
                    }
                }else {
                    message = name + " say " + message;
                    System.out.println(message);
                    if (clientMap.get(name) != null) {
//                            将信息发到所有客户端
                        SendToAll(selector, sc, message);
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

    public void SendToAll(Selector selector, SocketChannel except, String content) throws IOException {
//       发送数据到所有客户端
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
//       发送数据到指定客户端
        for (SelectionKey key : selector.keys()) {
            Channel targetchannel = key.channel();
            SocketChannel destchannel= (SocketChannel) clientMap.get(name);
//            如果获取不到对应的SocketChannel，告诉发送者，找不到用户
            if(destchannel==null){
                int index = content.indexOf("：");
                if(index >= 0) {
                    String theName = content.substring(0, index);
                    SocketChannel sourcechannel = (SocketChannel) clientMap.get(theName);
                        sourcechannel.write(charset.encode("指定私聊用户不存在"));
                }
                break;
//                System.out.println("指定私聊用户不存在");
            }
//            如果destchannel等于某个注册到selector的socketChannel，发给内容到指定的客户端
            if (targetchannel instanceof SocketChannel && targetchannel == destchannel) {
                destchannel.write(charset.encode(content));
            }
        }
    }
    public void ExitOne(Selector selector, Map clientMap, String content,String name) throws IOException {
//       发送数据到指定客户端
        for (SelectionKey key : selector.keys()) {
            Channel targetchannel = key.channel();
            SocketChannel destchannel= (SocketChannel) clientMap.get(name);
//            如果获取不到对应的SocketChannel，告诉发送者，找不到用户
            if(destchannel==null){
                int index = content.indexOf(".");
                if(index >= 0) {
                    String theName = content.substring(0, index);
                    SocketChannel sourcechannel = (SocketChannel) clientMap.get(theName);
                    sourcechannel.write(charset.encode("指定下线用户不存在"));
                }
                break;
//                System.out.println("指定私聊用户不存在");
            }
//            如果destchannel等于某个注册到selector的socketChannel，关闭指定的客户端
            if (targetchannel instanceof SocketChannel && targetchannel == destchannel) {
                key.channel();
                destchannel.close();
                int num = OnlineNum(selector);
                System.out.println(num);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NIOChatServer().init();
    }
    private class  ServerThread implements Runnable{

        @Override
        public void run() {
            try {
                while (true) {
                    int readyChannel = selector.select();
                    if (readyChannel == 0) {
                        continue;
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey sk = iterator.next();
                        dealWithSelectionKey(server, sk);
//                将处理过的事件移除
                        iterator.remove();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}



