package Operate;
import Function.TableManager;
import Function.UserManager;
import Function.UserManager;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Operating {

        private static final Pattern PATTERN_INSERT = Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
        private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;");
        private static final Pattern PATTERN_ALTER_TABLE_ADD = Pattern.compile("alter\\stable\\s(\\w+)\\sadd\\s(\\w+\\s\\w+)\\s?;");
        private static final Pattern PATTERN_DELETE = Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
        private static final Pattern PATTERN_UPDATE = Pattern.compile("update\\s(\\w+)\\sset\\s(\\w+\\s?=\\s?[^,\\s]+(?:\\s?,\\s?\\w+\\s?=\\s?[^,\\s]+)*)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
        private static final Pattern PATTERN_DROP_TABLE = Pattern.compile("drop\\stable\\s(\\w+);");
        private static final Pattern PATTERN_SELECT = Pattern.compile("select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+(?:\\.\\w+)?)*))\\sfrom\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\swhere\\s([^\\;]+\\s?;))?");
        private static final Pattern PATTERN_DELETE_INDEX = Pattern.compile("delete\\sindex\\s(\\w+)\\s?;");
        private static final Pattern PATTERN_GRANT_ADMIN = Pattern.compile("grant\\sadmin\\sto\\s([^;\\s]+)\\s?;");
        private static final Pattern PATTERN_REVOKE_ADMIN = Pattern.compile("revoke\\sadmin\\sfrom\\s([^;\\s]+)\\s?;");
        private static final Pattern PATTERN_CREATE_DATABASE = Pattern.compile("create\\s+database\\s+(\\w+)\\s*;");
        private static final Pattern PATTERN_USE_DATABASE = Pattern.compile("use\\s+(\\w+)\\s*;");
        private static final Pattern PATTERN_DROP_DATABASE = Pattern.compile("drop\\s+database\\s+(\\w+)\\s*;");


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
                        Matcher matcherCreateDB = PATTERN_CREATE_DATABASE.matcher(cmd);
                        Matcher matcherUserDB = PATTERN_USE_DATABASE.matcher(cmd);
                        Matcher matcherDeleteIndex = PATTERN_DROP_DATABASE.matcher(cmd);

                        while(matcherCreateDB.find()){

                        }
                        while(matcherUserDB.find()){

                        }
                        while(matcherDeleteIndex.find()){

                        }


                }



                /*
                //默认进入user1用户文件夹
                File userFolder = new File("dir", UserManager.getName());

                //默认进入user1的默认数据库db1
                File dbFolder = new File(userFolder, "db1");


                Table.init(user.getName(), dbFolder.getName());


                Scanner sc = new Scanner(System.in);
                String cmd;
                while (!"exit".equals(cmd = sc.nextLine())) {
                        Matcher matcherGrantAdmin = PATTERN_GRANT_ADMIN.matcher(cmd);
                        Matcher matcherRevokeAdmin = PATTERN_REVOKE_ADMIN.matcher(cmd);
                        Matcher matcherInsert = PATTERN_INSERT.matcher(cmd);
                        Matcher matcherCreateTable = PATTERN_CREATE_TABLE.matcher(cmd);
                        Matcher matcherAlterTable_add = PATTERN_ALTER_TABLE_ADD.matcher(cmd);
                        Matcher matcherDelete = PATTERN_DELETE.matcher(cmd);
                        Matcher matcherUpdate = PATTERN_UPDATE.matcher(cmd);
                        Matcher matcherDropTable = PATTERN_DROP_TABLE.matcher(cmd);
                        Matcher matcherSelect = PATTERN_SELECT.matcher(cmd);
                        Matcher matcherDeleteIndex = PATTERN_DELETE_INDEX.matcher(cmd);

                        while (matcherGrantAdmin.find()) {
                                User grantUser = User.getUser(matcherGrantAdmin.group(1));
                                if (null == grantUser) {
                                        System.out.println("授权失败！");
                                } else if (user.getName().equals(grantUser.getName())) {
                                        //如果是当前操作的用户，就直接更改当前用户权限
                                        user.grant(User.ADMIN);
                                        System.out.println("用户:" + user.getName() + "授权成功！");
                                } else {
                                        grantUser.grant(User.ADMIN);
                                        System.out.println("用户:" + grantUser.getName() + "授权成功!");
                                }
                        }

                        while (matcherRevokeAdmin.find()) {
                                User revokeUser = User.getUser(matcherRevokeAdmin.group(1));
                                if (null == revokeUser) {
                                        System.out.println("取消授权失败!");
                                }
                                if (user.getName().equals(revokeUser.getName())) {
                                        //如果是当前操作的用户，就直接更改当前用户权限
                                        user.grant(User.READ_ONLY);
                                        System.out.println("用户:" + user.getName() + "已取消授权！");
                                } else {
                                        revokeUser.grant(User.READ_ONLY);
                                        System.out.println("用户:" + revokeUser.getName() + "已取消授权！");
                                }
                        }

                        while (matcherAlterTable_add.find()) {
                                if (user.getLevel() != User.ADMIN) {
                                        System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                                        break;
                                }
                                alterTableAdd(matcherAlterTable_add);
                        }

                        while (matcherDropTable.find()) {
                                if (user.getLevel() != User.ADMIN) {
                                        System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                                        break;
                                }
                                dropTable(matcherDropTable);
                        }


                        while (matcherCreateTable.find()) {
                                if (user.getLevel() != User.ADMIN) {
                                        System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                                        break;
                                }
                                createTable(matcherCreateTable);
                        }

                        while (matcherDelete.find()) {
                                if (user.getLevel() != User.ADMIN) {
                                        System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                                        break;
                                }
                                delete(matcherDelete);
                        }

                        while (matcherUpdate.find()) {
                                if (user.getLevel() != User.ADMIN) {
                                        System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                                        break;
                                }
                                update(matcherUpdate);
                        }

                        while (matcherInsert.find()) {
                                if (user.getLevel() != User.ADMIN) {
                                        System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                                        break;
                                }
                                insert(matcherInsert);
                        }

                        while (matcherSelect.find()) {
                                select(matcherSelect);
                        }

                        while (matcherDeleteIndex.find()) {
                                if (user.getLevel() != User.ADMIN) {
                                        System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                                        break;
                                }
                                deleteIndex(matcherDeleteIndex);
                        }
                }

        }

                 */

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


}




