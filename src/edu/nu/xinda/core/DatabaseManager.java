package edu.nu.xinda.core;

import javax.xml.crypto.Data;
import java.sql.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseManager(){this.connect();};
    public static DatabaseManager getInstance(){
        if(instance==null){
            instance = new DatabaseManager();
        }
        return instance;
    }
    private Connection conn;
    private void connect(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String DB_URL = "jdbc:mysql://localhost:3306/project3-nudb";
            String USER = "root";
            String PASS = "hxd13504756572A";
            this.conn = DriverManager.getConnection(DB_URL,USER,PASS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Connection getConn(){
        return conn;
    }
}
