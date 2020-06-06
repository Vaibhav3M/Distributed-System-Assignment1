package GameServers.AmericaServer;

import Constants.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AmericaGameServer {

    public static void recieve(AmericanGameServerImpl serverImpl) {

        String responseString = "";
        DatagramSocket dataSocket = null;

        try {

            dataSocket = new DatagramSocket(Constants.SERVER_IP_PORT_AMERICA);
            byte[] buffer = new byte[1000];

            System.out.println(Constants.SERVER_NAME_AMERICA + " started..!!!");
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                dataSocket.receive(request);
                String requestMessage = new String(request.getData(), 0, request.getLength());

                if (requestMessage.split("=")[0].equalsIgnoreCase("username")) {
                    responseString = serverImpl.playerSignOut(requestMessage.split("=")[1],String.valueOf(Constants.SERVER_IP_PORT_AMERICA));
                } else {
                    responseString = serverImpl.getPlayerStatus("Admin", "Admin", String.valueOf(request.getPort()), false);
                }
                DatagramPacket reply = new DatagramPacket(responseString.getBytes(), responseString.length(), request.getAddress(), request.getPort());

                dataSocket.send(reply);
            }

        } catch (SocketException e) {
            System.out.println("Socket exception" + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("IO exception" +e.getLocalizedMessage());
        } finally {
            if (dataSocket != null) dataSocket.close();
        }

    }

    public static void main(String args[]) throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    AmericanGameServerImpl serverImplementation = new AmericanGameServerImpl();
                    //RMI setup
                    Registry registry = LocateRegistry.createRegistry(Constants.SERVER_IP_PORT_AMERICA);
                    registry.bind(Constants.SERVER_NAME_AMERICA, serverImplementation);
                    //UDP setup
                    recieve(serverImplementation);

                } catch (Exception e) {
                    System.out.println("Main exception" + e.getLocalizedMessage());
                }
            }
        }).start();

    }
}
