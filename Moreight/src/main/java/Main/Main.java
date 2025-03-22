package Main;
import Function.DatabaseManager;
import Function.TableManager;
import java.util.List;
import java.util.ArrayList;
import Function.UserManager;
import Operate.Operating;
import Operate.Table;
import Parser.Field;

public class Main {
    public static void main(String[] args) {
        // 登录注册的测试
//       Operating operating = new Operating();
//       operating.dbms();

        // 建库建表的测试
//        DatabaseManager.createDataBase("testDB");
//        DatabaseManager.useDatabase("testData");
//        Field field1 = new Field("name", "VARCHAR(8)");
//        Field field2 = new Field("age", "INT");
//        ArrayList<Field> fields = new ArrayList<>();
//        fields.add(field1);
//        fields.add(field2);
//        TableManager.CreateTable("testTable",fields);
//
//        DatabaseManager.showDatabases();

        // 删库显示库的测试
//        DatabaseManager.createDataBase("ndb");
//        DatabaseManager.useDatabase("ndb");
//        List<String> databases = DatabaseManager.showDatabases();
//        System.out.println("Databases: " + databases);
//        DatabaseManager.dropDatabase("ndb",2);

        // 插入记录的测试
        DatabaseManager.useDatabase("testData");

        ArrayList<String> columns = new ArrayList<>();
        columns.add("name");
        columns.add("age");

        ArrayList<Object> values = new ArrayList<>();
        values.add("Alice");
        values.add(25);

        Table.InsertIntoValue("testTable", columns, values);

    }

}
