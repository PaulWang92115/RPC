package com.paul.serializer;

import com.paul.serializer.Impl.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerEngine {

    private static final Map<SerializeType,Serializer> serialerMap = new ConcurrentHashMap<>();

    //注册已有的实现类
    static{
        serialerMap.put(SerializeType.DefaultJavaSerializer,new DefaultJavaSerializer());
        serialerMap.put(SerializeType.HessianSerializer,new HessianSerializer());
        serialerMap.put(SerializeType.ProtoStuffSerializer,new ProtoStuffSerializer());
        serialerMap.put(SerializeType.JSONSerializer,new JSONSerializer());
        serialerMap.put(SerializeType.XmlSerializer,new XmlSerializer());
    }

    //通用的序列化方法
    public static <T> byte[] serialize(T obj,String serialzieType){
        SerializeType serializeType = SerializeType.queryByType(serialzieType);
        if(serializeType == null){
            serializeType = SerializeType.DefaultJavaSerializer;
        }
        Serializer serializer = serialerMap.get(serializeType);

        return serializer.serialize(obj);

    }

    //通用反序列化方法
    public static <T> T deserialize(byte[] data, Class<T> clazz,String serialzieType){
        SerializeType serializeType = SerializeType.queryByType(serialzieType);
        if(serializeType == null){
            serializeType = SerializeType.DefaultJavaSerializer;
        }
        Serializer serializer = serialerMap.get(serializeType);
        return serializer.deserialize(data,clazz);
    }
}
