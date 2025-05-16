package Table;

import java.util.*;

import Storage.Page.Meta;
import Storage.Value.*;

/**
 * Schema类用于描述数据库表的结构。
 * 它包含表名和表中每个字段的规则定义。
 */
public class Schema {
    private ArrayList<Field> fields;
    private String primaryKeyName;

    public Schema(ArrayList<Field> fields) {
        this.fields = fields;
        initializePrimaryKey();
    }

    private void initializePrimaryKey() {
        for (Field field : fields) {
            if (field.isPrimaryKey()) primaryKeyName = field.getName();
        }
    }

    // 将来用来构造Table的
    public static Schema loadSchemaFromMeta(Meta meta) {
        ArrayList<Field> fields = new ArrayList<>();

        for (int i = 0; i < meta.getColumnCount(); i++) {
            String columnName = meta.getColumnNames().get(i);
            int columnType = meta.getColumnTypes().get(i);
            String constraint = meta.getColumnConstraints().get(i);
            String[] constraints = constraint.split(" ");     // 按空格拆分
            boolean isPrimaryKey = false;
            boolean isNotNull = false;
            boolean isUnique = false;
            Value defaultValue = null;

            for (String c : constraints) {
                if (c.equalsIgnoreCase("primaryKey")) {
                    isPrimaryKey = true;
                } else if (c.equalsIgnoreCase("notNull")) {
                    isNotNull = true;
                } else if (c.equalsIgnoreCase("Unique")) {
                    isUnique = true;
                } else if (c.startsWith("Default:")) {
                    String str = c.substring("Default:".length());  // 提取 Default 后的值
                    switch (columnType) {
                        case 1 -> defaultValue = new StringValue(str);
                        case 2 -> defaultValue = new IntValue(Integer.parseInt(str));
                        case 3 -> defaultValue = new LongValue(Long.parseLong(str));
                        case 4 -> defaultValue = new BooleanValue(str.equalsIgnoreCase("true"));
                        case 5 -> defaultValue = new NullValue();
                    }
                }
            }

            // 创建 Field 对象，并设置正确的字段类型
            Field field = new Field(columnName, Field.mapIntToFieldType(columnType));
            field.setPrimaryKey(isPrimaryKey);
            field.setNotnull(isNotNull);
            field.setUnique(isUnique);
            field.setDefault(defaultValue);

            fields.add(field);
        }

        // 创建 Schema 对象
        return new Schema(fields);
    }
    public Field getField(int id){
        return fields.get(id);
    }

    public ArrayList<String> getColumns(){
        ArrayList<String> columns=new ArrayList<>();
        for (Field f:fields){
            columns.add(f.getName());

        }
        return columns;
    }
    public String getPrimaryKey(){
        return this.primaryKeyName;
    }
    public void showAll(){
        for(Field f:fields){
            System.out.println(f.getType());
            System.out.println(f.getName());

        }

    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public boolean hasPrimaryKey() {
        return primaryKeyName != null && !primaryKeyName.isEmpty();
    }

    public void addColumn(Field field) {
        fields.add(field);
    }

    public void dropColumn(String columnName) {
        int index = getIndex(columnName);
        dropColumn(index);
    }

    public void dropColumn(int index) {
        if (index != -1) {
            fields.remove(index);
        }
    }

    public ArrayList<Field> getFields() {
        return this.fields;
    }

    public Field getField(String fieldName) {
        for (Field field : fields) {
            System.out.println("filed in get "+field);
            if (field.getName().equals(fieldName))

                return field;
        }
        return null;
    }

    public int getIndex(Field field) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(field.getName())) return i;
        }
        return -1;
    }

    public int getIndex(String fieldName) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(fieldName)) return i;
        }
        return -1;
    }

    public List<String> getColumnNames() {
        List<String> names = new ArrayList<>();
        for (Field field : fields) {
            names.add(field.getName());
        }
        return names;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }
}
