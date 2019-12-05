package client;

import common.CatalogClient;
import common.FileDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AlertHandler extends UnicastRemoteObject implements CatalogClient {
    public AlertHandler() throws RemoteException {
    }

    @Override
    public void sendResult(ArrayList<FileDTO> result) throws SQLException {

    }
    public void sendMsg(String msg){
        System.out.println("[ALERT MESSAGE]Someone have downloaded a file you recently uploaded!");
    }
}
