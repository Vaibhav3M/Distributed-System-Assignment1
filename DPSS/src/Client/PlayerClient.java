package Client;

import Constants.Constants;
import GameServers.DPSS_GameServerInterface;
import Models.Player;
import Constants.Validations;
import Utilities.CustomLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class PlayerClient {

    private static BufferedReader reader = new BufferedReader((new InputStreamReader(System.in)));
    private static int client_IP_Address = 0;
    private static int server_port_number = 0;
    private static String client_server_name = "";
    private static DPSS_GameServerInterface dpss_gameServerInterface = null;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static FileHandler fileHandler = null;


    // Return basic menu.
    private static int showMenu() throws Exception {


        System.out.println("\n**** Welcome to DPSS Game ****\n");

        int userinput = 1;

        System.out.println("Please select an option (1-4)");
        System.out.println("1. Create new Player");
        System.out.println("2. SignIn");
        System.out.println("3. SignOut");
        System.out.println("4. Exit");

		System.out.print("Please select an Option : ");

		userinput = getValidIntegerInput();

        return userinput;

    }


	public static void main(String args[]) throws Exception {

//        setupLogging("general");
//		while (!Validations.validateIP(client_IP_Address)) {
//
//			System.out.println("Please enter IP : (132, 93, 182)");
//			client_IP_Address = getValidIntegerInput();
//
//			switch (client_IP_Address) {
//
//				case 132:
//					client_server_name = Constants.SERVER_NAME_AMERICA;
//                    server_port_number = Constants.SERVER_PORT_AMERICA;
//					break;
//
//				case 93:
//					client_server_name = Constants.SERVER_NAME_EUROPE;
//					server_port_number = Constants.SERVER_PORT_EUROPE;
//					break;
//
//				case 182:
//					client_server_name = Constants.SERVER_NAME_ASIA;
//					server_port_number = Constants.SERVER_PORT_ASIA;
//					break;
//				default:
//					System.out.println("Invalid server IP");
//
//			}
//		}
//		System.out.println("*************** Welcome to " + client_server_name+" ****************");
//        System.out.println("LOADING...... Please be patient");


        //LOGGER.info("Activated : " + client_server_name + " at " + server_port_number);
        boolean exit = false;

        while (!exit) {

            int userinput = showMenu();

            switch (userinput) {

                case 1:
                    client_IP_Address = 0;
                    Player newPlayer = createPlayer();
                    String result1 = dpss_gameServerInterface.createPlayerAccount(newPlayer);
                    setupLogging(newPlayer.getUserName());
                    LOGGER.info(newPlayer.getUserName() + " request to create new account on " + client_server_name);
                    LOGGER.info(result1);
                    if (fileHandler != null) fileHandler.close();
                    System.out.println("Message: " +result1);
                    Thread.sleep(100);
                    break;

                case 2:
                    client_IP_Address = 0;
                    System.out.print("Please enter user name: ");
                    String userNameLogin = reader.readLine().trim();

                    System.out.print("Please enter password: ");
                    String password = reader.readLine().trim();

                    while (!Validations.validateIP(client_IP_Address)) {

                        System.out.print("Please enter IP starting (132, 93, 182): ");
                        client_IP_Address = getValidIntegerInput();
                    }
                    getServerFromIP(client_IP_Address);


                    System.out.println();

                    setupLogging(userNameLogin);
                    LOGGER.info(userNameLogin + " attempted to sign in on " + client_server_name);
                    String result2 = dpss_gameServerInterface.playerSignIn(userNameLogin, password,
                            String.valueOf(client_IP_Address));
                    LOGGER.info(result2);
                    System.out.println("Message: " +result2);
                    if (fileHandler != null) fileHandler.close();

                    Thread.sleep(100);
                    break;

                case 3:
                    client_IP_Address = 0;
                    System.out.print("Please enter user name: ");
                    String userNameLogout = reader.readLine().trim();

                    System.out.println();
                    setupLogging(userNameLogout);
                    LOGGER.info(userNameLogout + " attempted to sign out of " + client_server_name);

                    while (!Validations.validateIP(client_IP_Address)) {

                        System.out.print("Please enter IP starting (132, 93, 182): ");
                        client_IP_Address = getValidIntegerInput();
                    }
                    getServerFromIP(client_IP_Address);

                    String result3 = dpss_gameServerInterface.playerSignOut(userNameLogout, String.valueOf(client_IP_Address));
                    LOGGER.info(result3);
                    if (fileHandler != null) fileHandler.close();
                    System.out.println("Message: " + result3);
                    Thread.sleep(100);
                    break;

                case 4:
                    LOGGER.info("Exited the session.");
                   if (fileHandler != null) fileHandler.close();
                    System.out.println("Thank you for visiting our DPSS app");
                    Thread.sleep(100);
                    exit = true;
                    break;

				default:
					System.out.println("Oops..! Invalid input. Please select 1,2,3 or 4 to perform required action");

            }
        }
    }

    private static Player createPlayer() throws Exception {

        // inputting first name
        System.out.print("Please enter first name: ");
        String firstName = reader.readLine().trim();

        // inputting last name
        System.out.print("Please enter last name: ");
        String lastName = reader.readLine().trim();

        // inputting age
        boolean ageInt = false;
        int age = 0;

        do {
            try {
                System.out.print("Please enter your age: ");
                age = getValidIntegerInput();
                ageInt = true;
            } catch (Exception e) {
                System.out.println("Invalid age");
            }
        } while (!ageInt & Validations.validateAge(age));

        // inputting username
        System.out.print("Please enter a unique username: ");
        String userName = reader.readLine();
        while (!Validations.validateUserName(userName)) {
            System.out.println("Error: Username must be between 5 to 15 characters");
            System.out.print("Please enter a unique username: ");
            userName = reader.readLine().trim();
        }

        // inputting password
        System.out.print("Please enter password: ");
        String password = reader.readLine().trim();

        while (!Validations.validatePassword(password)) {
            System.out.println("Error: Password must be minimum 6 characters");
            System.out.print("Please enter password: ");
            password = reader.readLine().trim();
        }


        while (!Validations.validateIP(client_IP_Address)) {

            System.out.print("Please enter IP starting (132, 93, 182): ");
            client_IP_Address = getValidIntegerInput();
        }
        getServerFromIP(client_IP_Address);


        System.out.println();

        return new Player(firstName, lastName, age, userName, password, String.valueOf(client_IP_Address), false);

    }

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
            Registry registry = LocateRegistry.getRegistry(server_port_number);
            dpss_gameServerInterface = (DPSS_GameServerInterface) registry.lookup(client_server_name);

        }catch (Exception e){
            System.out.println( e.getLocalizedMessage());
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

			} catch (Exception e) {
				System.out.println("Error: This field requires a number value. Please try again");
			}
		} while (!inputValid);

		return value;
	}


    private static void setupLogging(String name) throws IOException {
        File files = new File(Constants.PLAYER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.PLAYER_LOG_DIRECTORY+"Player_"+ name+ ".log");
        if(!files.exists())
            files.createNewFile();
        fileHandler = CustomLogger.setup(files.getAbsolutePath());
    }

}
