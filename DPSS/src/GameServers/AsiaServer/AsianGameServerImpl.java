package GameServers.AsiaServer;

import Constants.Constants;
import GameServers.DPSS_GameServerInterface;
import Models.Player;
import SendUDP.SendReceiveUDPMessage;

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

        if(checkUserName(player.getUserName())){
            return "Username already exists";
        }

        char playerKey = player.getUserName().charAt(0);
        ArrayList<Player> playerList;

        if (playersTable.containsKey(playerKey)) {

            playerList = playersTable.get(playerKey);
            System.out.println("Player list is : " + playerList);
            for (int i = 0; i < playerList.size(); i++) {
                Player currPlayer = playerList.get(i);
                System.out.println("Player list is : " + currPlayer.toString());
                if (currPlayer.getUserName().equalsIgnoreCase(player.getUserName())) {
                    return "UserName already exists";
                }
            }
            playerList.add(player);
        } else {
            playerList = new ArrayList<>();
            playerList.add(player);
            playersTable.put(playerKey, playerList);
        }


        return "Successful";
    }

    @Override
    public String playerSignIn(String Username, String Password, String IPAddress) throws RemoteException {

        char playerKey = Username.charAt(0);

        if (playersTable.containsKey(playerKey)) {

            ArrayList<Player> playerList = playersTable.get(playerKey);

            for (int i = 0; i < playerList.size(); i++) {
                Player currPlayer =  playerList.get(i);
                if (currPlayer.getUserName().equalsIgnoreCase(Username) && currPlayer.getPassword().equalsIgnoreCase(Password)) {

                    currPlayer.setSignedIn(true);
                    playerList.remove(i);
                    playerList.add(currPlayer);
                    playersTable.put(playerKey, playerList);

                    return currPlayer.getUserName() + " has logged in.";
                }
            }
        } else {
            return "User not found";
        }

        return "Error occurred. Please try again";
    }

    @Override
    public String playerSignOut(String Username, String IPAddress) throws RemoteException {

        boolean isFromServerIP = (Integer.parseInt(IPAddress) == Constants.SERVER_IP_PORT_ASIA);

        char playerKey = Username.charAt(0);

        if (playersTable.containsKey(playerKey)) {

            ArrayList<Player> playerList = playersTable.get(playerKey);

            for (int i = 0; i < playerList.size(); i++) {
                Player currPlayer = playerList.get(i);
                if (currPlayer.getUserName().equalsIgnoreCase(Username)) {

                    if(isFromServerIP) {
                        currPlayer.setSignedIn(false);
                        playerList.remove(i);
                        playerList.add(currPlayer);
                        playersTable.put(playerKey, playerList);
                    }

                    return currPlayer.getUserName() + " has logged out.";
                }
            }
        } else {
            return "User not found";
        }

        return "User not found";
    }


    @Override
    public String getPlayerStatus(String AdminUsername, String AdminPassword, String IPAddress, Boolean checkOtherServers) throws RemoteException {

        if(!AdminUsername.equalsIgnoreCase("Admin") || !AdminPassword.equalsIgnoreCase("Admin")){
            return "Username or password incorrect.";
        }

        String response = Constants.SERVER_NAME_ASIA + " has : ";
        int onlineCount = 0;
        int offlineCount = 0;

        for (char key : playersTable.keySet()) {
            for (Player p : playersTable.get(key)) {
                if (p.isSignedIn()) onlineCount++;
                else offlineCount++;
            }
        }

        String response_America = "";
        String response_Europe = "";

        if (checkOtherServers) {
            response_America = getPlayerStatusUDP(Constants.SERVER_IP_PORT_AMERICA);
            response_Europe = getPlayerStatusUDP(Constants.SERVER_IP_PORT_EUROPE);
        }

        response = response + onlineCount + " online, " + offlineCount + " offline. " + response_America + response_Europe;
        return response;
    }

    private String getPlayerStatusUDP(int serverPort) {

        String[] response = {"No response from " + serverPort};

        SendReceiveUDPMessage sendReceiveUDPMessage = new SendReceiveUDPMessage();

        Thread UDPThread = new Thread(()->
        {
            try {
                response[0] = sendReceiveUDPMessage.getUDPResponse("", serverPort);

            } catch (Exception e) {
                System.out.println("Exception at getplayerstatus" +e.getLocalizedMessage());
            }

        });

        UDPThread.setName("Thread - UDP " + serverPort);
        UDPThread.start();

        try{
            UDPThread.join();
        }catch (Exception e){
            System.out.println("Exception at getplayerstatus" + e.getLocalizedMessage());
        }

        return response[0];

    }

    private boolean checkUserName(String userName) {
        SendReceiveUDPMessage sendReceiveUDPMessage = new SendReceiveUDPMessage();

        String check_american = sendReceiveUDPMessage.getUDPResponse("UserName="+userName,Constants.SERVER_IP_PORT_AMERICA);
        String check_europe = sendReceiveUDPMessage.getUDPResponse("UserName="+userName,Constants.SERVER_IP_PORT_EUROPE);

        return !check_american.equalsIgnoreCase("User not found") || !check_europe.equalsIgnoreCase("User not found");
    }

}
