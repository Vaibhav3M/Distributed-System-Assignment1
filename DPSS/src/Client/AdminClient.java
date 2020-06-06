package Client;

import Constants.Constants;
import GameServers.DPSS_GameServerInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class AdminClient {

    private static BufferedReader reader = new BufferedReader((new InputStreamReader(System.in)));
    private static int client_IP_Address = 132;
    private static String client_server_name = "";
    private static String adminUsername = "";
    private static String adminPassword = "";

    //Return basic menu.
    private static int showMenu() throws Exception
    {
        System.out.println("\n**** Welcome to DPSS Game ****\n");

        int userinput = 1;

        System.out.println("Please select an option (1,2 or 3)");
        System.out.println("1. Login");
        System.out.println("2. Get players info");
        System.out.println("3. Exit");

        boolean inputValid = false;
        do {
            try{
                System.out.print("Please select an Option : ");
                userinput = Integer.valueOf(reader.readLine());
                inputValid = true;
            }catch (Exception e){
                System.out.println("Oops..! Invalid input. Please select 1, 2 or 3 to perform required action");
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
        System.out.println("LOADING SERVER......");

        Registry registry = LocateRegistry.getRegistry(client_IP_Address);
        DPSS_GameServerInterface dpss_gameServerInterface = (DPSS_GameServerInterface) registry.lookup(client_server_name);
        System.out.println(client_server_name + " at " + client_IP_Address +  " Activated");

        boolean exit = false;
        while (!exit) {

            int userinput = showMenu();

            switch (userinput) {

                case 1:
                    System.out.print("Please enter user name: ");
                    adminUsername = reader.readLine();

                    System.out.print("Please enter password: ");
                    adminPassword = reader.readLine();

                    if(adminUsername.equalsIgnoreCase("Admin") && adminUsername.equalsIgnoreCase("Admin")){
                        System.out.println("Log in successful");
                    }

                    break;

                case 2:
                    System.out.println();
                    //getUDPResponse();
                    System.out.println(dpss_gameServerInterface.getPlayerStatus(adminUsername,adminPassword,String.valueOf(client_IP_Address),true));
                    break;

                case 3:
                    System.out.println("Thank you for visiting our DPSS app");
                    exit = true;
                    break;

            }
        }
    }


}
