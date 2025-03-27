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

        private static final Pattern PATTERN_INSERT = Pattern.compile("(?i)insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
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
                                System.out.println(matcherSelectTable.group(1));//rows
                                String tableName=matcherSelectTable.group(2);
                                //List<String> nameList=StringParser.parseFrom(matcherSelectTable.group(2));//选择多个表
                                System.out.println(matcherSelectTable.group(2));//after from
                                StringParser.parseWhere(matcherSelectTable.group(3));
                                System.out.println(matcherSelectTable.group(3));//where
                                continue;
                        }else if(matcherInsertTable.find()){
                                System.out.println("insert");
                                matched=true;
                                System.out.println(matcherInsertTable.group(1));
                                System.out.println(matcherInsertTable.group(2));
                                System.out.println(matcherInsertTable.group(3));
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




}




