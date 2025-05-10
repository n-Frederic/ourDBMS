package Table;

import Storage.Value.Value;

public class Field {
    private String name;
    private String type;
    private boolean primaryKey;
    ForeignKey fk;
    private boolean unique;
    private boolean notNull;
    private Value Default;

    public Field(String name, String type) {
        this.name = name;
        this.type = type;
        primaryKey = false;
        fk = new ForeignKey();
        fk.isforeignkey=false;
        fk.referField=null;
        fk.referTable=null;
        unique = false;
        notNull = false;
        Default=null;
    }


    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }


    public boolean isForeignKey(){
        return fk.isforeignkey;
    }
    public String getReferenceTable(){
        return fk.referTable;
    }
    public String getReferenceColumn(){
        return fk.referField;
    }
    public boolean isNotNull() {
        return notNull;
    }

    public void setNotnull(boolean notNUll) {
        this.notNull =notNUll;
    }

    public Value getDefault() {
        return Default;
    }

    public void setDefault(Value aDefault) {
        Default = aDefault;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", primaryKey=" + primaryKey +
                ", unique=" + unique +
                ", notNull=" + notNull +
                ", Default='" + Default + '\'' +
                '}';
    }

    public int mapFieldTypeToInt() {
        return switch (type.toUpperCase()) {
            case "STRING" -> 1;
            case "INT" -> 2;
            case "LONG" -> 3;
            case "BOOLEAN" -> 4;
            case "NULL" -> 5;
            default -> -1;  // 未知类型
        };
    }

    public static String mapIntToFieldType(int i) {
        return switch (i) {
            case 1 -> "STRING";
            case 2 -> "INT";
            case 3 -> "LONG";
            case 4 -> "BOOLEAN";
            case 5 -> "NULL";
            default -> "";  // 未知类型
        };
    }
}


