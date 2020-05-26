package GameServers.AsiaServer;

import GameServers.DPSS_GameServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AsiaGameServer extends UnicastRemoteObject implements DPSS_GameServerInterface {

    protected AsiaGameServer() throws RemoteException {
        super();
    }

    @Override
    public String createPlayerAccount(String FirstName, String LastName, int Age, String Username, String Password, String IPAddress) {
        return null;
    }

    @Override
    public String playerSignIn(String Username, String Password, String IPAddress) {
        return null;
    }

    @Override
    public String playerSignOut(String Username, String IPAddress) {
        return null;
    }

    @Override
    public String getPlayerStatus(String AdminUsername, String AdminPassword, String IPAddress) {
        return null;
    }
}
