package com.paul.procotol.socket;

import com.paul.framework.RpcRequest;
import com.paul.framework.URL;
import com.paul.register.Register;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 用来处理 socket
 */
public class SocketHandler implements Runnable{

    private Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());)
        {
            Object o = inputStream.readObject(); //readObject 是 java 反序列化的过程
            System.out.println(o);
            Object result = invoke((RpcRequest) o);
            //写回结果
            outputStream.writeObject(result);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Object invoke(RpcRequest invocation){

        //根据方法名和参数类型在 service 里获取方法
        try {
            String interFaceName = invocation.getInterfaceName();
            Class impClass = Class.forName(invocation.getImpl());

            Method method = impClass.getMethod(invocation.getMethodName(),invocation.getParamTypes());
            String result = (String)method.invoke(impClass.newInstance(),invocation.getParams());
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
