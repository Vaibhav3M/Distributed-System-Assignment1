package GameServers.AmericaServer;

import GameServers.DPSS_GameServerInterface;
import Models.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AmericaGameServer extends UnicastRemoteObject implements DPSS_GameServerInterface {

    protected AmericaGameServer() throws RemoteException {
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
