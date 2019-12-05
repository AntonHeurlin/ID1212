package common;

import com.mysql.cj.protocol.Resultset;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface CatalogClient extends Remote {

    void sendMsg(String serverOutput)throws RemoteException;
    void sendResult(ArrayList<FileDTO> allFiles) throws RemoteException, SQLException;
}
