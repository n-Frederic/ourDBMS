package Controller;

import Conditions.Condition;
import Conditions.ConditionNode;
import Conditions.ConditionParser;
import Table.*;

import Database.DatabaseManager;
import Table.TableManager;
import User.UserManager;
import Parser.commandParser;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Table.Table;
import Util.Func.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.swing.text.html.parser.Parser;

public class Operating {

    private static final Pattern PATTERN_INSERT = Pattern.compile("(?i)insert\\s+into\\s+(\\w+)\\s*\\(([^\\)]+)\\)\\s*values\\s*\\(([^\\)]+)\\);?");

    private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile("(?i)create\\s+table\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;");
    // 说明：(?i)表示不区分大小写；(?:column\s+)? 表示可选的 "column" 关键字及其后的空白字符
    private static final Pattern PATTERN_ALTER_TABLE = Pattern.compile(
            "(?i)alter\\s+table\\s+(\\w+)\\s+" +            // group1: 表名
                    "(add|drop|modify|change|rename)\\s+" +           // group2: 操作类型
                    "(?:column\\s+)?" +                              // 可选的 column 关键字（不捕获）
                    "(\\w+)" +                                      // group3: 列名（或重命名前的旧列名）
                    "(?:\\s+(.*?))?\\s*;"                           // group4: 其余部分，如列定义、数据类型、约束等（可选），以非贪婪方式匹配直到分号
    );


    // SHOW DATABASES;
    private static final Pattern PATTERN_SHOW_DATABASES =
            Pattern.compile("(?i)^\\s*SHOW\\s+DATABASES\\s*;*\\s*$");

    // SHOW TABLES;
    private static final Pattern PATTERN_SHOW_TABLES =
            Pattern.compile("(?i)^\\s*SHOW\\s+TABLES\\s*;?\\s*$");

    private static final Pattern PATTERN_DELETE = Pattern.compile("(?i)delete\\s+from\\s(\\w+)(?:\\s+where\\s([^\\;]+\\s?;))?");
    private static final Pattern PATTERN_UPDATE = Pattern.compile("(?i)^\\s*UPDATE\\s+" + "([\\w\\.]+)\\s+" + "SET\\s+" + "(.+?)" + "(?:\\s+FROM\\s+(.+?))?" + "(?:\\s+WHERE\\s+(.+?))?" + "\\s*;?\\s*$");

    private static final Pattern PATTERN_DROP_TABLE = Pattern.compile("(?i)drop\\s+table\\s+(\\w+)\\s*;?");

    private static final Pattern PATTERN_SELECT = Pattern.compile("(?i)select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+(?:\\.\\w+)?)*))\\s+from\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\s+where\\s([^\\;]+\\s?;))?");
    private static final Pattern PATTERN_DELETE_INDEX = Pattern.compile("(?i)delete\\s+index\\s(\\w+)\\s?;");
    private static final Pattern PATTERN_GRANT_ADMIN = Pattern.compile("(?i)grant\\s+admin\\s+to\\s([^;\\s]+)\\s?;");
    private static final Pattern PATTERN_REVOKE_ADMIN = Pattern.compile("(?i)revoke\\s+admin\\s+from\\s([^;\\s]+)\\s?;");
    private static final Pattern PATTERN_CREATE_DATABASE = Pattern.compile("(?i)create\\s+database\\s+(\\w+)\\s*;");
    private static final Pattern PATTERN_USE_DATABASE = Pattern.compile("(?i)use\\s+(\\w+)\\s*;");
    private static final Pattern PATTERN_DROP_DATABASE = Pattern.compile("(?i)drop\\s+database\\s+(\\w+)\\s*;");


    private static Scanner sc = new Scanner(System.in);
    private boolean login = false;
    private boolean enter_database = false;


    public void dbms() {

        do {
            System.out.println("欢迎使用 SimpleDBMS");
            System.out.println("1. 登录");
            System.out.println("2. 注册");
            System.out.print("请输入选项：");

            String choice = sc.nextLine();
            if ("1".equals(choice)) {
                login=UserAuthentication.login(sc);
            } else if ("2".equals(choice)) {
                login=UserAuthentication.register(sc);
            } else {
                System.out.println("无效选项，程序退出。");
                return;
            }

        } while (!login);

        Scanner sc = new Scanner(System.in);
        String cmd;
        while (!"exit".equals(cmd = sc.nextLine()) && enter_database == false) {

            boolean matched = false;  // 标记是否匹配成功
            Matcher matcherCreateDB = PATTERN_CREATE_DATABASE.matcher(cmd);
            Matcher matcherUserDB = PATTERN_USE_DATABASE.matcher(cmd);
            Matcher matcherDropDB = PATTERN_DROP_DATABASE.matcher(cmd);
            Matcher matcherShowDB=PATTERN_SHOW_DATABASES.matcher(cmd);


            if (matcherCreateDB.find()) {
                matched = true;
                String dbName = matcherCreateDB.group(1);
                System.out.println("创建数据库: " + dbName);

                DatabaseManager.createDataBase(dbName);
                // 这里你可以调用 parseCreateDatabase(cmd) 或执行创建逻辑
                continue;
            } else if (matcherUserDB.find()) {

                matched = true;
                String dbName = matcherUserDB.group(1);
                //System.out.println("使用数据库: " + dbName);
                boolean dbexist = false;
                dbexist = DatabaseManager.useDatabase(dbName);
                if (dbexist) {
                    enter_database = true;
                    System.out.println("使用数据库: " + dbName);
                    break;
                } else {
                    System.out.println("数据库不存在");
                }
                // 设置 enter_database = true，表示已进入数据库

                continue;
            } else if (matcherDropDB.find()) {

                matched = true;
                String dbName = matcherDropDB.group(1);


                System.out.println("删除数据库: " + dbName);
                DatabaseManager.dropDatabase(dbName, UserManager.GetCurrentUser().getLevel());
                // 执行删除逻辑
                continue;
            }else if(matcherShowDB.find()){
                System.out.println("show");

                List databases=DatabaseManager.listDatabases();
                Render.drawDatabaseList(databases);
                matched=true;

            } else if (!matched) {
                System.out.println("无效命令，请重新输入。");
                continue;
            }



        }
        System.out.println("请输入sql语句");
        while (!"exit".equals(cmd = sc.nextLine())) {


            boolean matched = false;  // 标记是否匹配成功
            Matcher matcherCreateTable = PATTERN_CREATE_TABLE.matcher(cmd);
            Matcher matcherDropTable = PATTERN_DROP_TABLE.matcher(cmd);
            Matcher matcherSelectTable = PATTERN_SELECT.matcher(cmd);
            Matcher matcherInsertTable = PATTERN_INSERT.matcher(cmd);
            Matcher matcherAlterTable = PATTERN_ALTER_TABLE.matcher(cmd);
            Matcher matcherDelete = PATTERN_DELETE.matcher(cmd);
            Matcher matcherUpdate = PATTERN_UPDATE.matcher(cmd);
            Matcher matcherShowTB=PATTERN_SHOW_TABLES.matcher(cmd);


            if (matcherCreateTable.find()) {
                System.out.println("create");
                matched = true;

                // ✅ 取出表名
                String tableName = matcherCreateTable.group(1);

                // ✅ 取出字段定义并解析
                String fieldsStr = matcherCreateTable.group(2);
                ArrayList<Field> fieldList = commandParser.parseCreateTable(fieldsStr);


                if (fieldList == null) {


                } else {
                    System.out.println("创建表: " + tableName);
                    for (Field f : fieldList) {
                        System.out.println("字段: " + f.getName() + ", 类型: " + f.getType());
                    }
                    TableManager.CreateTable(tableName, fieldList);
                }
                continue;

            } else if (matcherDropTable.find()) {
                System.out.println("drop");
                // matched = true;
                String tableName = matcherDropTable.group(1);  //
                System.out.println("删除表: " + tableName);     //
                TableManager.DropTable(tableName, 2);
                continue;

            } else if (matcherSelectTable.find()) {
                System.out.println("select");
                //matched = true;
                select(matcherSelectTable);


                continue;
            } else if(matcherShowTB.find()){
                System.out.println("tables:");
                List<String >tables=TableManager.showTables();
                Render.drawTablesList(tables);
                continue;


            } else if (matcherInsertTable.find()) {
                System.out.println("insert");
                //matched = true;
                insert(matcherInsertTable);
                continue;

            } else if (matcherAlterTable.find()) {

                System.out.println("alter");
                alter(matcherAlterTable);
                // matched = true;
                continue;


            } else if (matcherDelete.find()) {
                String tableName = matcherDelete.group(1);
                String conditionstr = matcherDelete.group(2);
                ArrayList<Condition> conditions;

                Path fpath=Table.From_data(tableName);
                ConditionParser parser=new ConditionParser(fpath);
                parser.tokenizeWhere(matcherSelectTable.group(3));


            } else if (matcherUpdate.find()) {
                String tableName;
                String conditionstr;

                update(matcherUpdate);

                matched = true;
                continue;


            }

            if (!matched) {
                System.out.println("错误输入: " + cmd);  // 调试输出，查看具体输入的命令
                continue;
            }

            System.out.println("matched?" + matched);

        }


    }




//        private void createDB(Matcher matcherCreateTable) {
//                String tableName = matcherCreate.group(1);
//                String propertys = matcherCreateTable.group(2);
//                Map<String, Field> fieldMap = StringUtil.parseCreateTable(propertys);
//                System.out.println(TableManager.CreateTable(tableName, fieldMap));
//        }

    private void dropDB(Matcher matcherDropTable) {
        String tableName = matcherDropTable.group(1);
//                System.out.println(TableManager.DropTable(tableName));
    }

    private void useDB(Matcher matcherDropTable) {
        String tableName = matcherDropTable.group(1);
//                System.out.println(Table.dropTable(tableName));
    }

    public static boolean checkType(JsonElement value, String expectedType) {
        switch (expectedType.toLowerCase()) {
            case "int":
                return value.isJsonPrimitive() && ((JsonPrimitive) value).isNumber() &&
                        value.getAsJsonPrimitive().getAsString().matches("-?\\d+");
            case "float":
                return value.isJsonPrimitive() && ((JsonPrimitive) value).isNumber();
            case "string":
                return value.isJsonPrimitive() && ((JsonPrimitive) value).isString();
            case "boolean":
                return value.isJsonPrimitive() && ((JsonPrimitive) value).isBoolean();
            default:
                return false;
        }
    }


    private void select(Matcher matcherSelect) {
        String tableName = matcherSelect.group(2);
        Path path=Table.From_data(tableName);
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Condition> conditions = new ArrayList<>();

        JsonArray data;


//
        String columnsStr = matcherSelect.group(1);
        if (columnsStr.equals("*")) {
            Schema schema=Schema.loadSchema(DatabaseManager.getCurrentDatabase(),tableName);
            columns = new ArrayList<>();
            for (Field field : schema.getFields()) {
                columns.add(field.getName());
            }

        } else {
            columns = commandParser.parseSelectColumn(columnsStr);

        }
        if(!(matcherSelect.group(3)==null)){
            System.out.println("with conditions");
            String conditionStr = matcherSelect.group(3).toLowerCase().trim();
            conditionStr=commandParser.parseBetweenAnd(conditionStr);
            ConditionNode logicTree;
            ConditionParser parser=new ConditionParser(Table.From_data(tableName));
            List<String> tokens = parser.tokenizeWhere(conditionStr);
            logicTree = parser.parseConditionTree(tokens);
            data=logicTree.evaluate();
            System.out.println("条件表达式树结构为：");
            System.out.println(logicTree);


        }else{

            data=Table.readData(path);

        }


//
        // System.out.println("conditions: " + conditions);


        System.out.println(columns);
        Render.DrawSelectedTable(data,columns);
        //Table.SelectFromTable(tableName,columns,conditions);

        //Table.From(tableName);
        //Table.DrawSelectedTable();


    }


    private void alter(Matcher matcherAlter){

        //System.out.println("altering1");
        String tableName;
        String details;
        String column;
        String operation;
        tableName = matcherAlter.group(1);
        System.out.println(tableName);

        column = matcherAlter.group(3);//name
        System.out.println(column);

        operation = matcherAlter.group(2);
        System.out.println(operation);// "add"



        if(operation.equals("add")||operation.equals("ADD")){
            //System.out.println("adding");
            details=matcherAlter.group(4);
            //System.out.println("detail");
            Field field=new Field(column,details);

            Table.addColumn(tableName,field);
        }else if(operation.equals("drop")||operation.equals("DROP")){
            Table.deleteColumn(tableName,column);

        }else if(operation.equals("modify")||operation.equals("MODIFY")){
            details=matcherAlter.group(4);
            Field field=new Field(column,details);


        }
        // "age int

//        switch (operation){
//            case "add":
//                ArrayList<String ,String>fields = commandParser.parseCreateTable(fieldString);
//
//        }
        ArrayList<Field> fields = new ArrayList<>();
        //fields = commandParser.parseCreateTable(fieldString);

    }


    private void update(Matcher mathcerUpdate){
        String tableName=mathcerUpdate.group(1);
        String statement=mathcerUpdate.group(2);
        String conditionStr=mathcerUpdate.group(4);
        JsonArray data;
        System.out.println(":"+tableName+":"+statement+":"+conditionStr);

        conditionStr=commandParser.parseBetweenAnd(conditionStr);
        ConditionNode logicTree;
        ConditionParser parser=new ConditionParser(Table.From_data(tableName));
        List<String> tokens = parser.tokenizeWhere(conditionStr);
        logicTree = parser.parseConditionTree(tokens);
        System.out.println(logicTree);
        //data=logicTree.evaluate();

        HashMap<String,String> statements=commandParser.parseUpdateSet(statement);
        Table.Set(tableName,statements,logicTree);


    }
    private void insert(Matcher matcherInsert) {
        String tableName = matcherInsert.group(1);
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();


        String columnsStr = matcherInsert.group(2);
        String valuesStr = matcherInsert.group(3);

        columns = commandParser.parseInsertColumn(columnsStr);
        values = commandParser.parseInsertValue(valuesStr);


        System.out.println("Table: " + tableName);
        System.out.println("Columns: " + columns);
        System.out.println("Values: " + values);

        Path tablepath=Table.From_data(tableName);
        Table.Insert(tablepath, columns, values);

//                Map dictMap = table.getFieldMap();
//                Map<String, String> data = new HashMap<>();
//
//                String[] fieldValues = matcherInsert.group(5).trim().split(",");
//                //如果插入指定的字段
//                if (null != matcherInsert.group(2)) {
//                        String[] fieldNames = matcherInsert.group(3).trim().split(",");
//                        //如果insert的名值数量不相等，错误
//                        if (fieldNames.length != fieldValues.length) {
//                                return;
//                        }
//                        for (int i = 0; i < fieldNames.length; i++) {
//                                String fieldName = fieldNames[i].trim();
//                                String fieldValue = fieldValues[i].trim();
//                                //如果在数据字典中未发现这个字段，返回错误
//                                if (!dictMap.containsKey(fieldName)) {
//                                        return;
//                                }
//                                data.put(fieldName, fieldValue);
//                        }
//                } else {//否则插入全部字段
//                        Set<String> fieldNames = dictMap.keySet();
//                        int i = 0;
//                        for (String fieldName : fieldNames) {
//                                String fieldValue = fieldValues[i].trim();
//
//                                data.put(fieldName, fieldValue);
//
//                                i++;
//                        }
//                }
//                table.insert(data);
    }
}




