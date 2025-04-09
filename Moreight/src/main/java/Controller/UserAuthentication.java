package Controller;

import User.UserManager;

import java.util.Scanner;

public class UserAuthentication {

    protected static void login(Scanner sc, boolean login) {
        System.out.print("用户名：");
        String username = sc.nextLine();
        System.out.print("密码：");
        String password = sc.nextLine();
        int result= UserManager.checkUserExists(username,password);
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

    protected static  void register(Scanner sc, boolean login)  {
        System.out.print("设置用户名：");
        String username = sc.nextLine();
        System.out.print("设置密码：");
        String password = sc.nextLine();
        int result = UserManager.CreateUser(username,password);
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


}
