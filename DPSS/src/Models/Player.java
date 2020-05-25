package Models;

public class Player {

    private String firstName;
    private String lastName;
    private int age;

    private String userName;
    private String password;
    private String IPAddress;


    public Player(String firstName, String lastName, int age, String userName, String password, String IPAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.userName = userName;
        this.password = password;
        this.IPAddress = IPAddress;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
