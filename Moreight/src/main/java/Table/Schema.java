package Table;

import Storage.BPlusTree.Tuple;
import Storage.BPlusTree.BpTree;
import Storage.BPlusTree.Value.Value;
import com.google.gson.*;

import java.io.*;
import java.util.*;

/**
 * Schema类用于描述数据库表的结构。
 * 它包含表名和表中每个字段的规则定义。
 * 该类支持从JSON文件中加载表结构定义。
 */
public class Schema {
    private List<String> columnNames;
    private List<Class<? extends Value>> columnTypes;
    private ArrayList<Field> fields;
    private BpTree bpTree;
    private String primaryKeyName;


    public Schema(ArrayList<String> names, List<Class<?extends Value>>types) {
        this.columnNames = names;
        this.columnTypes = types;
        this.bpTree = new BpTree();
        initializePrimaryKey();
    }

    private void initializePrimaryKey() {
        for(Field field : fields) {
            if(field.isPrimaryKey()) primaryKeyName = field.getName();
        }
    }
    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<Class<? extends Value>> getColumnTypes() {
        return columnTypes;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public boolean hasPrimaryKey() {
        return primaryKeyName != null && !primaryKeyName.isEmpty();
    }

    public void addColumn(Field field) {
        fields.add(field);
        columnNames.add(field.getName());
        columnTypes.add(field.getTypeClass());
    }

    public void dropColumn(String columnName) {
        int index = columnNames.indexOf(columnName);
        if (index != -1) {
            fields.remove(index);
            columnNames.remove(index);
            columnTypes.remove(index);
        }
    }


//    public static Schema loadSchema(String dbName, String tableName) {
//        File schemaFile = new File("../TestData/DatabaseManager/" + dbName + "/" + tableName + "_schema.json");
//        try (FileReader reader = new FileReader(schemaFile)) {
//            JsonObject schemaObject = JsonParser.parseReader(reader).getAsJsonObject();
//            JsonArray fieldsArray = schemaObject.getAsJsonArray("fields");
//
//            ArrayList<Field> columns = new ArrayList<>();
//            for (JsonElement fieldElement : fieldsArray) {
//                JsonObject fieldObj = fieldElement.getAsJsonObject();
//                String fieldName = fieldObj.get("fieldName").getAsString();
//                JsonArray constraints = fieldObj.getAsJsonArray("constraint");
//
//                Field field = new Field(fieldName, "");
//                for (JsonElement constraint : constraints) {
//                    JsonObject c = constraint.getAsJsonObject();
//                    if (c.has("Type")) field.setType(c.get("Type").getAsString());
//                    if (c.has("PRIMARY KEY")) field.setPrimaryKey(c.get("PRIMARY KEY").getAsBoolean());
//                    if (c.has("UNIQUE")) field.setUnique(c.get("UNIQUE").getAsBoolean());
//                    if (c.has("NOT NULL")) field.setNotnull(c.get("NOT NULL").getAsBoolean());
//                    if (c.has("Default")) field.setDefault(c.get("Default").getAsString());
//                }
//                columns.add(field);
//            }
//            return new Schema(tableName, columns);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public ArrayList<Field> getFields(){
        return this.fields;
    }

    public Field getField(String fieldName) {
        for(Field field : fields) {
            if(field.getName().equals(fieldName))
                return field;
        }
        return null;
    }

    public int getIndex(Field field) {
        for(int i = 0; i < fields.size(); i++) {
            if(fields.get(i).getName().equals(field.getName())) return i;
        }
        return -1;
    }
}
