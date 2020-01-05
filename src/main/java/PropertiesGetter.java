import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by bogus on 2019-07-08.
 **/
public class PropertiesGetter {
    private static ZooKeeper zooKeeper;
    private static ZookeeperConnection conn;

    public static void getConfigs(String path, String key, Map<String, Object> parentMap) {
        List<String> childs = null;
        try {
            childs = zooKeeper.getChildren(path, null);
            if (childs.size() > 0) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (String child : childs) {
                    parentMap.put(key, map);
                    getConfigs(path + "/" + child, child, map);
                }
            } else {
                parentMap.put(key, new String(zooKeeper.getData(path, null, null)));
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static Properties getPropertiesData() {
        Properties readConf = new Properties();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        getConfigs("/hydrak/properties", "properties", resultMap);
        Map<String, String> propertiesData = (Map<String, String>) resultMap.get("properties");
        for (Map.Entry<String, String> entry : propertiesData.entrySet()) {
            readConf.put(entry.getKey(), entry.getValue());
        }
        return readConf;
    }

    public static void main(String args[]) {
        Properties properties = new Properties();
        try {
            conn = new ZookeeperConnection();
            zooKeeper = conn.connect(args[0]);
            properties = getPropertiesData();
            properties.store(new FileWriter("hydrakProperties.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
