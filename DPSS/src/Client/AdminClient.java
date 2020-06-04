package Client;

import Constants.Constants;
import GameServers.DPSS_GameServerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;


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
                    getUDPResponse();
                    break;

                case 3:
                    System.out.println("Thank you for visiting our DPSS app");
                    exit = true;
                    break;

            }
        }
    }

    private static void getUDPResponse() {

        DatagramSocket datagramSocket = null;

        try{
            datagramSocket = new DatagramSocket();

            byte[] message = (adminUsername  + "-" +adminPassword).getBytes();
            InetAddress hostAddress = InetAddress.getByName("localhost");

            DatagramPacket request = new DatagramPacket(message,message.length,hostAddress,client_IP_Address);
            datagramSocket.send(request);

            byte[] buffer = new byte[1000];

            DatagramPacket serverResponse = new DatagramPacket(buffer,buffer.length);
            datagramSocket.receive(serverResponse);

            System.out.println("Player info: " + new String(serverResponse.getData()));

        }catch (SocketException e){
            System.out.println("Socket creation failed due to: " + e.getLocalizedMessage());
        }catch (UnknownHostException e){
            System.out.println(e.getLocalizedMessage());
        }catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
        finally {
            if(datagramSocket != null) datagramSocket.close();
        }


    }

}
