package GameServers.EuropeServer;

import Constants.Constants;
import GameServers.DPSS_GameServerInterface;
import Models.Player;
import SendUDP.SendReceiveUDPMessage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EuropeGameServerImpl extends UnicastRemoteObject implements DPSS_GameServerInterface {

    private static Lock lock = new ReentrantLock(true);
    private static Hashtable<Character, ArrayList<Player>> playersTable = new Hashtable();

    protected EuropeGameServerImpl() throws RemoteException {
        super();

        try {
            addDummyData();
        } catch (Exception e) {
            System.out.println("Unable to add dummy data on " + Constants.SERVER_NAME_EUROPE);
        }
    }


    @Override
    public String createPlayerAccount(Player player) throws RemoteException {

        if (checkUserName(player.getUserName())) {
            return "Username already exists";
        }

        char playerKey = player.getUserName().charAt(0);

        ArrayList<Player> playerList;

        try {
            lock.lock();

            if (playersTable.containsKey(playerKey)) {

                playerList = playersTable.get(playerKey);

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

        } finally {
            lock.unlock();
        }

        return "Successful";
    }

    @Override
    public String playerSignIn(String Username, String Password, String IPAddress) throws RemoteException {

        char playerKey = Username.charAt(0);

        try {
            lock.lock();
            if (playersTable.containsKey(playerKey)) {

                ArrayList<Player> playerList = playersTable.get(playerKey);

                for (int i = 0; i < playerList.size(); i++) {
                    Player currPlayer = (Player) playerList.get(i);
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
        } finally {
            lock.unlock();
        }

        return "Error occurred. Please try again";
    }

    @Override
    public String playerSignOut(String Username, String IPAddress) throws RemoteException {

        boolean isFromServerIP = (Integer.parseInt(IPAddress) == Constants.SERVER_IP_PORT_EUROPE);

        char playerKey = Username.charAt(0);
        try {
            lock.lock();
            if (playersTable.containsKey(playerKey)) {

                ArrayList<Player> playerList = playersTable.get(playerKey);

                for (int i = 0; i < playerList.size(); i++) {
                    Player currPlayer = playerList.get(i);
                    if (currPlayer.getUserName().equalsIgnoreCase(Username)) {

                        if (isFromServerIP) {
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
        } finally {
            lock.unlock();
        }

        return "Error occurred. Please try again";
    }

    @Override
    public String getPlayerStatus(String AdminUsername, String AdminPassword, String IPAddress, Boolean checkOtherServers) throws RemoteException {

        if (!AdminUsername.equalsIgnoreCase("Admin") || !AdminPassword.equalsIgnoreCase("Admin")) {
            return "Username or password incorrect.";
        }

        String response = "EU: ";
        int onlineCount = 0;
        int offlineCount = 0;

        try {
            lock.lock();
            for (char key : playersTable.keySet()) {
                for (Player p : playersTable.get(key)) {
                    if (p.isSignedIn()) onlineCount++;
                    else offlineCount++;
                }
            }
        } finally {
            lock.unlock();
        }

        String response_Asia = "";
        String response_America = "";

        if (checkOtherServers) {
            response_Asia = getPlayerStatusUDP(Constants.SERVER_IP_PORT_ASIA);
            response_America = getPlayerStatusUDP(Constants.SERVER_IP_PORT_AMERICA);
        }

        response = response + onlineCount + " online, " + offlineCount + " offline. " + response_Asia + response_America;
        return response;
    }

    private String getPlayerStatusUDP(int serverPort) {

        String[] response = {"No response from " + serverPort};

        SendReceiveUDPMessage sendReceiveUDPMessage = new SendReceiveUDPMessage();

        Thread UDPThread = new Thread(() ->
        {
            try {
                response[0] = sendReceiveUDPMessage.getUDPResponse("playerstatus", serverPort, Constants.SERVER_IP_PORT_EUROPE);

            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }

        });

        UDPThread.setName("Thread - UDP " + serverPort);
        UDPThread.start();

        try {
            UDPThread.join();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return response[0];

    }

    private boolean checkUserName(String userName) {
        SendReceiveUDPMessage sendReceiveUDPMessage = new SendReceiveUDPMessage();

        String check_american = sendReceiveUDPMessage.getUDPResponse("UserName=" + userName, Constants.SERVER_IP_PORT_AMERICA, Constants.SERVER_IP_PORT_EUROPE);
        String check_asia = sendReceiveUDPMessage.getUDPResponse("UserName=" + userName, Constants.SERVER_IP_PORT_ASIA, Constants.SERVER_IP_PORT_EUROPE);

        return !check_american.equalsIgnoreCase("User not found") || !check_asia.equalsIgnoreCase("User not found");
    }

    private void addDummyData() throws Exception {
        createPlayerAccount(new Player("Alex", "Alex", 21, "Alex", "Alex", String.valueOf(Constants.SERVER_IP_PORT_AMERICA), false));
        createPlayerAccount(new Player("Test", "Test", 21, "american", "qwqwqw", String.valueOf(Constants.SERVER_IP_PORT_AMERICA), true));
    }
}
