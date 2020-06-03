package GameServers.EuropeServer;

import GameServers.DPSS_GameServerInterface;
import Models.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;

public class EuropeGameServerImpl extends UnicastRemoteObject implements DPSS_GameServerInterface {

    private static Hashtable<String, ArrayList<Player>> playersTable = new Hashtable();

    protected EuropeGameServerImpl() throws RemoteException {
        super();
    }


    @Override
    public String createPlayerAccount(Player player) throws RemoteException {
        return null;
    }

    @Override
    public String playerSignIn(String Username, String Password, String IPAddress) throws RemoteException {
        return null;
    }

    @Override
    public String playerSignOut(String Username, String IPAddress) throws RemoteException {
        return null;
    }

}
