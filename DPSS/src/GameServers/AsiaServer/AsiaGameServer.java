package GameServers.AsiaServer;

import Constants.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AsiaGameServer {

    public static void recieve(AsianGameServerImpl serverImpl) {

        String responseString = "";
        DatagramSocket dataSocket = null;

        try {

            dataSocket = new DatagramSocket(Constants.SERVER_IP_PORT_ASIA);
            byte[] buffer = new byte[1000];

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                dataSocket.receive(request);
                String credentials = new String(request.getData(),0,request.getLength());
                String adminUsername = credentials.split("-")[0];
                String adminPassword = credentials.split("-")[1];

                responseString = serverImpl.getPlayerStatus(adminUsername,adminPassword,String.valueOf(request.getPort()));

                DatagramPacket reply = new DatagramPacket(responseString.getBytes(), responseString.length(), request.getAddress(), request.getPort());

                dataSocket.send(reply);
            }

        } catch (SocketException e) {

        } catch (
                IOException e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            if (dataSocket != null) dataSocket.close();
        }

    }

    public static void main(String args[]) throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    AsianGameServerImpl serverImplementation = new AsianGameServerImpl();
                    //RMI setup
                    Registry registry = LocateRegistry.createRegistry(Constants.SERVER_IP_PORT_ASIA);
                    registry.bind(Constants.SERVER_NAME_ASIA, serverImplementation);
                    //UDP setup
                    recieve(serverImplementation);

                    System.out.println(Constants.SERVER_NAME_ASIA + " started..!!!");
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }).start();

    }
}
