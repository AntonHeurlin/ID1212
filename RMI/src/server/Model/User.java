package server.Model;

import common.CatalogClient;
import common.FileDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User extends UnicastRemoteObject implements CatalogClient {

    private final CatalogClient client;
    private String username;
    private String password;

    public User(CatalogClient remoteClientObject, String uname) throws RemoteException {
        this.client = remoteClientObject;
        this.username = uname;
        this.password = password;
    }

    public String getUsername(){ return this.username;}

    public String getPassword() { return this.password; }

    @Override
    public void sendMsg(String msg) throws RemoteException {
        client.sendMsg(msg);
    }
    public void sendResult(ArrayList<FileDTO> result) throws RemoteException, SQLException {
        client.sendResult(result);
    }
}
