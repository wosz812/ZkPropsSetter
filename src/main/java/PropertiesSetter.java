import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.*;
import java.util.*;

/**
 * Created by bogus on 2019-03-26.
 **/
public class PropertiesSetter {
    private static ZooKeeper zooKeeper;
    private static ZookeeperConnection conn;


    //Znode 생성 메소드.
    public static void create(String path, byte[] data) throws KeeperException,
            InterruptedException {
        zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);

    }

    public Properties loadProp(String path) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = getClass().getResourceAsStream(path);
        properties.load(inputStream);
        return properties;
    }

    public static void main(String[] args) {
        PropertiesSetter zkCreate = new PropertiesSetter();

        Properties properties = new Properties();
        RwkEncUtil rwkEncUtil = new RwkEncUtil();
        Properties properInfo = new Properties();
        Properties serverInfo = new Properties();
//        String propertiesType = args[0];
        String zkhost = args[0];
        String jdbcUrl = args[1];
        String jdbcId = args[2];
        String jdbcPw = args[3];
//        String kafkaServer = args[5];
//        String hostName = args[6];
        try {
            properInfo.load(new FileReader("hydrakProperties.properties"));
            serverInfo.load(new FileReader("serverInfo.properties"));
        } catch (FileNotFoundException e) {
            System.out.println("File not Found !! " + e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (propertiesType.toUpperCase().equals("CDH")) {
//
//            properInfo = zkCreate.loadProp("cdhProperties.properties");
//            serverInfo = zkCreate.loadProp("serverInfo.properties");
//        } else if (propertiesType.toUpperCase().equals("HDP")) {
//            properInfo = zkCreate.loadProp("hdpProperties.properties");
//            serverInfo = zkCreate.loadProp("serverInfo.properties");
//        }


        properInfo.setProperty("hydrak.jdbc.url", jdbcUrl);
        properInfo.setProperty("hydrak.jdbc.id", jdbcId);
        properInfo.setProperty("hydrak.jdbc.password", rwkEncUtil.Encrypt(jdbcPw, "redwoodk"));
//        properInfo.setProperty("kafka.bootstrap.servers", kafkaServer);
        properInfo.setProperty("hbase.zookeeper.quorum", zkhost);

        Set<String> propertiesKeys = properInfo.stringPropertyNames();
//        for (String key : propertiesKeys) {
//            if (properInfo.get(key).toString().contains("hostname")) {
//                String temp = properInfo.get(key).toString().replace("hostname", hostName);
//                properInfo.setProperty(key, temp);
//            }
//        }

        ArrayList<String> basicZnode = new ArrayList<String>();
        basicZnode.add("/hydrak");
        basicZnode.add("/hydrak/properties");
        basicZnode.add("/hydrak/server_info");
        basicZnode.add("/hydrak/server_info/SAMPLE");


        // DataSet
        byte[] data = "".getBytes();
        // Declare data
        try {

            conn = new ZookeeperConnection();
            zooKeeper = conn.connect(zkhost);
            //BasicZnode Create

            for (int i = 0; i < basicZnode.size(); i++) {
                create(basicZnode.get(i), data);
                System.out.println("Basic Znode : " + basicZnode.get(i));
            }

            //Properties Create
            System.out.println("#################### Properties 정보 ####################");
            for (String key : propertiesKeys) {
                create("/hydrak/properties/" + key, properInfo.get(key).toString().getBytes());
                System.out.println(key + " : " + properInfo.get(key));
            }
            System.out.println(" ");
            //ServerInfo Create
            Set<String> serverInfokeys = serverInfo.stringPropertyNames();
            System.out.println("#################### ServerInfo 정보 ####################");
            for (String key : serverInfokeys) {
                String path = "/hydrak/server_info/SAMPLE/";
                create(path + key, serverInfo.get(key).toString().getBytes());
                System.out.println(key + " : " + serverInfo.get(key));
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
