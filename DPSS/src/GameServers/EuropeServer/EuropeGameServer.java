package GameServers.EuropeServer;

import Constants.Constants;
import Utilities.CustomLogger;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class EuropeGameServer {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void recieve(EuropeGameServerImpl serverImpl) {

        String responseString = "";
        DatagramSocket dataSocket = null;

        try {

            dataSocket = new DatagramSocket(Constants.SERVER_IP_PORT_EUROPE);
            byte[] buffer = new byte[1000];

            LOGGER.info( "Server started..!!!");
            System.out.println(Constants.SERVER_NAME_EUROPE + " started..!!!");
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                dataSocket.receive(request);
                String requestMessage = new String(request.getData(),0,request.getLength());
                System.out.println(requestMessage);

                LOGGER.info("Received UDP request message: " + requestMessage);


                String request_IP = requestMessage.split(":")[0];
                requestMessage = requestMessage.split(":")[1];

                if (requestMessage.split("=")[0].equalsIgnoreCase("username")) {
                    responseString = serverImpl.playerSignOut(requestMessage.split("=")[1],request_IP);
                } else {
                    responseString = serverImpl.getPlayerStatus("Admin", "Admin", String.valueOf(request.getPort()), false);
                }

                LOGGER.info("Sent UDP response message: " + responseString);

                DatagramPacket reply = new DatagramPacket(responseString.getBytes(), responseString.length(), request.getAddress(), request.getPort());
                dataSocket.send(reply);
            }

        } catch (SocketException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            if (dataSocket != null) dataSocket.close();
        }

    }

    public static void main(String args[]) throws Exception {

        Thread server_europe = new Thread(()->
        {
                try {
                    EuropeGameServerImpl serverImplementation = new EuropeGameServerImpl();
                    //RMI setup
                    Registry registry = LocateRegistry.createRegistry(Constants.SERVER_IP_PORT_EUROPE);
                    registry.bind(Constants.SERVER_NAME_EUROPE, serverImplementation);

                    setupLogging();

                    //UDP setup
                    recieve(serverImplementation);

                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            });

        server_europe.setName("thread_Europe_server");
        server_europe.start();

    }

    private static void setupLogging() throws IOException {
        File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.SERVER_LOG_DIRECTORY+"EUROPE_Server.log");
        if(!files.exists())
            files.createNewFile();
        CustomLogger.setup(files.getAbsolutePath());
    }
}
