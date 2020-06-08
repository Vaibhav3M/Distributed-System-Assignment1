# Distributed-System-Assignment1

## Distributed Player Status System (DPSS) using Java RMI

Submitted By : Vaibhav Malhotra - 40079373

## Build and Run

### IntelliJ:

-	Open the project in folder DPSS
-	Run: AmericaGameServer, EuropeGameServer, AsiaGameServer.
-	Run: PlayerClient (to launch a player window)
-	Run:  AdminClient (to launch a Admin window)
-	To run multiple clients change the configuration to “Allow parallel run”.




### From Command Line

- Move to **DPSS** directoy: `cd DPSS`.

- Create a new folder named **dist** in the current folder : `mkdir dist`.

- Compile the code (outputting into dist folder): `javac -d dist src/**/*.java`.

- Move to **dist** folder: `cd dist`.

- Run all 3 servers using following commands (will have to open different terminals):

```
java -cp . GameServers.AmericaServer.AmericaGameServer
java -cp . GameServers.EuropeServer.EuropeGameServer
java -cp . GameServers.AsiaServer.AsiaGameServer
```

- Run the clients (will have to open in different terminals):

```
java -cp . Client.PlayerClient
java -cp . Client.AdminClient
