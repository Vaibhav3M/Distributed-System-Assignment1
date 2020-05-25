package Models;

public class Admin {

    private String userName;
    private String password;
    private String IPAddress;

    public Admin(String userName, String password, String IPAddress) {
        this.userName = userName;
        this.password = password;
        this.IPAddress = IPAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

}
