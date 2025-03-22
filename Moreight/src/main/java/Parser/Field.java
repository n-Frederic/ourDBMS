package Parser;

public class Field {
    private String name;
    private String type;
    private boolean primaryKey;
    private boolean unique;
    private boolean notNUll;
    private String Default;

    public Field(String name, String type) {
        this.name = name;
        this.type = type;
        primaryKey = false;
        unique = false;
        notNUll = false;
        notNUll = false;
        Default = "";
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

    public boolean isNotnull() {
        return notNUll;
    }

    public void setNotnull(boolean notNUll) {
        this.notNUll =notNUll;
    }

    public String getDefault() {
        return Default;
    }

    public void setDefault(String aDefault) {
        Default = aDefault;
    }
}
