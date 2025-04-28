package Table;

import Database.DatabaseManager;
import Storage.BPlusTree.BpNode;
import Storage.BPlusTree.BpTree;
import Storage.BPlusTree.Tuple;
import Storage.BPlusTree.Value.*;
import Util.Func.Render;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.*;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

/**
 * TableManager类用于管理数据库中的表。
 * 它支持表的创建和删除功能，表的结构定义和数据分别存储在JSON文件中。
 */
public class TableManager {
    private static final String DIRECTORY = "../TestData/DatabaseManager";

    /**
     * 创建新表。
     * @param args 包含表字段定义的Field对象列表。
     */
    // (1) type (2) PRIMARY KEY (3) UNIQUE (4) NOT NULL (5) DEFAULT
    public static void CreateTable(String tableName,ArrayList<Field> args) {
        Schema schema = new Schema(args);
        Table table = new Table(schema);  // 构造空表对象
    }
    /**
     * 删除表。
     * @param tableName 表名。
     * @param userLevel 用户权限等级（1为游客，其他为管理员）。
     */
    public static void DropTable(String tableName, int userLevel) {
        // TODO:根据表名和权限，把文件直接删喽
    }


//    private static JsonArray getConstraints(Field field) {
//        JsonObject type = new JsonObject();
//        JsonObject primaryKey = new JsonObject();
//        JsonObject unique = new JsonObject();
//        JsonObject notNull = new JsonObject();
//        JsonObject Default = new JsonObject();
//        type.addProperty("Type", field.getType());
//        primaryKey.addProperty("PRIMARY KEY", field.isPrimaryKey());
//        unique.addProperty("UNIQUE", field.isUnique());
//        notNull.addProperty("NOT NULL", field.isNotNull());
//        Default.addProperty("Default", field.getDefault());
//        JsonArray constraints = new JsonArray();
//        constraints.add(type);
//        constraints.add(primaryKey);
//        constraints.add(unique);
//        constraints.add(notNull);
//        constraints.add(Default);
//        return constraints;
//    }

    public static List<String> showTables() {
        List<String> tables = new ArrayList<>();
        Path dbDir = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase());

        // 调试：打印当前目录
        System.out.println("[DEBUG] showTables() dbDir = " + dbDir.toAbsolutePath());
        System.out.println("[DEBUG] exists? " + Files.exists(dbDir) + ", isDirectory? " + Files.isDirectory(dbDir));

        if (!Files.isDirectory(dbDir)) {
            System.out.println("[DEBUG] 数据库目录不存在或不是目录，直接返回空列表");
            return tables;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbDir)) {
            for (Path file : stream) {
                String name = file.getFileName().toString();
                // 调试：打印每个文件名
               // System.out.println("[DEBUG] found file: " + name);
                // 调试：判断后缀
                boolean endsSchemaJson = name.endsWith("_schema.json");
                boolean endsSchema     = name.endsWith("_schema");

                String tableName = null;
                if (endsSchemaJson) {
                    tableName = name.substring(0, name.length() - "_schema.json".length());
                } else if (endsSchema) {
                    tableName = name.substring(0, name.length() - "_schema".length());
                }
                if (tableName != null) {
                    //System.out.println("[DEBUG] → 添加表名: " + tableName);
                    tables.add(tableName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("列出表失败: " + dbDir, e);
        }

        // 调试：打印最终结果
        //System.out.println("[DEBUG] showTables() 返回表列表: " + tables);
        return tables;
    }

    public void addColumn(Field newField,Table table){
        if(table.getSchema().getIndex(newField)!=-1){
            throw new IllegalArgumentException("列已存在：" + newField.getName());
        }

        table.getSchema().addColumn(newField);

        // 更新树里的，新列默认都是nullValue
        BpNode current = table.getTree().getHead();
        while (current != null) {
            for (Tuple tuple : current.getEntries()) {
                tuple.appendValue(new NullValue());
            }
            current = current.getNext();
        }
    }

    public void dropColumn(String fieldName,Table table){
        Schema schema = table.getSchema();
        BpTree tree = table.getTree();

        int index = schema.getIndex(fieldName);
        if (index == -1) throw new IllegalArgumentException("列名不存在：" + fieldName);

        schema.dropColumn(fieldName);

        // 遍历改Tuple
        BpNode current  = tree.getHead();
        while(current != null){
            for(Tuple tuple:current.getEntries()){
                tuple.removeValue(index);
            }
            current = current.getNext();
        }
    }

    public void renameColumn(String fieldName, String newFieldName, Table table) {
        Schema schema = table.getSchema();
        int index = schema.getIndex(fieldName);
        if (index == -1) {
            throw new IllegalArgumentException("列名不存在: " + fieldName);
        }
        schema.getField(fieldName).setName(newFieldName);
    }

    public void desc(Table table) {
        Schema schema = table.getSchema();
        ArrayList<Field> fields = schema.getFields();
        ArrayList<String> columnNames = new ArrayList<>();

        for (Field field : fields) {
            columnNames.add(field.getName());
        }

        // 从B+树里取所有元组
        ArrayList<Tuple> tuples = new ArrayList<>();
        BpNode node = table.getTree().getHead();
        while (node != null) {
            tuples.addAll(node.getEntries());
            node = node.getNext();
        }

        Render.DrawSelectedTable(tuples, columnNames);
    }

}
