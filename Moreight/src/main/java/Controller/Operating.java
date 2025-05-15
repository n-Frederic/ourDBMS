package Controller;

import Conditions.Condition;
import Conditions.ConditionNode;
import Conditions.ConditionParser;
import Storage.Page.Tuple;
import Storage.Value.Value;
import Table.*;

import Database.DatabaseManager;
import Table.TableManager;
//import UI.UI.CommandHandler;
import User.UserManager;
import Parser.commandParser;
//import UI.UI;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Storage.Value.NullValue;
import Table.Table;
import Util.Func.*;
import Util.Filter.*;
import javax.swing.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class Operating { // implements CommandHandler

    private static final Pattern PATTERN_INSERT = Pattern.compile("(?i)insert\\s+into\\s+(\\w+)\\s*\\(([^\\)]+)\\)\\s*values\\s*\\(([^\\)]+)\\);?");

    private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile(
            "(?i)create\\s+table\\s+(\\w+)\\s*\\(((?:\\s*\\w+\\s+\\w+(?:\\([^)]+\\))?\\s*,\\s*)*\\w+\\s+\\w+(?:\\([^)]+\\))?\\s*)\\);"
    );
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

    private static final Pattern PATTERN_SELECT = Pattern.compile(
            "(?i)^\\s*SELECT\\s+" +                     // SELECT 关键字
                    "(.+?)\\s+" +                               // group(1): 列名列表
                    "FROM\\s+" +                                // FROM 关键字
                    "(\\w+)" +                                  // group(2): 表名
                    "(?:\\s+WHERE\\s+(.+?))?" +                 // group(3): 可选的 WHERE 条件
                    "(?:\\s+GROUP\\s+BY\\s+(.+?))?" +          // group(4): 可选的 GROUP BY 子句
                    "\\s*;?\\s*$"                              // 可选的结尾分号
    );
    private static final Pattern PATTERN_DELETE_INDEX = Pattern.compile("(?i)delete\\s+index\\s(\\w+)\\s?;");
    private static final Pattern PATTERN_GRANT_ADMIN = Pattern.compile("(?i)grant\\s+admin\\s+to\\s([^;\\s]+)\\s?;");
    private static final Pattern PATTERN_REVOKE_ADMIN = Pattern.compile("(?i)revoke\\s+admin\\s+from\\s([^;\\s]+)\\s?;");
    private static final Pattern PATTERN_CREATE_DATABASE = Pattern.compile("(?i)create\\s+database\\s+(\\w+)\\s*;");
    private static final Pattern PATTERN_USE_DATABASE = Pattern.compile("(?i)use\\s+(\\w+)\\s*;");
    private static final Pattern PATTERN_DROP_DATABASE = Pattern.compile("(?i)drop\\s+database\\s+(\\w+)\\s*;");
    private static final Pattern PATTERN_DESC =
            Pattern.compile("(?i)^\\s*(DESC|DESCRIBE)\\s+(\\w+)(\\s*;)?\\s*$");




    private static Scanner sc = new Scanner(System.in);
    private boolean login = false;
    private boolean enter_database = false;

    //private UI ui; // 定义 UI 对象

    //ui实验
    String cmd1;
    public Schema schema;

    public Operating(String cmd1){
        this.cmd1=cmd1;
    }
    public Operating(){

    }


    public void dbms() throws IOException {

        do {
            System.out.println("欢迎使用 SimpleDBMS");
            System.out.println("1. 登录");
            System.out.println("2. 注册");

            System.out.print("请输入选项：");

            String choice = sc.nextLine();
            //String choice=cmd1;
            if ("1".equals(choice)) {
                login=UserAuthentication.login(sc);
                System.out.println(login);
            } else if ("2".equals(choice)) {
                login=UserAuthentication.register(sc);
                System.out.println(login);
            }
            else {
                System.out.println("无效选项，程序退出。");
                return;
            }

        } while (!login);
        System.out.println("成功登录，欢迎"+UserManager.getCurrentUser().getUserName());
//
//        SwingUtilities.invokeLater(() -> { // 在 Swing 线程中执行 UI 相关操作
//            ui = new UI(this); // 创建 UI 对象并传入自身作为命令处理
//        });
//        if(UserManager.getCurrentUser().hasPermission("admin")){
//            //System.out.println("start permission");
//           // System.out.println(UserAuthentication.quit);
//            while(!UserAuthentication.quit){
//                UserAuthentication.permissionManagement(sc);
//            }
//
//        }





        Scanner sc = new Scanner(System.in);
        String cmd;
        //尝试把这里的命令行输入变为ui里传来的字符串
        while (!"exit".equals(cmd = sc.nextLine()) && enter_database == false) {
            System.out.println("=== 数据库操作 ===");

            boolean matched = false;  // 标记是否匹配成功
            Matcher matcherCreateDB = PATTERN_CREATE_DATABASE.matcher(cmd);
            Matcher matcherUserDB = PATTERN_USE_DATABASE.matcher(cmd);
            Matcher matcherDropDB = PATTERN_DROP_DATABASE.matcher(cmd);
            Matcher matcherShowDB=PATTERN_SHOW_DATABASES.matcher(cmd);


            if (matcherCreateDB.find()) {
                matched = true;
                String dbName = matcherCreateDB.group(1);

                if(TypeFilter.databaseExist(dbName)){
                    System.out.println(dbName+" 已经存在!");
                    continue;
                }
                if(UserManager.getCurrentUser().ddlOK()){
                    System.out.println("创建数据库: " + dbName);
                    DatabaseManager.createDataBase(dbName);

                }else{
                    System.out.println("只有管理员可以创建数据库");
                }



                // 这里你可以调用 parseCreateDatabase(cmd) 或执行创建逻辑
                continue;
            } else if (matcherUserDB.find()) {

                matched = true;
                String dbName = matcherUserDB.group(1);
                if(!TypeFilter.databaseExist(dbName)){
                    System.out.println(dbName+" 不存在!");
                    continue;
                }
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

                if(!TypeFilter.databaseExist(dbName)){
                    System.out.println(dbName+" not exist!");
                    continue;
                }

                if(UserManager.getCurrentUser().ddlOK()){
                    System.out.println("删除数据库: " + dbName);
                    DatabaseManager.dropDatabase(dbName);

                }else{
                    System.out.println("只有管理员可以删除数据库");
                }
                continue;

//                // 执行删除逻辑
//                continue;
            }else if(matcherShowDB.find()){
                System.out.println("show");

                List<String> databases=DatabaseManager.listDatabases();
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
            Matcher matcherDESC=PATTERN_DESC.matcher(cmd);



            if (matcherCreateTable.find()) {
                System.out.println("create");
                matched = true;




                // ✅ 取出表名
                String tableName = matcherCreateTable.group(1);
                if(TypeFilter.tableExist(tableName)){
                    System.out.println("表已经存在!");
                    continue;
                }




                // ✅ 取出字段定义并解析
                String fieldsStr = matcherCreateTable.group(2);
                ArrayList<Field> fieldList = commandParser.parseCreateTable(fieldsStr);
                if(UserManager.getCurrentUser().ddlOK()){
                    System.out.println("建表: " + tableName);
                    create(fieldList,tableName);



                }else{
                    System.out.println("只有管理员可以建表");
                }
                continue;



            } else if (matcherDropTable.find()) {
                System.out.println("drop");
                // matched = true;
                String tableName = matcherDropTable.group(1);  //
                if(!TypeFilter.tableExist(tableName)){
                    System.out.println("Table 不存在!");
                    continue;
                }

                if(UserManager.getCurrentUser().ddlOK()){

                    System.out.println("删除表: " + tableName);
                    TableManager.DropTable(tableName, 2);


                }else{
                    System.out.println("只有管理员可以删除表");
                }

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

                if(UserManager.getCurrentUser().dmlOK()){

                    System.out.println("insert");
                    //matched = true;
                    insert(matcherInsertTable);


                }else{
                    System.out.println("只有用户可以插入数据");
                }


                continue;

            } else if (matcherAlterTable.find()) {

                if(UserManager.getCurrentUser().ddlOK()){
                    System.out.println("alter");
                    alter(matcherAlterTable);


                }else{
                    System.out.println("只有管理员可以修改表结构");
                }


                // matched = true;
                continue;


            } else if (matcherDelete.find()) {

                if(UserManager.getCurrentUser().dmlOK()){
                    String tableName = matcherDelete.group(1);
                    String conditionstr = matcherDelete.group(2);
                    ArrayList<Condition> conditions;

                    Table table=new Table(tableName);
                    ConditionParser parser=new ConditionParser(table);
                    parser.tokenizeWhere(matcherSelectTable.group(3));

                }else{
                    System.out.println("只有用户可以删除数据");
                }



            } else if (matcherUpdate.find()) {


                if(UserManager.getCurrentUser().dmlOK()){
                    String tableName;
                    String conditionstr;

                    update(matcherUpdate);

                }else{
                    System.out.println("只有用户可以更新数据");
                }




                matched = true;
                continue;


            }else if(matcherDESC.find()){
                String tableName=matcherDESC.group(2);
                Table table =new Table(tableName);
                //System.out.println(table.getSchema());
                TableManager.desc(table);
                continue;


            }

            if (!matched) {
                System.out.println("错误输入: " + cmd);  // 调试输出，查看具体输入的命令
                continue;
            }

            System.out.println("matched?" + matched);

        }


    }



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

    /**
     * 检查列是否存在，支持聚合函数中的列
     * @param table 表对象
     * @param columns 要检查的列名列表（可能包含聚合函数）
     * @return 所有列都有效返回true，否则返回false
     */

    private boolean checkColumnsExist(Table table, List<?>  columns) {
        for (Object column : columns) {
            // 检查是否是聚合函数
            String[] aggInfo = commandParser.parseAggregateFunction(column.toString());

            if (aggInfo != null) {
                // 处理聚合函数中的列
                String aggColumn = aggInfo[1]; // 获取聚合参数

                // 特殊处理count(*)
                if (aggColumn.equals("*")) {
                    continue; // count(*) 总是有效
                }

                // 检查聚合参数是否是有效列
                if (!TypeFilter.columnExist(table, aggColumn)) {
                    System.out.println("聚合函数 " + column + " 中的列 '" + aggColumn + "' 不存在!");
                    return false;
                }
            } else {
                // 普通列检查
                if (!TypeFilter.columnExist(table, column.toString())) {
                    System.out.println("列 '" + column + "' 不存在!");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 应用 GROUP BY 分组操作
     */
    private ArrayList<Tuple> applyGroupBy(ArrayList<Tuple> data,
                                          ArrayList<String[]> columns,
                                          String groupByStr,
                                          Table table) {
        // 1. 解析 GROUP BY 列
        String[] groupByColumns = groupByStr.split("\\s*,\\s*");

        // 2. 验证 GROUP BY 列是否存在
        for (String col : groupByColumns) {
            if (!TypeFilter.columnExist(table, col)) {
                System.out.println("GROUP BY column '" + col + "' not exist!");
                return data; // 返回原始数据或抛出异常
            }
        }

        // 3. 分组数据
        Map<Tuple, ArrayList<Tuple>> groups = new HashMap<>();
        for (Tuple tuple : data) {
            // 创建分组键（只包含 GROUP BY 列的值）
            Tuple key = createGroupKey(tuple, groupByColumns, table.getSchema());

            // 将元组添加到对应的分组
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(tuple);
        }

        // 4. 对每个分组应用聚合函数
        ArrayList<Tuple> result = new ArrayList<>();
        for (Map.Entry<Tuple, ArrayList<Tuple>> entry : groups.entrySet()) {
            Tuple groupedTuple = applyAggregates(entry.getKey(), entry.getValue(), columns);
            result.add(groupedTuple);
        }

        return result;
    }

    /**
     * 创建分组键
     */
    private Tuple createGroupKey(Tuple tuple, String[] groupByColumns, Schema schema) {
        // 实现根据 GROUP BY 列创建键的逻辑
        Tuple key=new Tuple();
        // ...
        return key;
    }

    /**
     * 应用聚合函数
     */
    private Tuple applyAggregates(Tuple groupKey, ArrayList<Tuple> groupTuples, ArrayList<String[]> columns) {
        Tuple resultTuple=new Tuple();
        // ...
        return resultTuple;
    }
    private boolean create(ArrayList<Field> fieldList,String tableName)throws IOException {
        Set<String> existingColumnNames = new HashSet<>();
        for (Field fieldList1 : fieldList) {
            String columnName = fieldList1.getName();
            if (existingColumnNames.contains(columnName)) {
                System.out.println("column exist!");
                return false;
            } else {
                boolean validtype;
                validtype = TypeFilter.typeExist(fieldList);
                if (!validtype) {
                    System.out.println("type invalid!");
                    return false;
                } else {

                    if (fieldList1.isForeignKey()) {
                        // 验证引用的表和列是否存在
                        if (!TypeFilter.tableExist(fieldList1.getReferenceTable())) {
                            System.out.println("外键引用的表 " + fieldList1.getReferenceTable() + " 不存在");
                            return false;
                        }
                        if (!TypeFilter.columnExist(new Table(fieldList1.getReferenceTable()),
                                fieldList1.getReferenceColumn())) {
                            System.out.println("外键引用的列 " + fieldList1.getReferenceColumn() + " 不存在");
                            return false;
                        }
                    }

                    if (fieldList == null) {


                    } else {
                        System.out.println("创建表: " + tableName);
                        for (Field f : fieldList) {
                            System.out.println("字段: " + f.getName() + ", 类型: " + f.getType());
                        }
                        TableManager.CreateTable(tableName, fieldList);
                    }
                    continue;

                }

            }

        }
        return true;

    }

    private boolean select(Matcher matcherSelect) throws IOException{
        String tableName = matcherSelect.group(2);
        if(!TypeFilter.tableExist(tableName)){
            System.out.println(tableName+" not exist!");

        }else{
            Table table=new Table(tableName);
            ArrayList<String[]> columns = new ArrayList<>();
            ArrayList<Condition> conditions = new ArrayList<>();

            ArrayList<Tuple> data;


//
            String columnsStr = matcherSelect.group(1);
            if (columnsStr.equals("*")) {
                Schema schema=table.getSchema();
                columns = new ArrayList<>();



            } else {
                columns = commandParser.parseSelectColumn(columnsStr);
                checkColumnsExist(table,columns);
                for(Object column:columns){
                    if(!TypeFilter.columnExist(table,column.toString())){
                        System.out.println("column not exist!");
                        return false;

                    }
                }


            }
            if(!(matcherSelect.group(3)==null)){
                System.out.println("with conditions");
                String conditionStr = matcherSelect.group(3).toLowerCase().trim();
                conditionStr=commandParser.parseBetweenAnd(conditionStr);
                ConditionNode logicTree;
                ConditionParser parser=new ConditionParser(table);
                List<String> tokens = parser.tokenizeWhere(conditionStr);
                logicTree = parser.parseConditionTree(tokens);
                data=logicTree.evaluate();
                System.out.println("条件表达式树结构为：");
                System.out.println(logicTree);


            }else{

                data=table.selectAll();

            }


            // 解析 GROUP BY 子句
            if (matcherSelect.group(4) != null) {
                String groupByStr = matcherSelect.group(4).trim();
                data = applyGroupBy(data, columns, groupByStr, table);
            }

//
            // System.out.println("conditions: " + conditions);


            System.out.println(columns);
            // TODO:待修改
//            Render.DrawSelectedTable(data,columns);






            //Table.SelectFromTable(tableName,columns,conditions);

            //Table.From(tableName);
            //Table.DrawSelectedTable();


        }
        return true;


    }




    private void alter(Matcher matcherAlter)throws IOException{


        //System.out.println("altering1");
        String tableName;
        String details;
        String column;
        String operation;
        tableName = matcherAlter.group(1);
        System.out.println(tableName);
        if(!TypeFilter.tableExist(tableName)){
            System.out.println(tableName+" not exist!");

        }else{
            column = matcherAlter.group(3);//name
            System.out.println(column);

            operation = matcherAlter.group(2);
            System.out.println(operation);// "add"

            Table table=new Table(tableName);



            if(operation.equals("add")||operation.equals("ADD")){
                //System.out.println("adding");
                details=matcherAlter.group(4);
                //System.out.println("detail");

                Field field=new Field(column,details);

                TableManager.addColumn(field,table);

            }else if (operation.equals("drop") || operation.equals("DROP")) {
                // 检查该列是否被其他表的外键引用
                if (table.isColumnReferenced(column)) {
                    System.out.println("无法删除列 " + column + ": 被其他表的外键引用");
                    return;
                }
                TableManager.dropColumn(column, table);
            }else if (operation.equals("modify") || operation.equals("MODIFY")) {
                // 检查该列是否是外键或被外键引用
                if (table.isColumnForeignKey(column) || table.isColumnReferenced(column)) {
                    System.out.println("无法修改外键列 " + column + " 的类型");
                    return;
                }
                // ...执行修改...
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



    }


    private void update(Matcher mathcerUpdate)throws IOException{
        String tableName=mathcerUpdate.group(1);
        String statement=mathcerUpdate.group(2);
        String conditionStr=mathcerUpdate.group(4);
        ArrayList<Tuple> data;
        System.out.println(":"+tableName+":"+statement+":"+conditionStr);
        if(!TypeFilter.tableExist(tableName)){
            System.out.println(tableName+" not exist!");

        }else{
            conditionStr=commandParser.parseBetweenAnd(conditionStr);
            ConditionNode logicTree;
            Table table=new Table(tableName);
            ConditionParser parser=new ConditionParser(table);
            List<String> tokens = parser.tokenizeWhere(conditionStr);
            logicTree = parser.parseConditionTree(tokens);
            System.out.println(logicTree);
            //data=logicTree.evaluate();

            HashMap<String,String> statements=commandParser.parseUpdateSet(statement);
            if (table.hasForeignKeyConstraints()) {
                for (Map.Entry<String, String> entry : statements.entrySet()) {
                    Field field = schema.getField(entry.getKey());
                    if (field != null && field.isForeignKey()) {
                        Table refTable = new Table(tableName);
//                        if (!refTable.containsValue(field.getReferenceColumn(), entry.getValue())) {
//                            System.out.println("违反外键约束: 值 " + entry.getValue() + " 在表 " +
//                                    field.getReferenceTable() + " 的 " +
//                                    field.getReferenceColumn() + " 列中不存在");
//                            return;
//                        }
                    }
                }
            }

//            table.update(tableName,statements,logicTree);

        }




    }

    private void insert(Matcher matcherInsert) throws IOException {
        String tableName   = matcherInsert.group(1);
        List<String> columns   = commandParser.parseInsertColumn(matcherInsert.group(2));
        List<Object> rawValues = commandParser.parseInsertValue(matcherInsert.group(3));
        System.out.println(tableName);
        System.out.println(columns);
        System.out.println(rawValues );
        Table table=new Table(tableName);
        Schema schema  = table.getSchema();


        try{
            Tuple t=TypeFilter.createTupleFromInsertValues(schema, columns, rawValues);
            t.showAll();
            table.insert(t);
            System.out.println("插入成功");

        }catch (Exception e){
            System.out.println("fail to create tuple");
        }
//
//        if(!TypeFilter.tableExist(tableName)){
//            System.out.println(tableName+" not exist!");
//
//
//        }else if(true){
        // --- load schema ---


//            // 1. 校验并转换
//            List<Value> castedValues = TypeFilter.validateAndConvertValues(columns, rawValues, schema);
//
//           System.out.println(castedValues.get(1).getType());
//           System.out.println(columns.get(1));
//
//            Tuple keyTuple = buildTuple(columns, castedValues, schema);

//            // 2. 真正调用插入
//            if (table.hasForeignKeyConstraints()) {
//                for (Field field : schema.getFields()) {
//                    if (field.isForeignKey()) {
//                        Value fkValue = castedValues.get(columns.indexOf(field.getName()));
//                        Table refTable = new Table(field.getReferenceTable());
//                        if (!refTable.containsValue(field.getReferenceColumn(), fkValue)) {
//                            System.out.println("违反外键约束: 值 " + fkValue + " 在表 " +
//                                    field.getReferenceTable() + " 的 " +
//                                    field.getReferenceColumn() + " 列中不存在");
//                            return;
//                        }
//                    }
//                }
//            }





//        }

    }
    private Tuple buildTuple(
            List<String> columns,
            List<Value>  values,
            Schema       schema) {

        // 1. 准备列名→下标的映射
        List<Field> fields = schema.getFields();
        Map<String,Integer> colIdx = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            colIdx.put(fields.get(i).getName().toLowerCase(), i);
        }

        // 2. 创建一个和全表列数一样大的 Value 数组，默认填 null（或你定义的 NullValue）
        Value[] arr = new Value[fields.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new NullValue();  // 假设你已有一个 NullValue 实现
        }

        // 3. 把用户指定列的位置，用实际转换后的值覆盖进去
        for (int i = 0; i < columns.size(); i++) {
            String col = columns.get(i).toLowerCase();
            int idx    = colIdx.get(col);    // 一定存在，否则前面 validate 就会报错
            arr[idx]   = values.get(i);

            System.out.println(values.get(i).getType());
        }

        // 4. 用这个数组构造一个 Tuple 返回
        return new Tuple(arr);
    }

    /**
     * 与ui连接的试用方法
     * @param command
     * @return
     */
    // 处理用户输入的命令
    public String processCommand(String command) {
        boolean matched = false;
        // 匹配 create table 命令
        Matcher matcherCreateTable = PATTERN_CREATE_TABLE.matcher(command);
        if (matcherCreateTable.find()) {
            return handleCreateTable(matcherCreateTable,matched);
        }

        // 如果命令不匹配，返回错误信息
        return "无效命令: " + command;
    }

    // 处理 create table 命令
    private String handleCreateTable(Matcher matcher,boolean matched ) {
//        System.out.println("create");
//        matched = true;
//
//        // ✅ 取出表名
//        String tableName = matcher.group(1);
//        if(TypeFilter.tableExist(tableName)){
//            System.out.println("Table already exist!");
//            return "Table already exist!";
//        }
//        // ✅ 取出字段定义并解析
//        String fieldsStr = matcher.group(2);
//        ArrayList<Field> fieldList = commandParser.parseCreateTable(fieldsStr);
//
//
//        Set<String> existingColumnNames = new HashSet<>();
//        for(Field fieldList1 : fieldList) {
//            String columnName = fieldList1.getName();
//            if(existingColumnNames.contains(columnName)) {
//                System.out.println("column exist!");
//                return "column exist!";
//
//            }else{
//                boolean validtype;
//                validtype=TypeFilter.typeExist(fieldList);
//                if(!validtype){
//                    System.out.println("type invalid!");
//                    return "type invalid!";
//
//                }else{
//                    if (fieldList == null) {
//
//                    } else {
//                        System.out.println("创建表: " + tableName);
//                        for (Field f : fieldList) {
//                            System.out.println("字段: " + f.getName() + ", 类型: " + f.getType());
//                            return "字段: " + f.getName() + ", 类型: " + f.getType();
//                        }
//                        //TableManager.CreateTable(tableName, fieldList);
//                    }
//                }
//            }
//        }
        return "   ";
    }
}




