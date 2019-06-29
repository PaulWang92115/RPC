package com.paul.procotol.http;

import com.paul.framework.RpcRequest;
import com.paul.framework.RpcResponse;
import org.apache.commons.io.IOUtils;
import org.objenesis.Objenesis;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {
	
	private static HttpClient INSTANCE = new HttpClient();
	
	private HttpClient(){}
	
	public static HttpClient getInstance(){
		return INSTANCE;
	}

    public Object post(String hostname, Integer port, RpcRequest invocation){

        try{
            URL url = new URL("http",hostname,port,"/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(invocation);
            oos.flush();
            oos.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            RpcResponse rpcResponse =  (RpcResponse)toObject(IOUtils.toByteArray(inputStream));
            return rpcResponse.getData();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public Object toObject (byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }
}
