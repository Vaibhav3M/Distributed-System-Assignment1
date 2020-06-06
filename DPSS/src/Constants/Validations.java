package Constants;

public class Validations {

    public static boolean validateUserName(String username){

        int userNameLength = username.length();

        return  userNameLength >= 6 && userNameLength <=15;
    }

    public static boolean validatePassword(String password){

        return password.length() >= 6;
    }

    public static boolean validateAge(int age){

        return age > 0;
    }

    public static boolean validateIP(int IP){

        return (IP == 132 || IP == 93 || IP == 182);
    }
}
