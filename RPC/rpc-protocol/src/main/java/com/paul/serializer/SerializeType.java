package com.paul.serializer;

public enum SerializeType {

    DefaultJavaSerializer("Java"),
    HessianSerializer("Hessian"),
    JSONSerializer("Json"),
    ProtoStuffSerializer("ProtoStuff"),
    XmlSerializer("Xml");

    private String serializeType;

    private SerializeType(String serializeType) {
        this.serializeType = serializeType;
    }

    public String getSerializeType() {
        return serializeType;
    }

    public static SerializeType queryByType(String serializeType) {
        if("".equals(serializeType) || null == serializeType) {
            return null;
        }

        for (SerializeType serialize : SerializeType.values()) {
            if (serializeType.equals(serialize.getSerializeType())) {
                return serialize;
            }
        }
        return null;
    }
}
