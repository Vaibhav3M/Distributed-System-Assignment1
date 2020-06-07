package GameServers.AsiaServer;

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
import java.util.logging.Logger;

public class AsianGameServerImpl extends UnicastRemoteObject implements DPSS_GameServerInterface {
    private static final long serialVersionUID = 7526472295622776147L;

    private static Lock lock = new ReentrantLock(true);
    private static Hashtable<Character, ArrayList<Player>> playersTable = new Hashtable<>();
    private static Logger LOGGER;


    protected AsianGameServerImpl(Logger logger) throws RemoteException {
        super();
        LOGGER = logger;
    }

    @Override
    public String createPlayerAccount(Player player) throws RemoteException {

        LOGGER.info("Received RMI request - Create Player - " + player.toString());

        if (checkUserName(player.getUserName())) {

            LOGGER.info("Username=" + player.getUserName() +" already existed");
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

                    if (currPlayer.getUserName().equalsIgnoreCase(player.getUserName())) {
                        LOGGER.info("Username=" + player.getUserName() +" already existed");

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

        LOGGER.info("Player Created successfully - " + player.toString());

        return "Successful";
    }

    @Override
    public String playerSignIn(String Username, String Password, String IPAddress) throws RemoteException {

        LOGGER.info("Received RMI request - SignIn Player - " + "Username=" +Username);

        char playerKey = Username.charAt(0);

        try {
            lock.lock();
            if (playersTable.containsKey(playerKey)) {

                ArrayList<Player> playerList = playersTable.get(playerKey);

                for (int i = 0; i < playerList.size(); i++) {
                    Player currPlayer = playerList.get(i);
                    if (currPlayer.getUserName().equalsIgnoreCase(Username) && currPlayer.getPassword().equalsIgnoreCase(Password)) {

                        currPlayer.setSignedIn(true);
                        playerList.remove(i);
                        playerList.add(currPlayer);
                        playersTable.put(playerKey, playerList);

                        LOGGER.info("Player SignedIn - " + "Username=" + Username);
                        return currPlayer.getUserName() + " has logged in.";
                    }
                }
            } else {

                LOGGER.info("Player not found - " + "Username=" + Username);
                return Username + " not found";
            }
        } finally {
            lock.unlock();
        }

        return Username + " not found";
    }

    @Override
    public String playerSignOut(String Username, String IPAddress) throws RemoteException {

        boolean isFromServerIP = (Integer.parseInt(IPAddress) == Constants.SERVER_IP_PORT_ASIA);

        char playerKey = Username.charAt(0);

        try {
            lock.lock();
            if (playersTable.containsKey(playerKey)) {

                ArrayList<Player> playerList = playersTable.get(playerKey);

                for (int i = 0; i < playerList.size(); i++) {
                    Player currPlayer = playerList.get(i);
                    if (currPlayer.getUserName().equalsIgnoreCase(Username)) {

                        if (isFromServerIP) {
                            LOGGER.info("Received RMI request - SignOut Player - " + Username);

                            currPlayer.setSignedIn(false);
                            playerList.remove(i);
                            playerList.add(currPlayer);
                            playersTable.put(playerKey, playerList);
                        }
                        LOGGER.info("Player SignedOut - " + "Username=" + Username);

                        return currPlayer.getUserName() + " has logged out.";
                    }
                }
            } else {
                LOGGER.info("Player not found - " + "Username=" + Username);
                return  "User not found";
            }
        } finally {
            lock.unlock();
        }

        return  "User not found";
    }


    @Override
    public String getPlayerStatus(String AdminUsername, String AdminPassword, String IPAddress, Boolean checkOtherServers) throws RemoteException {

        if (!AdminUsername.equalsIgnoreCase("Admin") || !AdminPassword.equalsIgnoreCase("Admin")) {
            return "Username or password incorrect.";
        }

        String response =  "AS: ";
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

        LOGGER.info("Created UDP request - Get player status from port "+ serverPort);
        String[] response = {"No response from " + serverPort};

        SendReceiveUDPMessage sendReceiveUDPMessage = new SendReceiveUDPMessage();

        Thread UDPThread = new Thread(() ->
        {
            try {
                response[0] = sendReceiveUDPMessage.getUDPResponse("playerstatus", serverPort,Constants.SERVER_IP_PORT_ASIA);

            } catch (Exception e) {
                System.out.println("Exception at getplayerstatus" + e.getLocalizedMessage());
            }

        });

        UDPThread.setName("Thread - UDP " + serverPort);
        UDPThread.start();

        try {
            UDPThread.join();
        } catch (Exception e) {
            System.out.println("Exception at getplayerstatus" + e.getLocalizedMessage());
        }
        LOGGER.info("Received UDP response from "+ serverPort + " - " + response[0]);
        return response[0];

    }

    private boolean checkUserName(String userName) {
        SendReceiveUDPMessage sendReceiveUDPMessage = new SendReceiveUDPMessage();

        String check_american = sendReceiveUDPMessage.getUDPResponse("UserName=" + userName, Constants.SERVER_IP_PORT_AMERICA,Constants.SERVER_IP_PORT_ASIA);
        String check_europe = sendReceiveUDPMessage.getUDPResponse("UserName=" + userName, Constants.SERVER_IP_PORT_EUROPE,Constants.SERVER_IP_PORT_ASIA);

        return !check_american.equalsIgnoreCase("User not found") || !check_europe.equalsIgnoreCase("User not found");
    }

    private void addDummyData() throws Exception {
        createPlayerAccount(new Player("Alex", "Alex", 21, "Alex", "Alex", String.valueOf(Constants.SERVER_IP_PORT_AMERICA), false));
        createPlayerAccount(new Player("Test", "Test", 21, "american", "qwqwqw", String.valueOf(Constants.SERVER_IP_PORT_AMERICA), true));
    }


}
