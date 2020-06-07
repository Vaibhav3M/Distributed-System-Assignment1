package Client;

import Constants.Constants;
import GameServers.DPSS_GameServerInterface;
import Utilities.CustomLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;


public class AdminClient {

    private static BufferedReader reader = new BufferedReader((new InputStreamReader(System.in)));
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static int client_IP_Address = 132;
    private static String client_server_name = "";
    private static String adminUsername = "";
    private static String adminPassword = "";
    private static boolean adminLogin = false;

    //Return basic menu.
    private static int showMenu() throws Exception
    {
        System.out.println("\n**** Welcome to DPSS Game ****\n");

        int userinput = 1;

        System.out.println("Please select an option (1 or 2)");
        System.out.println("1. Get players info");
        System.out.println("2. Exit");

        boolean inputValid = false;
        do {
            try{
                System.out.print("Please select an Option : ");
                userinput = Integer.valueOf(reader.readLine().trim());
                inputValid = true;
            }catch (Exception e){
                System.out.println("Oops..! Invalid input. Please select 1 or 2 to perform required action");
            }
        } while (!inputValid);

        return userinput;

    }


    public static void main(String args[]) throws Exception{

        setupLogging();


        System.out.println("Please enter IP : (132, 93, 182)");

        client_IP_Address = getValidIntegerInput();

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
        System.out.println("*************** Welcome to " + client_server_name+" ****************");
        System.out.println("LOADING...... Please be patient");

        Registry registry = LocateRegistry.getRegistry(client_IP_Address);
        DPSS_GameServerInterface dpss_gameServerInterface = (DPSS_GameServerInterface) registry.lookup(client_server_name);

        LOGGER.info( "Admin Session started at " + client_server_name + " on port " + client_IP_Address);
        System.out.println(client_server_name + " at " + client_IP_Address +  " Activated");
        System.out.println();


        while(!adminLogin){
            System.out.println("Login to access admin functions.");
            System.out.print("        Please enter user name: ");
            adminUsername = reader.readLine().trim();

            System.out.print("        Please enter password: ");
            adminPassword = reader.readLine().trim();

            if(adminUsername.equalsIgnoreCase("Admin") && adminUsername.equalsIgnoreCase("Admin")){
                LOGGER.info("Admin logged in successfully.");
                System.out.println("Log in successful");
                adminLogin = true;
            }
            else{
                LOGGER.info("Invalid log in attempt.");
                System.out.println("Credentials invalid. Please try again.");
            }
        }

        boolean exit = false;
        while (!exit) {

            int userinput = showMenu();

            switch (userinput) {

                case 1:
                    System.out.println();

                    String playerInfo = dpss_gameServerInterface.getPlayerStatus(adminUsername,adminPassword,String.valueOf(client_IP_Address),true);
                    LOGGER.info("Admin requested player info. ");
                    LOGGER.info("Info received: " + playerInfo);
                    System.out.println(playerInfo);
                    break;

                case 2:
                    LOGGER.info("Admin session over.");
                    System.out.println("Thank you for visiting our DPSS app");
                    exit = true;
                    break;

            }
        }
    }

    private static int getValidIntegerInput() {

        int value = 0;
        boolean inputValid = false;
        do {
            try {
                String input = reader.readLine().trim();
                if(input.contains(".")){
                    input = input.split("\\.")[0];
                }
                value = Integer.valueOf(input);
                inputValid = true;
                //logger.info("User selected " + value);
            } catch (Exception e) {
                System.out.println("This field requires a number value. Please try again");
            }
        } while (!inputValid);

        return value;
    }

    private static void setupLogging() throws IOException {
        File files = new File(Constants.ADMIN_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.ADMIN_LOG_DIRECTORY+"Admin.log");
        if(!files.exists())
            files.createNewFile();
        CustomLogger.setup(files.getAbsolutePath());
    }


}
