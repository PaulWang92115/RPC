package com.paul.procotol.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

    private static SocketServer INSTANCE = new SocketServer();

    private SocketServer(){};

    public static SocketServer getInstance() {
        return INSTANCE;
    }

    //没有核心线程数量控制的线程池，最大线程数是 Integer 的最大值,多线程实现伪异步
    ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 发布服务,bio 模型
     * @param service
     * @param port
     */
    public void publiser(int port){
        try (ServerSocket serverSocket  = new ServerSocket(port);)
        {
            while (true){
                Socket socket = serverSocket.accept();//接收请求
                executorService.execute(new SocketHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
