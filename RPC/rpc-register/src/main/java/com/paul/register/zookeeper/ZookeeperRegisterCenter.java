package com.paul.register.zookeeper;

import com.paul.framework.Configuration;
import com.paul.framework.ServiceConsumer;
import com.paul.framework.ServiceProvider;
import com.paul.register.RegisterCenter4Consumer;
import com.paul.register.RegisterCenter4Provider;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.common.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private static final Map<String,List<ServiceProvider>> providerServiceMap = new ConcurrentHashMap<>();

    //服务端 zookeeper 元信息，选择服务（第一次从zookeeper 拉取，后续由zookeeper监听机制主动更新）
    private static final Map<String,List<ServiceProvider>> serviceData4Consumer = new ConcurrentHashMap<>();

    //从配置文件中获取 zookeeper 服务地址列表
    private static String  ZK_SERIVCE = Configuration.getInstance().getAddress();

    //从配置文件中获取 zookeeper 会话超时时间配置
    private static int ZK_SESSION_TIME_OUT = 5000;

    //从配置文件中获取 zookeeper 连接超时事件配置
    private static int  ZK_CONNECTION_TIME_OUT = 5000;

    private static String ROOT_PATH = "/rpc_register";
    public  static String PROVIDER_TYPE = "/provider";
    public  static String CONSUMER_TYPE = "/consumer";

    private static volatile ZkClient zkClient = null;

    @Override
    public void initProviderMap() {
        if(serviceData4Consumer.isEmpty()){
            serviceData4Consumer.putAll(fetchOrUpdateServiceMetaData());
        }

    }

    @Override
    public Map<String, List<ServiceProvider>> getServiceMetaDataMap4Consumer() {
        return serviceData4Consumer;
    }

    @Override
    public void registerConsumer(ServiceConsumer consumer) {
        if(consumer == null){
            return;
        }

        //连接 zookeeper ，注册服务
        synchronized (ZookeeperRegisterCenter.class){
            if(zkClient == null){
                zkClient = new ZkClient(ZK_SERIVCE,ZK_SESSION_TIME_OUT,ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }
            //创建  zookeeper 命名空间
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
            String serviceNode = consumer.getConsumer().getName();
            String servicePath = ROOT_PATH  + CONSUMER_TYPE+ "/" + serviceNode;

            exist = zkClient.exists(servicePath);
            System.out.println("exist:"+exist);
            System.out.println("servicePath:"+servicePath);
            if(!exist){
                zkClient.createPersistent(servicePath,true);
            }

            //创建当前服务器节点
            InetAddress addr = null;
            try {
                addr = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            String ip = addr.getHostAddress();
            String currentServiceIpNode = servicePath + "/" + ip;
            exist = zkClient.exists(currentServiceIpNode);
            if(!exist){
                zkClient.createEphemeral(currentServiceIpNode);
            }


        }

    }

    @Override
    public void registerProvider(List<ServiceProvider> serivceList) {
        if(serivceList == null || serivceList.size() == 0){
            return;
        }
        
        //连接 zookeeper，注册服务,加锁，将所有需要注册的服务放到providerServiceMap里面
        synchronized (ZookeeperRegisterCenter.class){
            for(ServiceProvider provider:serivceList){
            	//获取接口名称
                String serviceItfKey = provider.getProvider().getName();
                //先从当前服务提供者的集合里面获取
                List<ServiceProvider> providers = providerServiceMap.get(serviceItfKey);
                if(providers == null){
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

            for(Map.Entry<String,List<ServiceProvider>> entry:providerServiceMap.entrySet()){
                //创建服务提供者节点
                String serviceNode = entry.getKey();
                String servicePath = ROOT_PATH +PROVIDER_TYPE +"/" + serviceNode;
                exist = zkClient.exists(servicePath);
                if(!exist){
                    zkClient.createPersistent(servicePath,true);
                }

                //创建当前服务器节点，这里是注册时使用，一个接口对应的ServiceProvider 只有一个 
                int serverPort = entry.getValue().get(0).getPort();
                InetAddress addr = null;
                try {
                    addr = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                String ip = addr.getHostAddress();
                String serviceIpNode = servicePath +"/" + ip + "|" + serverPort;
                System.out.println("serviceIpNode:" + serviceIpNode);
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
                        List<String> activeServiceIpList = new ArrayList<>();
                        for(String input:list){
                        	String ip = StringUtils.split(input, "|").get(0);
                        	activeServiceIpList.add(ip);
                        }
                        refreshActivityService(activeServiceIpList);
                    }
                });

            }
        }

    }

    /**
     * 
     * 在某个服务端获取自己暴露的服务
     */
    @Override
    public Map<String, List<ServiceProvider>> getProviderService() {
        return providerServiceMap;
    }
    
    
    //利用ZK自动刷新当前存活的服务提供者列表数据
    private void refreshActivityService(List<String> serviceIpList) {
        if (serviceIpList == null||serviceIpList.isEmpty()) {
            serviceIpList = new ArrayList<>();
        }

        Map<String, List<ServiceProvider>> currentServiceMetaDataMap = new HashMap<>();
        for (Map.Entry<String, List<ServiceProvider>> entry : providerServiceMap.entrySet()) {
            String key = entry.getKey();
            List<ServiceProvider> providerServices = entry.getValue();

            List<ServiceProvider> serviceMetaDataModelList = currentServiceMetaDataMap.get(key);
            if (serviceMetaDataModelList == null) {
                serviceMetaDataModelList = new ArrayList<>();
            }

            for (ServiceProvider serviceMetaData : providerServices) {
                if (serviceIpList.contains(serviceMetaData.getIp())) {
                    serviceMetaDataModelList.add(serviceMetaData);
                }
            }
            currentServiceMetaDataMap.put(key, serviceMetaDataModelList);
        }
        providerServiceMap.clear();
        providerServiceMap.putAll(currentServiceMetaDataMap);
    }


    private void refreshServiceMetaDataMap(List<String> serviceIpList) {
        if (serviceIpList == null) {
            serviceIpList = new ArrayList<>();
        }

        Map<String, List<ServiceProvider>> currentServiceMetaDataMap = new HashMap<>();
        for (Map.Entry<String, List<ServiceProvider>> entry : serviceData4Consumer.entrySet()) {
            String serviceItfKey = entry.getKey();
            List<ServiceProvider> serviceList = entry.getValue();

            List<ServiceProvider> providerServiceList = currentServiceMetaDataMap.get(serviceItfKey);
            if (providerServiceList == null) {
                providerServiceList = new ArrayList<>();
            }

            for (ServiceProvider serviceMetaData : serviceList) {
                if (serviceIpList.contains(serviceMetaData.getIp())) {
                    providerServiceList.add(serviceMetaData);
                }
            }
            currentServiceMetaDataMap.put(serviceItfKey, providerServiceList);
        }

        serviceData4Consumer.clear();
        serviceData4Consumer.putAll(currentServiceMetaDataMap);
    }


    private Map<String, List<ServiceProvider>> fetchOrUpdateServiceMetaData() {
        final Map<String, List<ServiceProvider>> providerServiceMap = new ConcurrentHashMap<>();
        //连接zk
        synchronized (ZookeeperRegisterCenter.class) {
            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERIVCE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }
        }

        //从ZK获取服务提供者列表
        String providePath = ROOT_PATH+PROVIDER_TYPE;
        System.out.println("111111:"+providePath);
        List<String> providerServices = zkClient.getChildren(providePath);
        System.out.println(providerServices.toString());
        for (String serviceName : providerServices) {
            String servicePath = providePath +"/"+ serviceName;
            System.out.println("1100:"+servicePath);
            List<String> ipPathList = zkClient.getChildren(servicePath);
            System.out.println("ipPathList:"+ipPathList.toString());
            for (String ipPath : ipPathList) {
                System.out.println("444:"+ipPath);
                String serverIp = ipPath.split("\\|")[0];
                String serverPort = ipPath.split("\\|")[1];
                System.out.println("555:"+serverIp);
                System.out.println("666:"+serverPort);
                List<ServiceProvider> providerServiceList = providerServiceMap.get(serviceName);
                if (providerServiceList == null) {
                    providerServiceList = new ArrayList<>();
                }
                ServiceProvider providerService = new ServiceProvider();

                try {
                	Class clazz = Class.forName(serviceName);
                    providerService.setProvider(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                providerService.setIp(serverIp);
                providerService.setPort(Integer.parseInt(serverPort));
                providerService.setGroupName("");
                providerServiceList.add(providerService);

                providerServiceMap.put(serviceName, providerServiceList);
            }

            //监听注册服务的变化,同时更新数据到本地缓存
            zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                @Override
                public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                    if (currentChilds == null) {
                        currentChilds = new ArrayList<>();
                    }
                    List<String> activeServiceIpList = new ArrayList<>();
                    for(String input:currentChilds){
                    	String ip = StringUtils.split(input, "|").get(0);
                    	activeServiceIpList.add(ip);
                    }
                    refreshServiceMetaDataMap(activeServiceIpList);
                }
            });
        }
        return providerServiceMap;
    }

}
