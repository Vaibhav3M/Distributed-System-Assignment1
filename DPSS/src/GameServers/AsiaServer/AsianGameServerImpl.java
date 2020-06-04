package GameServers.AsiaServer;

import Constants.Constants;
import GameServers.DPSS_GameServerInterface;
import Models.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;

public class AsianGameServerImpl extends UnicastRemoteObject implements DPSS_GameServerInterface {
    private static final long serialVersionUID = 7526472295622776147L;

    private static Hashtable<Character, ArrayList<Player>> playersTable = new Hashtable<>();

    protected AsianGameServerImpl() throws RemoteException {
        super();
    }

    @Override
    public String createPlayerAccount(Player player) throws RemoteException {

        char playerKey = player.getUserName().charAt(0);
        System.out.println(playerKey);
        ArrayList<Player> playerList;

        if(playersTable.containsKey(playerKey)){

            playerList = playersTable.get(playerKey);
            System.out.println("Player list is : " + playerList);
            for(int i = 0; i < playerList.size(); i++) {
                Player currPlayer =  playerList.get(i);
                System.out.println("Player list is : " + currPlayer.toString());
                if (currPlayer.getUserName().equalsIgnoreCase(player.getUserName())) {
                    return "UserName already exists";
                }
            }
            playerList.add(player);
        }
        else{
            playerList = new ArrayList<>();
            playerList.add(player);
            playersTable.put(playerKey,playerList);
        }


        return "Successful";
    }

    @Override
    public String playerSignIn(String Username, String Password, String IPAddress) throws RemoteException {

        char playerKey = Username.charAt(0);

        if(playersTable.containsKey(playerKey)){

            ArrayList<Player> playerList = playersTable.get(playerKey);

            for(int i = 0; i < playerList.size(); i++) {
            Player currPlayer = (Player) playerList.get(i);
            if(currPlayer.getUserName().equalsIgnoreCase(Username) && currPlayer.getPassword().equalsIgnoreCase(Password)){

                currPlayer.setSignedIn(true);
                playerList.remove(i);
                playerList.add(currPlayer);
                playersTable.put(playerKey,playerList);

                return currPlayer.getUserName() + " has logged in.";
            }
            }
        }
        else{
            return "User not found";
        }

        return "Error occurred. Please try again";
    }

    @Override
    public String playerSignOut(String Username, String IPAddress) throws RemoteException {

        char playerKey = Username.charAt(0);

        if(playersTable.containsKey(playerKey)){

            ArrayList<Player> playerList = playersTable.get(playerKey);

            for(int i = 0; i < playerList.size(); i++) {
                Player currPlayer = (Player) playerList.get(i);
                if(currPlayer.getUserName().equalsIgnoreCase(Username)){

                    currPlayer.setSignedIn(false);
                    playerList.remove(i);
                    playerList.add(currPlayer);
                    playersTable.put(playerKey,playerList);

                    return currPlayer.getUserName() + " has logged out.";
                }
            }
        }
        else{
            return "User not found";
        }

        return "Error occurred. Please try again";
    }

    @Override
    public String getPlayerStatus(String AdminUsername, String AdminPassword, String IPAddress) throws RemoteException {

        if(!AdminUsername.equalsIgnoreCase("Admin") || !AdminPassword.equalsIgnoreCase("Admin")){
            return "Username or password incorrect.";
        }

        String response = Constants.SERVER_NAME_ASIA +  " has : ";
        int onlineCount = 0;
        int offlineCount = 0;

        for(char key:playersTable.keySet()){
            for(Player p:playersTable.get(key)){
                if(p.isSignedIn())  onlineCount++;
                else offlineCount++;
            }
        }

//        playersTable.forEach((k,v) -> {
//            for(Player p:v){
//                if(p.isSignedIn())  onlineCount++;
//                else offlineCount++;
//            }
//        });

        response = response + onlineCount + " online, " + offlineCount + " offline";
        return response;
    }

}
