package server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.protocol.Resultset;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import common.CatalogClient;
import common.FileDTO;
import common.MsgIntepreter;
import server.Integration.DBAccess;

import common.CatalogServer;
import server.Model.User;

public class Controller extends UnicastRemoteObject implements CatalogServer {
    private final DBAccess db;
    private ArrayList<String> usersOnline;
    private ArrayList<User> onlineUsers;
    private ArrayList<User> affectedUsers;

    public Controller() throws RemoteException {
        db = new DBAccess();
        usersOnline = new ArrayList<String>();
        affectedUsers = new ArrayList<User>();
    }

    @Override
    public void printMsg() {
        System.out.println("This is an example RMI program");
    }

    @Override
    public void register(CatalogClient responseHandler, String uname, String pword) throws SQLException, RemoteException {
        String returnString;
        if(db.userExist(uname) == false){
            if(db.createUser(uname, pword)){
                returnString = "1#true#"+uname;
                responseHandler.sendMsg(returnString);
            }else {
                returnString = "1#false#";
                responseHandler.sendMsg(returnString);
            }
        }else {
            returnString = "1#false";
            responseHandler.sendMsg(returnString);
        }

    }

    @Override
    public void login(CatalogClient responseHandler, String uname, String pword) {
        String returnString;
        try {
            System.out.println("Login, user: " +uname);
            if(db.loginUser(uname, pword)) {
                if(usersOnline.contains(uname)){
                    returnString = "2#false";
                    responseHandler.sendMsg(returnString);
                }else{
                    usersOnline.add(uname);
                    returnString = "2#true#";
                    responseHandler.sendMsg(returnString);
                }
            }else{
                returnString = "2#false";
                responseHandler.sendMsg(returnString);
            }

        }catch(Exception e) {
            System.out.println("Error helvete");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void logout(CatalogClient responseHandler, String uname) throws RemoteException {
        try {
            removeUser(uname);
            usersOnline.remove(uname);
            responseHandler.sendMsg("3#true");
        }catch (Exception e){
            responseHandler.sendMsg("3#false");
        }
    }

    @Override
    public void upload(CatalogClient remoteClientObject, String currentClient, String filename, String owner, String filelength) throws SQLException, RemoteException {
        if(usersOnline.contains(currentClient)){
            User newUser = new User(remoteClientObject, currentClient);
            affectedUsers.add(newUser);
            boolean status = db.storeFile(filename, owner, filelength);
            if(status){
                remoteClientObject.sendMsg("4#true#"+filename);
            }else{remoteClientObject.sendMsg("4#false");}
        }else{
            remoteClientObject.sendMsg("4#false");
        }

    }

    @Override
    public void download(CatalogClient responseHandler, String filename, String currentClient) throws SQLException, RemoteException {
        if(usersOnline.contains(currentClient)){
            String returnString = db.fileExist(filename);
            if(!returnString.equals("null")) {
                String infoString[] = Intepret(returnString);
                alertAffectedUser(infoString[1]);
                responseHandler.sendMsg("5#true#" + returnString);
            }else{
                responseHandler.sendMsg("5#false");
            }
        }else{
            responseHandler.sendMsg("5#false");
        }
    }

    public String[] Intepret(String msg){
        String[] finalMsg = msg.split("#");
        return finalMsg;
    }

    public void alertAffectedUser(String username) throws RemoteException {
        for(int i = 0; i < affectedUsers.size(); i++){
            //System.out.println(username +" vs. " +affectedUsers.get(i).getUsername());
            if(affectedUsers.get(i).getUsername().equals(username)){
                affectedUsers.get(i).sendMsg("7");
            }
        }
    }

    public void ls(CatalogClient responseHandler, String currentClient) throws SQLException, RemoteException {
        ArrayList<FileDTO> allFiles = db.findAllFiles();
        responseHandler.sendResult(allFiles);
    }

    public void removeUser(String username){
        for(int i = 0; i < affectedUsers.size(); i++){
            System.out.println(username +" vs. " +affectedUsers.get(i).getUsername());
            if(affectedUsers.get(i).getUsername().equals(username)){
                affectedUsers.remove(i);
            }
        }
    }


}
