package GameServers.AsiaServer;

import Constants.Constants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AsiaGameServer {

    public static void main(String args[]) throws Exception{

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    AsianGameServerImpl serverImplementation = new AsianGameServerImpl();
                    Registry registry = LocateRegistry.createRegistry(Constants.SERVER_IP_PORT_ASIA);

                    registry.bind(Constants.SERVER_NAME_ASIA, serverImplementation);

                    System.out.println(Constants.SERVER_NAME_ASIA + " started..!!!");
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }).start();

    }
}
