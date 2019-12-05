package client;

import common.CatalogClient;
import common.CatalogServer;
//import server.Model.ClientHandler;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.Scanner;

public class View {

    static String filePath = "C:\\Users\\Anton\\Desktop\\RMI\\";
    static Scanner scanner = new Scanner(System.in);



    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(null);
            CatalogServer server = (CatalogServer) registry.lookup("CATALOG_SERVER");

            run(server);

        } catch (Exception e) {
            System.err.println("Could not run Catalogue server " + e.toString());
            e.printStackTrace();
        }
    }

    //@Override
    public static void run(CatalogServer server) throws RemoteException, SQLException {
        String input;
        CatalogClient alertHandler = new ResponseHandler();
        String currentClient = "null";
        System.out.println("Welcome to this Catalogue Service. Write 'Login <username> <password>' to Login or 'Register <username> <password>' to register a new account");
        while(true) {
            input = scanner.nextLine();
            String[] clientInput = inputHandler(input);
            input = clientInput[0].toUpperCase();
            switch(input) {
                case "REGISTER":{
                    server.register(alertHandler, clientInput[1], clientInput[2]);

                    break;
                }
                case "LOGIN":{
                    server.login(alertHandler, clientInput[1], clientInput[2]);
                    currentClient = clientInput[1];
                    break;
                }
                case "LOGOUT":{
                    server.logout(alertHandler, currentClient);
                    currentClient = "null";
                    break;
                }
                case "TEST":{
                    server.printMsg();
                    break;
                }
                case "UPLOAD":{
                    String currentFilePath = filePath +clientInput[1];
                    System.out.println(currentFilePath);
                    File file = new File(currentFilePath);

                    server.upload(alertHandler, currentClient, file.getName(), currentClient, Long.toString(file.length()));

                    break;
                }
                case "DOWNLOAD":{
                    server.download(alertHandler, clientInput[1], currentClient);
                    break;
                }
                case "LS":{
                    server.ls(alertHandler, currentClient);
                    break;
                }
                case "HELP": {
                    System.out.println("[HELP] COMMANDS: \n[LS] lists all files in the Catalog\n[UPLOAD] used to upload a file, example 'upload test.txt'\n[DOWNLOAD] used to download a specific file, example 'download test.txt'\n[LOGOUT] logout from the Catalog");
                }


            }
        }
    }

    private static String[] inputHandler(String str) {
        String[] input = str.split(" ");
        return input;
    }
}
