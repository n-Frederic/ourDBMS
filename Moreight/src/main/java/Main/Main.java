package Main;
import Function.DatabaseManager;
import Function.TableManager;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import Function.UserManager;
import Operate.Operating;
import Operate.Condition;
import Operate.Table;
import Parser.Field;
import Parser.StringParser;
import com.google.gson.JsonArray;


public class Main {
    public static void main(String[] args) {
        // 登录注册的测试
//       Operating operating = new Operating();
//       operating.dbms();


        // 建库建表的测试
//        DatabaseManager.createDataBase("20250324testDB");
//        DatabaseManager.useDatabase("20250324testDB");
//        Field field = new Field("Sno","VARCHAR(8)");
//        Field field1 = new Field("Sname", "VARCHAR(8)");
//        Field field2 = new Field("Ssex","VARCHAR(4)");
//        Field field3 = new Field("Sbirthday", "VARCHAR(10)");
//        ArrayList<Field> fields = new ArrayList<>();
//        fields.add(field);
//        fields.add(field1);
//        fields.add(field2);
//        fields.add(field3);
//        TableManager.CreateTable("student",fields);

//        DatabaseManager.showDatabases();

        // 删库显示库的测试
//        DatabaseManager.createDataBase("ndb");
//        DatabaseManager.useDatabase("ndb");
//        List<String> databases = DatabaseManager.showDatabases();
//        System.out.println("Databases: " + databases);
//        DatabaseManager.dropDatabase("ndb",2);

        // 插入记录的测试
//        DatabaseManager.useDatabase("20250324testDB");
//        ArrayList<String> columns = new ArrayList<>();
//        columns.add("Sno");
//        columns.add("Sname");
//        columns.add("Ssex");
//        columns.add("Sbirthday");
//        ArrayList<Object> value = new ArrayList<>();
//        value.add("23301116");
//        value.add("周学超");
//        value.add("男");
//        value.add("2005-01-16");
//        ArrayList<Object> value1 = new ArrayList<>();
//        value1.add("23301111");
//        value1.add("未知");
//        value1.add("女");
//        value1.add("");
//        Table.InsertIntoValue("student", columns, value);
//        Table.InsertIntoValue("student",columns,value1);

        // 测试select
        DatabaseManager.useDatabase("testDB");
        Path datapath =Table.From("student");
        JsonArray records=Table.Where(datapath,new Condition("Ssex","女","="));

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Sno");
        arrayList.add("Sname");
        arrayList.add("Ssex");
        Map<String,Integer> map=new LinkedHashMap<>();
        Table.DrawSelectedTable(records,arrayList,map);



//        Table.SelectFromTable("student");
//        ArrayList<String> columns = new ArrayList<>();
//        columns.add("Ssex");
//        ArrayList<Object> values = new ArrayList<>();
//        values.add("男");
//        Table.SelectFromTable("student",columns,values);

//        //测试CELECT 列名部分
//        String str1="count(*) as total, sum(price)";
//        String str2="name,age";
//        System.out.println(str2);
//        ArrayList<String> test = StringParser.parseSelectColumn(str2);
//        for (String Str : test) {
//            System.out.println(Str);
//        }

//        //测试where
//        String str1="name='Alice'";
//        String str2="age > 20 AND status = 'active'";
//        System.out.println(str2);
//        ArrayList<Condition> test = StringParser.parseWhere(str2);
//        for (Condition condition : test) {
//            System.out.println(condition.toString());
//        }




//        System.out.println("中文");
//        System.out.println("yeye");
    }

}
