package Table;

import Database.DatabaseManager;
import Storage.BPlusTree.BpTree;
import Storage.Page.*;
import Storage.Value.BooleanValue;
import Storage.Value.NullValue;


import Storage.Value.StringValue;
import Storage.Value.Value;
import Util.Func.Render;
import java.nio.file.*;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * TableManager类用于管理数据库中的表。
 * 它支持表的创建和删除功能，表的结构定义和数据分别存储在JSON文件中。
 */
public class TableManager {
    private static final String DIRECTORY = "../TestData/DatabaseManager";
    private static final String database = DatabaseManager.getCurrentDatabase();

    /**
     * 创建新表。
     * @param args 包含表字段定义的Field对象列表。
     */
    // (1) type (2) PRIMARY KEY (3) UNIQUE (4) NOT NULL (5) DEFAULT
    public static void CreateTable(String tableName, ArrayList<Field> args) {
        try {
            File dir = new File(DIRECTORY+"/"+database+"/"+tableName);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(DIRECTORY + "/" +database + "/" + tableName + "/"+tableName+".idb");
            if (file.exists()) {
                System.out.println("表已存在：" + tableName);
                return;
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            Meta meta = new Meta(args);
            meta.writeMetaToDisk(raf);

            raf.close();

            System.out.println("表 " + tableName + " 创建成功！");
        } catch (IOException e) {
            System.err.println("创建表失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 删除表。
     * @param tableName 表名。
     * @param userLevel 用户权限等级（1为游客，其他为管理员）。
     */
    public static void DropTable(String tableName, int userLevel) throws IOException {
        if (userLevel == 1) {
            System.out.println("权限不足，游客无法删除表。");
            return;
        }
        File tableFile = new File(DIRECTORY + "/" + DatabaseManager.getCurrentDatabase() + "/" + tableName + "/" + tableName + ".idb");
        if (tableFile.exists()) {
            if (tableFile.delete()) {
                System.out.println("表 " + tableName + " 已成功删除。");
            } else {
                System.out.println("删除表 " + tableName + " 失败。");
            }
        } else {
            System.out.println("表 " + tableName + " 不存在。");
        }

        Files.deleteIfExists(Paths.get(DIRECTORY + "/" + DatabaseManager.getCurrentDatabase() + "/" +tableName));
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
                if(Files.isDirectory(file)) {
                    String name = file.getFileName().toString();
                    tables.add(name);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("列出表失败: " + dbDir, e);
        }

        // 调试：打印最终结果
        //System.out.println("[DEBUG] showTables() 返回表列表: " + tables);
        return tables;
    }

    public static void addColumn(Field newField,Table table) throws IOException {
        if(table.getSchema().getIndex(newField)!=-1){
            throw new IllegalArgumentException("列已存在：" + newField.getName());
        }

        PageManager pageManager = table.getPageManager();
        Meta meta = pageManager.getMeta();
        meta.setColumnCount(meta.getColumnCount()+1);
        meta.getColumnNames().add(newField.getName());
        meta.getColumnTypes().add(newField.mapFieldTypeToInt());
        meta.getColumnConstraints().add(newField.constraintToString());
        meta.writeMetaToDisk(pageManager.getPageIO().getFile());

        table.getSchema().addColumn(newField);

        // 更新树里的，新列默认都是nullValue
        Page current = table.getTree().getHead();

        while (current != null) {
            for (Tuple tuple : current.getTuples()) {
                if(newField.getDefault() != null) {
                    tuple.getValues().add(newField.getDefault());
                } else {
                    tuple.getValues().add(new NullValue());
                }
            }
            pageManager.updatePageToManager(current,true);
            if(current.getNext() != null) {
                current = current.getNext();
            } else break;
        }
        pageManager.flushModifiedPages();
    }
    /*
     * table是否被引用*/


    public static void dropColumn(String columnName, Table table) throws IOException {
        int index = table.getSchema().getIndex(columnName);
        if (index == -1) {
            throw new IllegalArgumentException("列不存在：" + columnName);
        }

        PageManager pageManager = table.getPageManager();
        Meta meta = pageManager.getMeta();

        // 更新元数据：删除列定义
        meta.setColumnCount(meta.getColumnCount() - 1);
        meta.getColumnNames().remove(index);
        meta.getColumnTypes().remove(index);
        meta.getColumnConstraints().remove(index);
        meta.writeMetaToDisk(pageManager.getPageIO().getFile());

        // 更新 schema
        table.getSchema().dropColumn(index);

        // 更新所有叶子页的元组数据：删除对应列的值
        Page current = table.getTree().getHead();
        while (current != null) {
            for (Tuple tuple : current.getTuples()) {
                if (tuple.getValues().size() > index) {
                    tuple.getValues().remove(index);
                }
            }
            pageManager.updatePageToManager(current, true);
            if (current.getNext() != null) {
                current = current.getNext();
            } else {
                break;
            }
        }

        pageManager.flushModifiedPages();
    }


    public static void  renameColumn(String fieldName, String newFieldName, Table table) {
        Meta meta = table.getPageManager().getMeta();
        Schema schema = table.getSchema();
        int index = schema.getIndex(fieldName);
        if (index == -1) {
            throw new IllegalArgumentException("列名不存在: " + fieldName);
        }
        schema.getField(fieldName).setName(newFieldName);

        meta.getColumnNames().set(index,newFieldName);

        RandomAccessFile raf = table.getPageManager().getPageIO().getFile();



    }

    public static void modifyColumn(Table table, String name, Field newField) throws IOException {
        Meta meta = table.getPageManager().getMeta();
        Schema schema = table.getSchema();
        List<Field> fieldList = schema.getFields();
        int index = -1;

        // 先处理有无该列
        for (int i = 0; i < fieldList.size(); i++) {
            if (fieldList.get(i).getName().equals(name)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalArgumentException("字段 " + name + " 不存在！");
        }

        // 处理是否有主键冲突
        if (newField.isPrimaryKey()) {
            for (Field field : fieldList) {
                if (field.isPrimaryKey() && field.getName().equals(name)) {
                    throw new IllegalArgumentException("已有主键 " + field.getName() + "，不能设定多个主键！");
                }
            }
        }
        fieldList.set(index, newField);
        if(newField.isPrimaryKey()) schema.setPrimaryKeyName(newField.getName());

        meta.getColumnNames().set(index,newField.getName());
        meta.getColumnTypes().set(index,newField.mapFieldTypeToInt());
        meta.getColumnConstraints().set(index,newField.constraintToString());

        RandomAccessFile raf = table.getPageManager().getPageIO().getFile();
        meta.writeMetaToDisk(raf);
    }

    public static void desc(Table table) throws IOException {
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<Tuple> tuples = new ArrayList<>();
        fieldNames.add("Field");
        fieldNames.add("Type");
        fieldNames.add("Null");
        fieldNames.add("Key");
        fieldNames.add("Default");

        Meta meta = Meta.readMetaFromDisk(table.getPageManager().getPageIO().getFile());
        Schema schema = Schema.loadSchemaFromMeta(meta);
        ArrayList<Field> fields = schema.getFields();

        for(Field field : fields) {
            ArrayList<Value> info = new ArrayList<>();
            info.add(new StringValue(field.getName()));
            info.add(new StringValue(field.getType()));
            info.add(new BooleanValue(!field.isNotNull()));
            info.add(field.isPrimaryKey() ? new StringValue("PRI") : new NullValue());
            info.add(field.getDefault() != null ? field.getDefault() : new NullValue());
            tuples.add(new Tuple(info));
        }

        Render.DrawSelectedTable(tuples,fieldNames);
    }
}
