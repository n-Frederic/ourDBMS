package Operate;
import Parser.Field;
import Function.DatabaseManager;
import Function.TableManager;
import Function.UserManager;
import Function.UserManager;
import Parser.StringParser;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
public class Operating {

        private static final Pattern PATTERN_INSERT = Pattern.compile("(?i)insert\\s+into\\s+(\\w+)\\s*\\(([^\\)]+)\\)\\s*values\\s*\\(([^\\)]+)\\);?");

        private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile("(?i)create\\s+table\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;");
        private static final Pattern PATTERN_ALTER_TABLE_ADD = Pattern.compile("(?i)alter\\s+table\\s(\\w+)\\s+add\\s(\\w+\\s\\w+)\\s?;");
        private static final Pattern PATTERN_DELETE = Pattern.compile("(?i)delete\\s+from\\s(\\w+)(?:\\s+where\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\s+and\\s+(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
        private static final Pattern PATTERN_UPDATE = Pattern.compile("(?i)update\\s(\\w+)\\s+set\\s(\\w+\\s?=\\s?[^,\\s]+(?:\\s?,\\s?\\w+\\s?=\\s?[^,\\s]+)*)(?:\\s+where\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\s+and\\s+(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
        private static final Pattern PATTERN_DROP_TABLE = Pattern.compile("(?i)drop\\s+table\\s+(\\w+)\\s*;?");

        private static final Pattern PATTERN_SELECT = Pattern.compile("(?i)select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+(?:\\.\\w+)?)*))\\s+from\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\s+where\\s([^\\;]+\\s?;))?");
        private static final Pattern PATTERN_DELETE_INDEX = Pattern.compile("(?i)delete\\s+index\\s(\\w+)\\s?;");
        private static final Pattern PATTERN_GRANT_ADMIN = Pattern.compile("(?i)grant\\s+admin\\s+to\\s([^;\\s]+)\\s?;");
        private static final Pattern PATTERN_REVOKE_ADMIN = Pattern.compile("(?i)revoke\\s+admin\\s+from\\s([^;\\s]+)\\s?;");
        private static final Pattern PATTERN_CREATE_DATABASE = Pattern.compile("(?i)create\\s+database\\s+(\\w+)\\s*;");
        private static final Pattern PATTERN_USE_DATABASE = Pattern.compile("(?i)use\\s+(\\w+)\\s*;");
        private static final Pattern PATTERN_DROP_DATABASE = Pattern.compile("(?i)drop\\s+database\\s+(\\w+)\\s*;");


        private static Scanner sc = new Scanner(System.in);
        private  boolean login=false;
        private boolean enter_database=false;


        public void dbms() {

                do{
                        System.out.println("欢迎使用 SimpleDBMS");
                        System.out.println("1. 登录");
                        System.out.println("2. 注册");
                        System.out.print("请输入选项：");

                        String choice = sc.nextLine();
                        if ("1".equals(choice)) {
                                login();
                        } else if ("2".equals(choice)) {
                                register();
                        } else {
                                System.out.println("无效选项，程序退出。");
                                return;
                        }


                } while(login==false);

                Scanner sc = new Scanner(System.in);
                String cmd;
                while (!"exit".equals(cmd = sc.nextLine())&&enter_database==false) {

                        boolean matched = false;  // 标记是否匹配成功
                        Matcher matcherCreateDB = PATTERN_CREATE_DATABASE.matcher(cmd);
                        Matcher matcherUserDB = PATTERN_USE_DATABASE.matcher(cmd);
                        Matcher matcherDropDB= PATTERN_DROP_DATABASE.matcher(cmd);

                        if(matcherCreateDB.find()){
                                matched = true;
                                String dbName = matcherCreateDB.group(1);
                                System.out.println("创建数据库: " + dbName);

                                DatabaseManager.createDataBase(dbName);
                                // 这里你可以调用 parseCreateDatabase(cmd) 或执行创建逻辑
                                continue;
                        }
                        else if(matcherUserDB.find()){

                                matched = true;
                                String dbName = matcherUserDB.group(1);
                                //System.out.println("使用数据库: " + dbName);
                                boolean dbexist=false;
                                dbexist=DatabaseManager.useDatabase(dbName);
                                if(dbexist){
                                        enter_database = true;
                                        System.out.println("使用数据库: " + dbName);
                                        break;
                                }else{
                                        System.out.println("数据库不存在");
                                }
                                // 设置 enter_database = true，表示已进入数据库

                                continue;
                        }
                        else if(matcherDropDB.find()){

                                matched = true;
                                String dbName = matcherDropDB.group(1);


                                System.out.println("删除数据库: " + dbName);
                                DatabaseManager.dropDatabase(dbName,UserManager.GetCurrentUser().getLevel());
                                // 执行删除逻辑
                                continue;
                        }else if(!matched){
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


                        if (matcherCreateTable.find()) {
                                System.out.println("create");
                                matched = true;

                                // ✅ 取出表名
                                String tableName = matcherCreateTable.group(1);

                                // ✅ 取出字段定义并解析
                                String fieldsStr = matcherCreateTable.group(2);
                                ArrayList<Field> fieldList = StringParser.parseCreateTable(fieldsStr);



                                if(fieldList==null){


                                }else{
                                        System.out.println("创建表: " + tableName);
                                        for (Field f : fieldList) {
                                                System.out.println("字段: " + f.getName() + ", 类型: " + f.getType());
                                        }
                                        TableManager.CreateTable(tableName, fieldList);
                                }
                                continue;

                        }
                        else if (matcherDropTable.find()) {
                                System.out.println("drop");
                                matched = true;
                                String tableName = matcherDropTable.group(1);  //
                                System.out.println("删除表: " + tableName);     //
                                TableManager.DropTable(tableName, 2);
                                continue;

                        }else if(matcherSelectTable.find()){
                                System.out.println("select");
                                matched=true;
//                                select(matcherSelectTable);
                                String tableName=matcherSelectTable.group(2);
                                //List<String> nameList=StringParser.parseFrom(matcherSelectTable.group(2));//选择多个表
                                System.out.println(matcherSelectTable.group(2));//after from
                                StringParser.parseWhere(matcherSelectTable.group(3));
                                System.out.println(matcherSelectTable.group(3));//where
                                continue;
                        }else if(matcherInsertTable.find()){
                                System.out.println("insert");
                                matched=true;
                                insert(matcherInsertTable);
                                continue;

                        }

                        if (!matched) {
                                System.out.println("错误输入: " + cmd);  // 调试输出，查看具体输入的命令
                                continue;
                        }

                        System.out.println("matched?"+matched);

                }





        }
        private  void login() {
                System.out.print("用户名：");
                String username = sc.nextLine();
                System.out.print("密码：");
                String password = sc.nextLine();
                Integer result=UserManager.checkUserExists(username,password);
                if(result==1){
                        System.out.println("user name not exist!");
                }else if(result==2){
                        System.out.println("password is not correct!");
                }else if(result==3){
                        login=true;
                        System.out.println("login successful! welcome "+username);
                }else{
                        System.out.println("error.exiting......");
                }
                // 在此实现登录逻辑



        }

        private  void register()  {
                System.out.print("设置用户名：");
                String username = sc.nextLine();
                System.out.print("设置密码：");
                String password = sc.nextLine();
                Integer result=UserManager.CreateUser(username,password);
                if(result==2){
                        login=true;
                        System.out.println("login successful! welcome "+username);

                }else if(result==0){
                        System.out.println("register failed ,please check !");

                }else{
                        System.out.println("you have already registered, please log in!");

                }


                // 在此实现注册逻辑

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

//        private void select(Matcher matcherSelect) {
//                //将读到的所有数据放到tableDatasMap中
//                Map<String, List<Map<String, String>>> tableDatasMap = new LinkedHashMap<>();
//
//                //将投影放在Map<String,List<String>> projectionMap中
//                Map<String, List<String>> projectionMap = new LinkedHashMap<>();
//
//
//                List<String> tableNames = StringParser.parseFrom(matcherSelect.group(2));
//
//                String whereStr = matcherSelect.group(3);
//
//                //将tableName和table.fieldMap放入
//                Map<String, Map<String, Field>> fieldMaps = new HashMap();
//
//                for (String tableName : tableNames) {
//                        Table table = Table.getTable(tableName);
//                        if (null == table) {
//                                System.out.println("未找到表：" + tableName);
//                                return;
//                        }
//                        Map<String, Field> fieldMap = table.getFieldMap();
//                        fieldMaps.put(tableName, fieldMap);
//
//                        //解析选择
//                        List<SingleFilter> singleFilters = new ArrayList<>();
//
//                        List<Map<String, String>> filtList =StringParser.parseWhere(matcherSelect.group(3));
//                        for (Map<String, String> filtMap : filtList) {
//                                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
//                                        , filtMap.get("relationshipName"), filtMap.get("condition"));
//
//                                singleFilters.add(singleFilter);
//                        }
//
//                        //解析最终投影
//                        List<String> projections = StringUtil.parseProjection(matcherSelect.group(1), tableName, fieldMap);
//                        projectionMap.put(tableName, projections);
//
//
//                        //读取数据并进行选择操作
//                        List<Map<String, String>> srcDatas = table.read(singleFilters);
//                        List<Map<String, String>> datas = associatedTableName(tableName, srcDatas);
//
//                        tableDatasMap.put(tableName, datas);
//                }
//
//
////                //解析连接条件，并创建连接对象jion
////                List<Map<String, String>> joinConditionMapList = StringUtil.parseWhere_join(whereStr, fieldMaps);
////                List<JoinCondition> joinConditionList = new LinkedList<>();
////                for (Map<String, String> joinMap : joinConditionMapList) {
////                        String tableName1 = joinMap.get("tableName1");
////                        String tableName2 = joinMap.get("tableName2");
////                        String fieldName1 = joinMap.get("field1");
////                        String fieldName2 = joinMap.get("field2");
////                        Field field1 = fieldMaps.get(tableName1).get(fieldName1);
////                        Field field2 = fieldMaps.get(tableName2).get(fieldName2);
////                        String relationshipName = joinMap.get("relationshipName");
////                        JoinCondition joinCondition = new JoinCondition(tableName1, tableName2, field1, field2, relationshipName);
////
////                        joinConditionList.add(joinCondition);
////
////                        //将连接条件的字段加入投影中
////                        projectionMap.get(tableName1).add(fieldName1);
////                        projectionMap.get(tableName2).add(fieldName2);
////                }
////
////                List<Map<String, String>> resultDatas = Join.joinData(tableDatasMap, joinConditionList, projectionMap);
////                //System.out.println(resultDatas);
//
//                //将需要显示的字段名按table.filed的型式存入dataNameList
//                List<String> dataNameList = new LinkedList<>();
//                for (Map.Entry<String, List<String>> projectionEntry : projectionMap.entrySet()) {
//                        String projectionKey = projectionEntry.getKey();
//                        List<String> projectionValues = projectionEntry.getValue();
//                        for (String projectionValue : projectionValues) {
//                                dataNameList.add(projectionKey + "." + projectionValue);
//                        }
//
//                }
//
//                //计算名字长度，用来对齐数据
//                int[] lengh = new int[dataNameList.size()];
//                Iterator<String> dataNames = dataNameList.iterator();
//                for (int i = 0; i < dataNameList.size(); i++) {
//                        String dataName = dataNames.next();
//                        lengh[i] = dataName.length();
//                        System.out.printf("|%s", dataName);
//                }
//
//                System.out.println("|");
//                for (int ls : lengh) {
//                        for (int l = 0; l <= ls; l++) {
//                                System.out.printf("-");
//                        }
//                }
//                System.out.println("|");
//
//                for (Map<String, String> line : resultDatas) {
//                        Iterator<String> valueIter = line.values().iterator();
//                        for (int i = 0; i < lengh.length; i++) {
//                                String value = valueIter.next();
//                                System.out.printf("|%s", value);
//                                for (int j = 0; j < lengh[i] - value.length(); j++) {
//                                        System.out.printf(" ");
//                                }
//                        }
//                        System.out.println("|");
//                }
//        }

        private void insert(Matcher matcherInsert) {
                String tableName = matcherInsert.group(1);
                ArrayList<String>columns=new ArrayList<>();
                ArrayList<Object> values=new ArrayList<>();



                //columns=StringParser.parse

//                if (null == table) {
//                        System.out.println("未找到表：" + tableName);
//                        return;
//                }

                String columnsStr = matcherInsert.group(2);
                String valuesStr = matcherInsert.group(3);

                columns=StringParser.parseInsertColumn(columnsStr);
                values=StringParser.parseInsertValue(valuesStr);


                System.out.println("Table: " + tableName);
                System.out.println("Columns: " + columns);
                System.out.println("Values: " + values);

                Table.InsertIntoValue(tableName,columns,values);

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




