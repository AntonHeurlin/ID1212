package server.Integration;

import com.mysql.cj.protocol.Resultset;
import common.FileDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;


//import Common.FileDTO;
//import Common.UserDTO;


public class DBAccess {
    private Connection connection;
    private String URL = "jdbc:mysql://localhost:3306/mysampledatabase?autoReconnect=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String userID = "root";
    private String password = "Diablo3sucks";

    private PreparedStatement createAccountStmt;
    private PreparedStatement findAccountStmt;
    private PreparedStatement findAllAccountsStmt;
    private PreparedStatement checkFileStmt;
    private PreparedStatement storeFileStmt;
    private PreparedStatement updateFileStmt;
    private PreparedStatement findAllFilesStmt;
    private PreparedStatement deleteFileStmt;
    private PreparedStatement createUserStmt;
    private PreparedStatement findUserStmt;
    private PreparedStatement loginUserStmt;
    private PreparedStatement uploadFileStmt;
    private PreparedStatement findFileStmt;

    /**
     * @return the instance of this class.
     */
    public  DBAccess() {
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the database with the information in the instance variables and
     * sets up a commit configuration.
     *
     * @throws Exception if failed to connect to the database.
     */
    private void connect() throws Exception {
        Class.forName(driver);
        this.connection = DriverManager.getConnection(URL, userID, password);

        //this.connection.setAutoCommit(false);
    }

    public boolean createUser(String uname, String pword) throws SQLException {
        try {
            createUserStmt = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            createUserStmt.setString(1, uname);
            createUserStmt.setString(2, pword);
            int rows = createUserStmt.executeUpdate();

            if (rows != 1)
                return false;

        }catch(SQLException e) {	System.out.println(e.getMessage());	}

        return true;
    }

    public boolean userExist(String uname) throws SQLException {
        findUserStmt = connection.prepareStatement("SELECT * from users WHERE username = ?");
        findUserStmt.setString(1, uname);
        ResultSet result = findUserStmt.executeQuery();
        //System.out.println("Printing Result [userExist]: "+result.getString("username"));
        if(result.next())
            return true;
        return false;
    }

    public boolean loginUser(String uname, String pword) throws SQLException {
        loginUserStmt = connection.prepareStatement("SELECT * from users WHERE username = ? AND password = ?");
        loginUserStmt.setString(1, uname);
        loginUserStmt.setString(2, pword);
        ResultSet result = loginUserStmt.executeQuery();
        if(result.next()) {
            String username = result.getString("USERNAME");
            String password = result.getString("PASSWORD");
            System.out.println("DBACCESS login: " +username +uname);
            if(username.equals(uname) && password.equals(pword)) {
                System.out.println("Login success in DBACESS");
                return true;
            }
        }
        return false;
    }

    public String fileExist(String filename) throws SQLException {
        findFileStmt = connection.prepareStatement("SELECT * from files WHERE filename = ?");
        findFileStmt.setString(1, filename);
        ResultSet result = findFileStmt.executeQuery();
        String resultString = "null";
        if(result.next()){
            resultString = result.getString("filename") +"#" +result.getString("fileowner") +"#" +result.getString("size");
            return resultString;
        }
        return resultString;
    }

    public ArrayList<FileDTO> findAllFiles() throws SQLException {
        ArrayList<FileDTO> allFiles = new ArrayList<FileDTO>();
        findAllFilesStmt = connection.prepareStatement("SELECT * from files");
        ResultSet result = findAllFilesStmt.executeQuery();
        while(result.next()){
            allFiles.add(new FileDTO(result.getString("filename"), result.getString("fileowner"), result.getString("size")));
        }
        return allFiles;
    }

    public boolean storeFile(String filename, String fileowner, String size) throws SQLException {

        storeFileStmt = connection.prepareStatement("INSERT INTO files (filename, fileowner, size) VALUES (?, ?, ?)");
        try {
            storeFileStmt.setString(1, filename);
            storeFileStmt.setString(2, fileowner);
            storeFileStmt.setString(3, size);

            int rows = storeFileStmt.executeUpdate();
            System.out.println(rows);
            if (rows != 1) {
                System.out.println("storeFile "+rows);
                return false;
            }
        } catch (SQLException sqle) {

            sqle.printStackTrace();
        }
        return true;

    }





}
