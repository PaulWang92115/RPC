package com.paul.register.zookeeper;

import com.paul.register.RegisterCenter4Consumer;
import com.paul.register.RegisterCenter4Provider;
import com.sun.deploy.util.StringUtils;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZookeeperRegisterCenter implements RegisterCenter4Provider, RegisterCenter4Consumer {

    private static ZookeeperRegisterCenter registerCenter = new ZookeeperRegisterCenter();

    private ZookeeperRegisterCenter(){};

    public static ZookeeperRegisterCenter getInstance(){
        return registerCenter;
    }
    //服务提供者列表，key：服务提供者接口，value：服务提供者服务方法列表
    private static final Map<String,List<ProviderService>> providerServiceMap = new ConcurrentHashMap<>();

    //服务端 zookeeper 元信息，选择服务（第一次从zookeeper 拉取，后续由zookeeper监听机制主动更新）
    private static final Map<String,List<ProviderService>> serviceData4Consumer = new ConcurrentHashMap<>();

    //从配置文件中获取 zookeeper 服务地址列表
    private static String  ZK_SERIVCE;

    //从配置文件中获取 zookeeper 会话超时时间配置
    private static int ZK_SESSION_TIME_OUT;

    //从配置文件中获取 zookeeper 连接超时事件配置
    private static int  ZK_CONNECTION_TIME_OUT;

    private static String ROOT_PATH = "/rpc_register/" + "APP_NAME";
    public  static String PROVIDER_TYPE = "/provider";
    public  static String CONSUMER_TYPE = "/consumer";

    private static volatile ZkClient zkClient = null;

    @Override
    public void initProviderMap() {
        if(MapUtils.isEmpty(serviceData4Consumer)){
            serviceData4Consumer.putAll(fetchOrUpdateServiceMetaData());
        }

    }

    @Override
    public Map<String, List<ProviderService>> getServiceMetaDataMap4Consumer() {
        return serviceData4Consumer;
    }

    @Override
    public void registerConsumer(ConsumerService consumer) {
        if(consumer == null){
            return;
        }

        //连接 zookeeper ，注册服务
        synchronized (ZookeeperRegisterCenter.class){
            if(zkClient == null){
                zkClient = new ZkClient(ZK_SERIVCE,ZK_SESSION_TIME_OUT,ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }
            //创建  zookeeper 命名哦空间
            boolean exist = zkClient.exists(ROOT_PATH);
            if(!exist){
                zkClient.createPersistent(ROOT_PATH,true);
            }
            //创建服务提供者节点
            exist = zkClient.exists((ROOT_PATH));
            if(!exist){
                zkClient.createPersistent(ROOT_PATH);
            }

            //创建服务消费者节点
            String serviceNode = consumer.getServiceItf.getName();
            String servicePath = ROOT_PATH + "/" + serviceNode + CONSUMER_TYPE;

            exist = zkClient.exists(servicePath);
            if(!exist){
                zkClient.createPersistent(servicePath);
            }

            //创建当前服务器节点
            String ip = ""；
            String currentServiceIpNode = servicePath + "/" + ip;
            exist = zkClient.exists(currentServiceIpNode);
            if(!exist){
                zkClient.createEphemeral(currentServiceIpNode);
            }


        }

    }

    @Override
    public void registerProvider(List<ProviderSerivce> serivceList) {
        if(serivceList.size() == 0){
            return;
        }
        //连接 zookeeper，注册服务,加锁
        synchronized (ZookeeperRegisterCenter.class){
            //考虑新增节点的情况
            for(ProviderServicee provider:serivceList){
                String serviceItfKey = provider.getServiceItf.getName();
                List<ProviderServicee> providers = providerServiceMap.get(serviceItfKey);
                if(providers = null){
                    providers = new ArrayList<>();
                }
                providers.add(provider);
                providerServiceMap.put(serviceItfKey,providers);
            }

            if(zkClient == null){
                zkClient = new ZkClient(ZK_SERIVCE,ZK_SESSION_TIME_OUT,ZK_CONNECTION_TIME_OUT,new SerializableSerializer());
            }

            //创建当前应用 zookeeper 命名空间
            boolean exist = zkClient.exists(ROOT_PATH);
            if(!exist){
                zkClient.createPersistent(ROOT_PATH,true);
            }

            //服务提供者节点
            exist = zkClient.exists((ROOT_PATH));
            if(!exist){
                zkClient.createPersistent(ROOT_PATH);
            }

            for(Map.Entry<String,List<ProviderServicee>> entry:providerServiceMap.entrySet()){
                //创建服务提供者节点
                String serviceNode = entry.getKey();
                String servicePath = ROOT_PATH + "/" + serviceNode + PROVIDER_TYPE;
                exist = zkClient.exists(servicePath);
                if(!exist){
                    zkClient.createPersistent(servicePath,true);
                }

                //创建当前服务器节点
                int serverPort = entry.getValue().get(0).getServerPort();
                String ip = "";
                String serviceIpNode = servicePath + ip + "|" + serverPort;
                exist = zkClient.exists(serviceIpNode);
                if(!exist){
                    //创建临时节点
                    zkClient.createEphemeral(serviceIpNode);
                }
                //监听注册服务的变化，同时更新数据到本地缓存
                zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                    @Override
                    public void handleChildChange(String s, List<String> list) throws Exception {
                        if(list  == null){
                            list = new ArrayList<>();
                        }
                        //存活的服务 IP 列表
                        List<String> activeServiceIpList = Lists.newArrayList(Lists.transform(list, new Function<String,String>(){
                            @Override
                            public String apply(String input){
                                return StringUtils.split(input,"|")[0];
                            }
                        }));
                        refreshActivityService(activeServiceIpList);
                    }
                });

            }
        }

    }

    @Override
    public Map<String, List<ProviderSerivce>> getProviderService() {
        return providerServiceMap;
    }
}
