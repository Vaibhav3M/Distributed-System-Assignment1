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
import java.util.logging.Logger;

/**
 * The type Europe game server.
 */
public class EuropeGameServerImpl extends UnicastRemoteObject implements DPSS_GameServerInterface {

    //lock to enable synchronization
    private static Lock lock = new ReentrantLock(true);
    //to store player info
    private static Hashtable<Character, ArrayList<Player>> playersTable = new Hashtable();
    //to log activities in a log file
    private static Logger LOGGER;


    /**
     * Instantiates a new Europe game server.
     *
     * @param logger the logger
     * @throws RemoteException the remote exception
     */
    protected EuropeGameServerImpl(Logger logger) throws RemoteException {
        super();
        LOGGER = logger;
        addDummyData();
    }


    @Override
    public String createPlayerAccount(Player player) throws RemoteException {

        LOGGER.info("Received RMI request - Create Player - " + player.toString());

        //check if username exists
        if (checkUserName(player.getUserName())) {
            LOGGER.info("Username=" + player.getUserName() + " already existed");

            return "Username already exists";
        }

        char playerKey = player.getUserName().charAt(0);

        ArrayList<Player> playerList;

        try {
            // lock while performing operations
            lock.lock();

            if (playersTable.containsKey(playerKey)) {

                playerList = playersTable.get(playerKey);

                for (int i = 0; i < playerList.size(); i++) {
                    Player currPlayer = playerList.get(i);

                    if (currPlayer.getUserName().equalsIgnoreCase(player.getUserName())) {
                        LOGGER.info("Username=" + player.getUserName() + " already existed");

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
            //unlock once operation complete
            lock.unlock();
        }

        LOGGER.info("Player Created successfully - " + player.toString());

        return "Successful";
    }

    @Override
    public String playerSignIn(String Username, String Password, String IPAddress) throws RemoteException {

        LOGGER.info("Received RMI request - SignIn Player - " + "Username=" + Username);

        char playerKey = Username.charAt(0);

        try {
            // lock while performing operations
            lock.lock();
            if (playersTable.containsKey(playerKey)) {

                ArrayList<Player> playerList = playersTable.get(playerKey);

                for (int i = 0; i < playerList.size(); i++) {
                    Player currPlayer = (Player) playerList.get(i);
                    if (currPlayer.getUserName().equalsIgnoreCase(Username) && currPlayer.getPassword().equalsIgnoreCase(Password)) {

                        if (currPlayer.isSignedIn()) {
                            LOGGER.info("Player is already SignedIn - " + "Username=" + Username);
                            return currPlayer.getUserName() + " is already logged in.";
                        }

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

                return "User not found";
            }
        } finally {
            lock.unlock();
        }

        return "User not found";
    }

    @Override
    public String playerSignOut(String Username, String IPAddress) throws RemoteException {

        boolean isFromServerIP = (Integer.parseInt(IPAddress) == Constants.SERVER_IP_EUROPE);

        char playerKey = Username.charAt(0);
        try {
            // lock while performing operations
            lock.lock();
            if (playersTable.containsKey(playerKey)) {

                ArrayList<Player> playerList = playersTable.get(playerKey);

                for (int i = 0; i < playerList.size(); i++) {
                    Player currPlayer = playerList.get(i);
                    if (currPlayer.getUserName().equalsIgnoreCase(Username)) {

                        if (isFromServerIP) {
                            LOGGER.info("Received RMI request - SignOut Player - " + Username);

                            if (!currPlayer.isSignedIn()) {
                                LOGGER.info("Player is not SignedIn - " + "Username=" + Username);
                                return currPlayer.getUserName() + " is not signed in.";
                            }
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
                return "User not found";
            }
        } finally {
            lock.unlock();
        }

        return "User not found";
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

        //Send UDP requests to other servers
        if (checkOtherServers) {
            response_Asia = getPlayerStatusUDP(Constants.SERVER_PORT_ASIA);
            response_America = getPlayerStatusUDP(Constants.SERVER_PORT_AMERICA);
        }

        //append the results
        response = response + onlineCount + " online, " + offlineCount + " offline. " + response_Asia + response_America;
        return response;
    }

    /**
     * getPlayerStatusUDP.
     *
     * @param serverPort - port to which UDP request is sent
     * @return the UDP response
     */
    private String getPlayerStatusUDP(int serverPort) {

        LOGGER.info("Created UDP request - Get player status from port " + serverPort);
        String[] response = {"No response from " + serverPort};

        SendReceiveUDPMessage sendReceiveUDPMessage = new SendReceiveUDPMessage();

        //create a new thread for UDP request
        Thread UDPThread = new Thread(() ->
        {
            try {
                response[0] = sendReceiveUDPMessage.getUDPResponse("playerstatus", serverPort, Constants.SERVER_PORT_EUROPE);

            } catch (Exception e) {
                System.out.println("Exception at getPlayerStatus: " + e.getLocalizedMessage());
            }

        });

        UDPThread.setName("Thread - UDP " + serverPort);
        UDPThread.start();

        try {
            UDPThread.join();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        LOGGER.info("Received UDP response from " + serverPort + " - " + response[0]);
        return response[0];

    }

    /**
     * checkUserName - to check if username exists on other servers using UDP
     *
     * @param userName - username to check
     * @return  username status
     */
    private boolean checkUserName(String userName) {
        SendReceiveUDPMessage sendReceiveUDPMessage = new SendReceiveUDPMessage();

        String check_american = sendReceiveUDPMessage.getUDPResponse("UserName=" + userName, Constants.SERVER_PORT_AMERICA, Constants.SERVER_PORT_EUROPE);
        String check_asia = sendReceiveUDPMessage.getUDPResponse("UserName=" + userName, Constants.SERVER_PORT_ASIA, Constants.SERVER_PORT_EUROPE);

        return !check_american.equalsIgnoreCase("User not found") || !check_asia.equalsIgnoreCase("User not found");
    }

    private void addDummyData() {
        addDummyDataHelper(new Player("Test", "Test", 21, "Test_Europe", "test123", String.valueOf(Constants.SERVER_IP_AMERICA), false));
        addDummyDataHelper(new Player("John", "Human", 25, "John123", "john123", String.valueOf(Constants.SERVER_IP_AMERICA), true));
    }

    private void addDummyDataHelper(Player player){

        char playerKey = player.getUserName().charAt(0);

        ArrayList<Player> playerList;

        try {
            lock.lock();

            if (playersTable.containsKey(playerKey)) {

                playerList = playersTable.get(playerKey);

                playerList.add(player);

            } else {
                playerList = new ArrayList<>();
                playerList.add(player);
                playersTable.put(playerKey, playerList);

            }
        } finally {
            lock.unlock();
        }

    }
}
