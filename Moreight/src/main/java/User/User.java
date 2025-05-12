package User;

/**
 * User类用于表示用户信息。
 * 它包含用户名、密码和权限等级三个属性，并提供了相应的getter和setter方法。
 */
public class User {
    private String userName;
    private String password;
    private String level;  // 用户的权限等级,1是游客，2是管理员

    /**
     * 构造一个User对象。
     * @param userName 用户名。
     * @param password 密码。
     * @param level 权限等级。
     */
    public User(String userName,String password,String level){
        this.userName = userName;
        this.password = password;
        this.level = level;
    }

    /**
     * 获取用户的密码。
     * @return 用户的密码。
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置用户的密码。
     * @param password 新密码。
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户的用户名。
     * @return 用户的用户名。
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户的用户名。
     * @param userName 新用户名。
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }


    /**
     * 设置用户的权限等级。
     * @param level 新权限等级。
     */

    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * 获取用户的权限等级。
     * @return 用户的权限等级。
     */
    public String getLevel() {
        return level;
    }

    public  boolean hasPermission(String level) {
        System.out.println(this.level +level);
        System.out.println(this.level==level);
        return this.level.equals(level);


    }
    public boolean dmlOK(){
        return UserManager.isHigherPermission(this.level,"visitor");
    }
    public boolean ddlOK(){
        return UserManager.isHigherPermission(this.level,"user");
    }


}


