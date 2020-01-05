import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by bogus on 2019-03-26.
 **/
public class ZkDelete {
    private static ZooKeeper zk;
    private static ZookeeperConnection conn;
    private static String zkHost;

    public static Stat znode_exists(String path) throws KeeperException, InterruptedException {
        return zk.exists(path, true);
    }

    public static void delete(String path) {
        //zk.delete(path, zk.exists(path, true).getVersion());
        List<String> children = null;
        try {
            children = zk.getChildren(path, false);
            if (children.size() > 0) {
                for (String child : children) {
                    delete(path + "/" + child);
                }
                System.out.println("delete Parent Node: " + path);
                zk.delete(path, -1);
            } else {
                System.out.println("delete Parent Node: " + path);
                zk.delete(path, -1);
            }
        } catch (KeeperException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

    }

    public static void main(String[] args) {
        ZkDelete zkDelete = new ZkDelete();
        String path = "/" + args[0];
        zkHost = args[1];
        try {
            conn = new ZookeeperConnection();
            zk = conn.connect(zkHost);
            Stat stat = znode_exists(path);
            if (stat != null) {
                zkDelete.delete(path);
            } else {
                System.out.println("Node does not exists.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
