package GameServers;

import Models.Player;

import java.rmi.*;

public interface DPSS_GameServerInterface extends Remote {

    //Player methods
    public String createPlayerAccount(Player player) throws RemoteException;
    public String playerSignIn (String Username, String Password, String IPAddress) throws RemoteException;
    public String playerSignOut (String Username, String IPAddress) throws RemoteException;

    //Admin method
    public String getPlayerStatus (String AdminUsername, String AdminPassword, String IPAddress) throws RemoteException;


}
