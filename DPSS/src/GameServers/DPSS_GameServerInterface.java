package GameServers;

import java.rmi.*;

public interface DPSS_GameServerInterface extends Remote {

    //Player methods
    public String createPlayerAccount(String FirstName, String LastName, int Age, String Username, String Password, String IPAddress);
    public String playerSignIn (String Username, String Password, String IPAddress);
    public String playerSignOut (String Username, String IPAddress);

    //Admin method
    public String getPlayerStatus (String AdminUsername, String AdminPassword, String IPAddress);


}
