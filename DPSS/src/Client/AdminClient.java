package Client;

import Constants.*;
import GameServers.DPSS_GameServerInterface;
import Utilities.CustomLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


/**
 * The Admin client class.
 */
public class AdminClient {

    private static BufferedReader reader = new BufferedReader((new InputStreamReader(System.in)));
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // to manage log files
    static FileHandler fileHandler = null;

    private static int client_IP_Address = 0;
    private static String client_server_name = "";
    private static int server_port_number = 0;
    private static String adminUsername = "";
    private static String adminPassword = "";
    private static boolean adminLogin = false;
    // gameserver interface object
    private static DPSS_GameServerInterface dpss_gameServerInterface =null;

    //Return Admin menu.
    private static int showMenu() throws Exception
    {
        int userinput = 1;

        System.out.println("Please select an option (1 or 2)");
        System.out.println("1. Login");
        System.out.println("2. Get players info");
        System.out.println("3. Exit");

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


    /**
     * Main.
     *
     * @param args the args
     * @throws Exception the exception
     */
    public static void main(String args[]) throws Exception{

        //setup for logger
        setupLogging();

        System.out.println("\n**** Welcome to DPSS Game ****\n");


        boolean exit = false;
        while (!exit) {

            int userinput = showMenu();

            switch (userinput) {

                //login for admin
                case 1:
                    adminLogin = false;
                    client_IP_Address = 0;
                    while(!adminLogin){
                        System.out.println("Login to access admin functions.");
                        System.out.print("        Please enter user name: ");
                        adminUsername = reader.readLine().trim();

                        System.out.print("        Please enter password: ");
                        adminPassword = reader.readLine().trim();

                        while (!Validations.validateIP(client_IP_Address)) {

                            System.out.print("        Please enter IP starting (132, 93, 182): ");
                            client_IP_Address = getValidIntegerInput();
                        }
                        getServerFromIP(client_IP_Address);

                        if(adminUsername.equalsIgnoreCase("Admin") && adminUsername.equalsIgnoreCase("Admin")){
                            LOGGER.info("Admin logged in successfully at " + client_IP_Address);
                            System.out.println("Message: Log in successful");
                            System.out.println();
                            adminLogin = true;
                        }
                        else{
                            adminLogin = false;
                            client_IP_Address = 0;
                            LOGGER.info("Invalid log in attempt at " + client_IP_Address);
                            System.out.println("Error: Credentials invalid. Please try again.");
                            System.out.println();
                        }
                    }
                    break;

                //get player status for admin
                case 2:
                    if(!adminLogin) {
                        System.out.println("Error: Please login to access this functionality.");
                        System.out.println();
                        break;
                    }
                    System.out.println();
                    String playerInfo = dpss_gameServerInterface.getPlayerStatus(adminUsername,adminPassword,String.valueOf(client_IP_Address),true);
                    LOGGER.info("Admin requested player info at " + client_IP_Address);
                    LOGGER.info("Info received: " + playerInfo);
                    System.out.println(playerInfo);
                    System.out.println();
                    break;

                //end admin session
                case 3:
                    LOGGER.info("Admin session over at " + client_IP_Address);
                    if (fileHandler != null) fileHandler.close();
                    System.out.println("Message: Thank you for visiting our DPSS app");
                    exit = true;
                    break;

            }
        }
    }

    /**
     * getServerFromIP. - This method takes IP by user and gets RMI registry
     *
     * @param client_IP_Address IP address entered by user
     */
    private static void getServerFromIP(int client_IP_Address){

        switch (client_IP_Address) {

            case 132:
                client_server_name = Constants.SERVER_NAME_AMERICA;
                server_port_number = Constants.SERVER_PORT_AMERICA;
                break;

            case 93:
                client_server_name = Constants.SERVER_NAME_EUROPE;
                server_port_number = Constants.SERVER_PORT_EUROPE;
                break;

            case 182:
                client_server_name = Constants.SERVER_NAME_ASIA;
                server_port_number = Constants.SERVER_PORT_ASIA;
                break;
            default:
                System.out.println("Error: Invalid server IP");


        }

        try {
            System.out.println("***** Verifying info from server. Please wait. *****");
            //get registry for port number
            Registry registry = LocateRegistry.getRegistry(server_port_number);
            // initilize the interface object
            dpss_gameServerInterface = (DPSS_GameServerInterface) registry.lookup(client_server_name);

        }catch (Exception e){
            System.out.println( e.getLocalizedMessage());
        }
    }

    /**
     * getValidIntegerInput. - Takes input from console and validates proper integer
     *
     */
    private static int getValidIntegerInput() {

        int value = 0;
        boolean inputValid = false;
        do {
            try {
                String input = reader.readLine().trim();
                if(input.contains(".")){
                    input = input.split("\\.")[0];
                }
                value = Integer.parseInt(input);
                inputValid = true;

            } catch (Exception e) {
                System.out.println("This field requires a number value. Please try again");
            }
        } while (!inputValid);

        return value;
    }

    /**
     * setupLogging. - Setup logger for the class
     *
     */
    private static void setupLogging() throws IOException {
        File files = new File(Constants.ADMIN_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.ADMIN_LOG_DIRECTORY+"Admin.log");
        if(!files.exists())
            files.createNewFile();
       fileHandler = CustomLogger.setup(files.getAbsolutePath());
    }


}
