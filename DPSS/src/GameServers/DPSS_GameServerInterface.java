package GameServers;

import Models.Player;

import java.rmi.*;

/**
 * The interface Dpss game server interface.
 */
public interface DPSS_GameServerInterface extends Remote {

    //Player methods

    /**
     * Create player and add to hashtable.
     *
     * @param player the player
     * @return Return the status
     * @throws RemoteException the remote exception
     */

    public String createPlayerAccount(Player player) throws RemoteException;

    /**
     * Player sign in method.
     *
     * @param Username  the username
     * @param Password  the password
     * @param IPAddress the ip address
     * @return Return the status
     * @throws RemoteException the remote exception
     */
    public String playerSignIn (String Username, String Password, String IPAddress) throws RemoteException;

    /**
     * Player sign out method.
     *
     * @param Username  the username
     * @param IPAddress the ip address
     * @return Return the status
     * @throws RemoteException the remote exception
     */
    public String playerSignOut (String Username, String IPAddress) throws RemoteException;

    //Admin method


    /**
     * Gets player status from server and other servers using UDP.
     *
     * @param AdminUsername     the admin username
     * @param AdminPassword     the admin password
     * @param IPAddress         the ip address
     * @param checkOtherServers the check other servers
     * @return the player status
     * @throws RemoteException the remote exception
     */
    public String getPlayerStatus (String AdminUsername, String AdminPassword, String IPAddress, Boolean checkOtherServers) throws RemoteException;


}
