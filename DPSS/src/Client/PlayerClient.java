package Client;

import Constants.Constants;
import GameServers.DPSS_GameServerInterface;
import Models.Player;
import Constants.Validations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class PlayerClient {

    private static BufferedReader reader = new BufferedReader((new InputStreamReader(System.in)));
    private static int client_IP_Address = 132;
    private static String client_server_name = "";
    private static DPSS_GameServerInterface dpss_gameServerInterface = null;

    //Return basic menu.
    private static int showMenu() throws Exception
    {
        System.out.println("\n**** Welcome to DPSS Game ****\n");

        int userinput = 1;

        System.out.println("Please select an option (1-4)");
        System.out.println("1. Create new Player");
        System.out.println("2. SignIn");
        System.out.println("3. SignOut");
        System.out.println("4. Exit");

        boolean inputValid = false;
        do {
            try{
                System.out.print("Please select an Option : ");
                userinput = Integer.valueOf(reader.readLine());
                inputValid = true;
            }catch (Exception e){
                System.out.println("Oops..! Invalid input. Please select 1,2,3 or 4 to perform required action");
            }
        } while (!inputValid);

        return userinput;

    }



    public static void main(String args[]) throws Exception{

        System.out.println("Please enter IP : (132, 93, 182)");

        client_IP_Address = Integer.valueOf(reader.readLine());

        switch (client_IP_Address){

            case 132:
                client_server_name = Constants.SERVER_NAME_AMERICA;
                break;

            case 93:
                client_server_name = Constants.SERVER_NAME_EUROPE;
                break;

            case 182:
                client_server_name = Constants.SERVER_NAME_ASIA;
                break;
            default:
                System.out.println("Invalid server IP");

        }
        System.out.println("LOADING......");

        Registry registry = LocateRegistry.getRegistry(client_IP_Address);
        dpss_gameServerInterface = (DPSS_GameServerInterface) registry.lookup(client_server_name);

        System.out.println(client_server_name + " at " + client_IP_Address +  " Activated");
        boolean exit = false;

        while (!exit) {

            int userinput = showMenu();

            switch (userinput) {

                case 1:
                    Player newPlayer = createPlayer();

                    System.out.println(dpss_gameServerInterface.createPlayerAccount(newPlayer));
                    break;

                case 2:

                    System.out.print("Please enter user name: ");
                    String userNameLogin = reader.readLine();

                    System.out.print("Please enter password: ");
                    String password = reader.readLine();

                    System.out.println(dpss_gameServerInterface.playerSignIn(userNameLogin, password, String.valueOf(client_IP_Address)));
                    break;

                case 3:

                    System.out.print("Please enter user name: ");
                    String userNameLogout = reader.readLine();

                    System.out.println(dpss_gameServerInterface.playerSignOut(userNameLogout, String.valueOf(client_IP_Address)));
                    break;

                case 4:

                    System.out.println("Thank you for visiting our DPSS app");
                    exit = true;
                    break;

            }
        }
    }

    private static Player createPlayer() throws Exception{

        //inputting first name
        System.out.print("Please enter first name: ");
        String firstName = reader.readLine();

        //inputting last name
        System.out.print("Please enter last name: ");
        String lastName = reader.readLine();

        //inputting age
        boolean ageInt = false;
        int age = 0;

        do {
            try{
                System.out.print("Please enter your age: ");
                age = Integer.valueOf(reader.readLine());
                ageInt = true;
            }catch (Exception e){
                System.out.println("Invalid age");
            }
        } while (!ageInt & Validations.validateAge(age));

        //inputting username
        System.out.print("Please enter a unique username: ");
        String userName = reader.readLine();
        while(!Validations.validateUserName(userName)){
            System.out.println("Username must be between 5 to 15 characters");
            System.out.print("Please enter a unique username: ");
            userName = reader.readLine();
        }

            //inputting password
        System.out.print("Please enter password: ");
        String password = reader.readLine();

        while(!Validations.validatePassword(password)){
            System.out.println("Password must be minimum 6 characters");
            System.out.print("Please enter password: ");
            password = reader.readLine();
        }

        System.out.println();

        return new Player(firstName,lastName,age,userName,password,String.valueOf(client_IP_Address),false);


    }


}
