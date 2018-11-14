package edu.nu.xinda.pages;

import edu.nu.xinda.core.MainLoop;

import edu.nu.xinda.core.DatabaseManager;

import java.io.IOException;
import java.sql.*;

public class LogIn implements Page{
    private static LogIn instance;
    private int currentStudentId;
    private String currentStudentName;
    private DatabaseManager dm= DatabaseManager.getInstance();
    private Connection conn=dm.getConn();

    private LogIn(){}
    public static LogIn getInstance(){
        if(instance == null){
            instance = new LogIn();
        }
        return instance;
    }

    public int getCurrentStudentId() {
        return currentStudentId;
    }

    public String getCurrentStudentName() {
        return currentStudentName;
    }

    @Override
    public void onEnter() {}

    @Override
    public void printPageInfo() {
        System.out.println("please input your ID and password to log in, separate by space");
    }

    @Override
    public MainLoop.Position execCommand(String command) {
        String[] input=command.split(" ");
        Statement stat= null;
        ResultSet rs=null;
        try {
            stat = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            String sql="select * from student where Id="+input[0]+" and Password='"+input[1]+"'";
            rs=stat.executeQuery(sql);
            if(rs.first()){
                currentStudentId = rs.getInt(1);
                currentStudentName = rs.getString(2);
                rs.close();
                return MainLoop.Position.MAIN_MENU;
            }else{
                rs.close();
                System.out.println("Sorry, this does not match our records. Check the spelling and try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return MainLoop.Position.STARTED;
    }
}
