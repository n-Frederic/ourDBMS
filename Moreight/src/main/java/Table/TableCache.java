package Table;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableCache {
    // key: 表名，value: 已加载的 Table 对象（包含数据、schema、B+ 树）
    private static final Map<String, Table> CACHE = new ConcurrentHashMap<>();

    public static Table getTable(String tableName) {
        // 如果已经加载，直接返回
        if (CACHE.containsKey(tableName)) {
            return CACHE.get(tableName);
        }
        // 否则，第一次从磁盘读取并缓存
        synchronized (CACHE) {
            if (!CACHE.containsKey(tableName)) {
                Table t = Table.loadFromDisk(tableName);
                CACHE.put(tableName, t);
            }
            return CACHE.get(tableName);
        }
    }
}
