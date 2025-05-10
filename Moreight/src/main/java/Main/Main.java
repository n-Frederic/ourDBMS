package Main;

import Controller.Operating;
import Storage.Page.Meta;
import Storage.Value.IntValue;
import Storage.Value.StringValue;
import Storage.Value.Value;
import Table.Field;
import Table.TableManager;
import Table.Table;
import Storage.Page.Tuple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;
import java.util.RandomAccess;

public class Main {
    public static void main(String[] args) throws IOException {

        TableManager tm = new TableManager();
        ArrayList<Field> fields = new ArrayList<>();

        fields.add(new Field("id", "INT"));
        fields.add(new Field("name", "STRING"));
        fields.add(new Field("age", "INT"));

        TableManager.CreateTable("student", fields);

        RandomAccessFile raf = new RandomAccessFile("../TestData/DatabaseManager/student/student.idb","rw");


        Table table = new Table("student");

        Value[] values = {new IntValue(1),new StringValue("zxc"),new IntValue(20)};
        Tuple t1 = new Tuple(values);
        table.insert(t1);


//        System.out.println("rootPageId : " + meta.getRootPageId());
//        System.out.println("highestPageId : " + meta.getHighestPageId());
//        System.out.println("MaxKeys : " +meta.getMaxKeys());

//        raf.seek(0);
//        for(int i = 0; i < 4; i++) {
//            System.out.println(raf.readInt());
//        }
//
//        int offset = 1024;
//
//        raf.seek(1024);
//        for(int i = 0; i < 3; i++) {
//            int size = raf.readInt();
//            System.out.println("nameSize : " + size);
//
//            byte[] bytes = new byte[size];
//            raf.readFully(bytes);
//            System.out.println("name : " + new String(bytes));
//
//            int type = raf.readInt();
//            System.out.println("type : " + type);
//
//            int size1 = raf.readInt();
//            System.out.println("constraintSize : " + size1);
//
//            byte[] bytes1 = new byte[size1];
//            raf.readFully(bytes1);
//            System.out.println("constraints :" + new String(bytes1));
//
//            int bytesRead = 4 + size + 4 + 4 + size1;
//            int toSkip = 128 - bytesRead;
//            if (toSkip > 0) {
//                raf.skipBytes(toSkip);
//            }
//        }

        // 登录注册的测试
//       Operating operating = new Operating();
//       operating.dbms();

//       UI ui=new UI();
//       ui.createUI();


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


    }
}
