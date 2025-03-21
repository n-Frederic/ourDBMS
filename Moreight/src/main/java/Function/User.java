package Function;

public class User {
    private String userName;
    private String password;
    private int level;  // 用户的权限等级

    public User(String userName,String password,int level){
        this.userName = userName;
        this.password = password;
        this.level = level;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}


