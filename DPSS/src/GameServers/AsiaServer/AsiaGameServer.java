package GameServers.AsiaServer;

import Constants.Constants;
import Utilities.CustomLogger;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class AsiaGameServer {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static FileHandler fileHandler = null;

    public static void recieve(AsianGameServerImpl serverImpl) {

        String responseString = "";
        DatagramSocket dataSocket = null;

        try {

            dataSocket = new DatagramSocket(Constants.SERVER_IP_PORT_ASIA);
            byte[] buffer = new byte[1000];
            LOGGER.info( "Server started..!!!");
            System.out.println(Constants.SERVER_NAME_ASIA + " started..!!!");
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                dataSocket.receive(request);
                String requestMessage = new String(request.getData(),0,request.getLength());

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
           LOGGER.info("Exception at socket" +e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.info("Exception at IO" +e.getLocalizedMessage());
        } finally {
            if (dataSocket != null) dataSocket.close();
            fileHandler.close();
        }

    }

    public static void main(String args[]) throws Exception {

        Thread server_asia = new Thread(()->
        {
                try {
                    AsianGameServerImpl serverImplementation = new AsianGameServerImpl(LOGGER);
                    //RMI setup
                    Registry registry = LocateRegistry.createRegistry(Constants.SERVER_IP_PORT_ASIA);
                    registry.bind(Constants.SERVER_NAME_ASIA, serverImplementation);
                    //setup logger
                    setupLogging();
                    //UDP setup
                    recieve(serverImplementation);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception at main" +e.getLocalizedMessage());
                }
            });

        server_asia.setName("thread_Asia_server");
        server_asia.start();


    }

    private static void setupLogging() throws IOException {
        File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.SERVER_LOG_DIRECTORY+"ASIA_Server.log");
        if(!files.exists())
            files.createNewFile();
        fileHandler = CustomLogger.setup(files.getAbsolutePath());
    }

}
