package client;

import common.CatalogClient;
import common.FileDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResponseHandler extends UnicastRemoteObject implements CatalogClient {
    public ResponseHandler() throws RemoteException {
    }
    @Override
    public void sendResult(ArrayList<FileDTO> result) throws SQLException {
        System.out.println("[LS] ALL FILES PRINTED BELOW");
        if (result.isEmpty()) {
            System.out.println("[LS] NO FILES IN DATABASE");
        }else {
            for (int i = 0; i < result.size(); i++) {
                System.out.println("[LS]" + i+1 + ". filename: " + result.get(i).getFilename() + "|| file owner: " + result.get(i).getOwner() + "|| file size: " + result.get(i).getSize());
            }
        }
    }

    @Override
    public void sendMsg(String serverOutput) {
        String[] serverResponse = serverOutput.split("#");

        switch (serverResponse[0]) {
            case "1": {
                if (Boolean.valueOf(serverResponse[1])) {
                    System.out.println("[REGISTRATION SUCCESFUL] Please proceed and login!");
                } else {
                    System.out.println("[REGISTRATION FAILED] Try another username");
                }
                break;
            }
            case "2": {
                if (Boolean.valueOf(serverResponse[1])) {
                    System.out.println("[LOGIN SUCCESFUL] COMMANDS: \n[HELP] show all user commands \n[LS] lists all files in the Catalog\n[UPLOAD] used to upload a file, example 'upload test.txt'\n[DOWNLOAD] used to download a specific file, example 'download test.txt'\n[LOGOUT] logout from the Catalog");
                } else {
                    System.out.println("[LOGIN FAILED] Try another username");
                }
                break;
            }
            case "3": {
                if (Boolean.valueOf(serverResponse[1])) {
                    System.out.println("[LOGOUT SUCCESFUL] Good bye!");
                } else {
                    System.out.println("[LOGOUT FAILED] You are not logged in!");
                }
                break;
            }
            case "4": {
                if (Boolean.valueOf(serverResponse[1])) {
                    System.out.println("[UPLOAD SUCCESFUL] " +serverResponse[2]+ " has been uploaded!");
                } else {
                    System.out.println("[UPLOAD FAILED] Failed to upload the file!");
                }
                break;
            }
            case "5": {
                if (Boolean.valueOf(serverResponse[1])) {
                    System.out.println("[DOWNLOAD SUCCESFULL] File downloaded: " +serverResponse[2] +" || file owner: " +serverResponse[3] +" || file size: " +serverResponse[4] +"bytes");
                    System.out.println("[DOWNLOAD SUCCESFUL] Download successfull!");
                } else {
                    System.out.println("[DOWNLOAD FAILED] Failed to download the file!");
                }
                break;
            }
            case "6": {
                if (Boolean.valueOf(serverResponse[1])) {
                    System.out.println("[LS SUCCESFUL] LS");
                } else {
                    System.out.println("[LS FAILED] Failed to upload the file!");
                }
                break;
            }
            case "7": {
                System.out.println("[ALERT MESSAGE]Someone have downloaded a file you recently uploaded!");
                break;
            }
        }
    }
}

