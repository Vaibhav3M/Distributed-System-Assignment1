package GameServers.AmericaServer;

import Constants.Constants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class AmericaGameServer {


    public static void main(String args[]) throws Exception{

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    AmericanGameServerImpl serverImplementation = new AmericanGameServerImpl();
                    Registry registry = LocateRegistry.createRegistry(Constants.SERVER_IP_PORT_AMERICA);

                    registry.bind(Constants.SERVER_NAME_AMERICA, serverImplementation);

                    System.out.println(Constants.SERVER_IP_PORT_AMERICA + " started..!!!");
                }
                catch (Exception e){
                    System.out.println(e.getLocalizedMessage());
                }

            }
        }).start();


    }

}
