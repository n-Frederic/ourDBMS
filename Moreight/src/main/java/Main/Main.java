package Main;
import Database.DatabaseManager;
import Table.*;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.gson.*;
import com.google.gson.*;
import java.nio.file.Paths;
import java.util.List;
import Conditions.*;

public class Main {
    public static void main(String[] args) {
        // 登录注册的测试
//       Operating operating = new Operating();
//       operating.dbms();
//
//        String conditionStr;
//        conditionStr = "age > 30 AND (gender = '男' OR (salary >= 5000 AND salay<=10000))";
//        ConditionNode logicTree;
//        List<String> tokens= ConditionParser.tokenizeWhere(conditionStr);
//        logicTree=ConditionParser.parseConditionTree(tokens);
//
//        System.out.println("条件表达式树结构为：");
//        System.out.println(logicTree);

//        // 1. 创建数据库和表
//        DatabaseManager.createDataBase("test_db");
//        DatabaseManager.useDatabase("test_db");
//
//        // 2. 添加新列
//        Schema.ColumnRule ageRule = new Schema.ColumnRule();
//        ageRule.setType("int");
//        ageRule.setDefaultValue("0");
//
//        Table.addColumn("test_db", "users", "age", ageRule);

//        ArrayList<Field> fields = new ArrayList<>();
//        fields.add(new Field("id", "int"));
//        fields.add(new Field("name", "string"));
//        TableManager.CreateTable(tableName, fields);
//
//        // (3) 插入数据
//        ArrayList<String> columns = new ArrayList<>();
//        columns.add("id");
//        columns.add("name");
//
//        ArrayList<Object> values = new ArrayList<>();
//        values.add(1);         // id
//        values.add("张三");     // name
//
//        // 调用插入方法
//        Path dataPath = Table.From_data(tableName);
//        Table.Insert(dataPath, columns, values);



//        // 示例条件表达式
//        String conditionStr = "age > 30 AND (gender = '男' OR salary >= 5000)";
//
//        // 构建条件解析器
//        ConditionParser parser = new ConditionParser(Paths.get(""));
//
//        // 解析表达式为标记列表
//        List<String> tokens = parser.tokenizeWhere(conditionStr);
//
//        // 构建条件表达式树
//        ConditionNode root = parser.parseConditionTree(tokens);
//
//
//        // 设置数据源
//        //Condition.setData(data);
//
//        // 评估条件表达式树并筛选数据
//        JsonArray filteredData = ConditionEvaluator.evaluateConditions(root);
//
//        // 输出筛选结果
//        System.out.println("筛选结果：");
//        for (JsonElement element : filteredData) {
//            System.out.println(element.toString());
//        }


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
//        DatabaseManager.useDatabase("testDB");
//        Path datapath =Table.From_data("student");
//        JsonArray records=Table.Where(datapath,new Condition("Ssex","女","="));
//
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("Sno");
//        arrayList.add("Sname");
//        arrayList.add("Ssex");
//        Map<String,Integer> map=new LinkedHashMap<>();
//        Table.DrawSelectedTable(records,arrayList,map);
//


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
