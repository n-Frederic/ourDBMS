package Main;
import Function.DatabaseManager;
import Function.TableManager;
import java.util.List;
import java.util.ArrayList;
import Function.UserManager;
import Operate.Operating;
public class Main {
    public static void main(String[] args) {
        // 登录注册的测试
       Operating operating = new Operating();
       operating.dbms();

        // 建库建表的测试
//        DatabaseManager.useDatabase("testData");
//        String[] strs1 = new String[2];
//        strs1[0] = "name";
//        strs1[1] = "PRIMARY KEY";
//        String[] strs2 = new String[2];
//        strs2[0] = "age";
//        strs2[1] = "INT";
//        ArrayList<String[]> strs = new ArrayList<>();
//        strs.add(strs1);
//        strs.add(strs2);
//        TableManager.CreateTable("testTable",strs);
//
//        DatabaseManager.showDatabases("testData");

        // 删库显示库的测试
//        DatabaseManager.createDataBase("ndb");
//        DatabaseManager.useDatabase("ndb");
//        List<String> databases = DatabaseManager.showDatabases();
//        System.out.println("Databases: " + databases);
//        DatabaseManager.dropDatabase("ndb",2);

    }

}
