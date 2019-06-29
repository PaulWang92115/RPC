package com.paul.procotol.http;

import com.paul.framework.RpcRequest;
import com.paul.framework.RpcResponse;
import com.paul.framework.URL;
import com.paul.register.Register;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HttpServletHandler{

    public void handler(HttpServletRequest req, HttpServletResponse resp) {

        try(InputStream inputStream = req.getInputStream();
            OutputStream outputStream =resp.getOutputStream();){
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            RpcRequest invocation = (RpcRequest) ois.readObject();

            // 从注册中心根据接口，找接口的实现类
            String interFaceName = invocation.getInterfaceName();
            Class impClass = Class.forName(invocation.getImpl());


            Method method = impClass.getMethod(invocation.getMethodName(),invocation.getParamTypes());
            Object result = method.invoke(impClass.newInstance(),invocation.getParams());

            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setResponseId(invocation.getRequestId());
            rpcResponse.setData(result);
            IOUtils.write(toByteArray(rpcResponse),outputStream);
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

}
