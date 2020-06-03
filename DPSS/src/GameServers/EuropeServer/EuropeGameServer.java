package GameServers.EuropeServer;

import Constants.Constants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EuropeGameServer {

    public static void main(String args[]) throws Exception{

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                EuropeGameServerImpl serverImplementation = new EuropeGameServerImpl();
                Registry registry = LocateRegistry.createRegistry(Constants.SERVER_IP_PORT_EUROPE);

                registry.bind(Constants.SERVER_NAME_EUROPE, serverImplementation);

                System.out.println(Constants.SERVER_NAME_EUROPE + " started..!!!");
                }catch (Exception e){
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }).start();


    }
}
