package common;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface CatalogServer extends Remote {
    public static String SERVER_NAME_IN_REGISTRY = "CATALOG_SERVER";

    void printMsg() throws RemoteException;
    void register(CatalogClient responseHandler, String uname, String pword) throws RemoteException, SQLException;
    void login(CatalogClient responseHandler, String uname, String pword) throws RemoteException;
    void logout(CatalogClient responseHandler, String uname) throws RemoteException;
    void upload(CatalogClient remoteClientObject, String currentClient, String filename, String owner, String filelength) throws RemoteException, SQLException;
    void download(CatalogClient responseHandler, String filename, String currentClient) throws RemoteException, SQLException;
    void ls(CatalogClient responseHandler, String currentClient)throws RemoteException, SQLException;
}
