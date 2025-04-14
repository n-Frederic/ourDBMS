package Table;

import java.util.HashMap;
import java.util.Map;

public class SchemaManager {
    private static SchemaManager instance = new SchemaManager();
    private Map<String, Schema> schemas = new HashMap<>();

    private SchemaManager() {}

    public static SchemaManager getInstance() {
        return instance;
    }

    // 加载 schema（可能是从磁盘，可能是建表时）
    public void register(Schema schema) {
        schemas.put(schema.getTableName(), schema);
    }

    public Schema get(String tableName) {
        return schemas.get(tableName);
    }

    public boolean contains(String tableName) {
        return schemas.containsKey(tableName);
    }
}
