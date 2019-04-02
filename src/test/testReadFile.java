package test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class testReadFile {
    public static void main(String[] args) {
        testReadFile.method();
        System.out.println("=================");
        testReadFile.method1();

    }
    public static  void method(){
        InputStream in=null;
        try {
            in=new BufferedInputStream(new FileInputStream("E:/1.txt"));
            byte [] buf=new byte[1024];
            int byteRead=in.read(buf);
            while (byteRead!=-1){
                for (int i=0;i<byteRead;i++){
                    System.out.println((char)buf[i]);
                }
                byteRead=in.read(buf);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void method1(){
        RandomAccessFile aFile=null;
        try {
            aFile=new RandomAccessFile("E:/1.txt","rw");
            FileChannel fileChannel=aFile.getChannel();
            ByteBuffer buf=ByteBuffer.allocate(1024);
            int bytesRead=fileChannel.read(buf);
            System.out.println(bytesRead);
            while (bytesRead!=-1){
                buf.flip();
                while (buf.hasRemaining()){
                    System.out.println((char)buf.get());
                }
                buf.compact();
                bytesRead=fileChannel.read(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (aFile!=null){
                try {
                    aFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
