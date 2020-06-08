package Models;

/**
 * The type Admin.
 */
public class Admin {

    private String userName;
    private String password;
    private String IPAddress;

    @Override
    public String toString() {
        return "Admin{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", IPAddress='" + IPAddress + '\'' +
                '}';
    }

    /**
     * Instantiates a new Admin.
     *
     * @param userName  the user name
     * @param password  the password
     * @param IPAddress the ip address
     */
    public Admin(String userName, String password, String IPAddress) {
        this.userName = userName;
        this.password = password;
        this.IPAddress = IPAddress;
    }

    /**
     * Gets user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets user name.
     *
     * @param userName the user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets ip address.
     *
     * @return the ip address
     */
    public String getIPAddress() {
        return IPAddress;
    }

    /**
     * Sets ip address.
     *
     * @param IPAddress the ip address
     */
    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

}
