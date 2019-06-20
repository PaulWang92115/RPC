package com.paul.procotol.socket;

import com.paul.framework.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient {

    private static SocketClient INSTANCE = new SocketClient();

    private SocketClient(){};

    public static SocketClient getInstance() {
        return INSTANCE;
    }

    private Socket newSocket(String host, Integer port) {
        System.out.println("创建一个新的 socket 连接");
        try {
            Socket socket = new Socket(host, port);
            return socket;
        } catch (IOException e) {
            System.out.println("建立连接失败");
            e.printStackTrace();
        }
        return null;
    }

    public Object sendRequest(String host, Integer port,RpcRequest rpcRequest) {
        Socket socket = newSocket(host,port);
        try (
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());)
        {
            outputStream.writeObject(rpcRequest);
            outputStream.flush();


            Object result = inputStream.readObject();

            inputStream.close();
            outputStream.close();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
