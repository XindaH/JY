package edu.nu.xinda.pages;

import edu.nu.xinda.core.MainLoop;

import edu.nu.xinda.core.DatabaseManager;
import sun.security.util.Password;

import java.io.IOException;
import java.sql.*;

public class LogIn implements Page {
    private static LogIn instance;
    private int currentStudentId;
    private String currentStudentName;
    private Connection conn;

    private LogIn() {
        conn = DatabaseManager.getInstance().getConn();
    }

    public static LogIn getInstance() {
        if (instance == null) {
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
    public void onEnter() {
        try {
            Statement stat = conn.createStatement();
            String createTable1 = "drop table if exists Warning;";
            String createTable2 = "create table Warning (signal1 Integer);";
            String insert="insert into Warning values(0)";
            stat.executeUpdate(createTable1);
            stat.executeUpdate(createTable2);
            stat.executeUpdate(insert);
        }catch (Exception ex) {
            ex.printStackTrace();
    }}

    @Override
    public void printPageInfo() {
        System.out.println("please input your ID and password to log in, separate by space.");
        System.out.print("OR type 'exit' to exit system\n>> ");
    }

    @Override
    public MainLoop.Position execCommand(String command) throws IOException {
        if (command.equals("exit")) return MainLoop.Position.EXIT;
        String[] input = command.split(" +");
        if (input.length != 2 || input[0].length() < 1 || input[1].length() < 1) {
            return MainLoop.Position.STARTED;
        }
        PreparedStatement stat;
        ResultSet rs;
        try {
            String sql = "select * from student where Id=? and Password=?";
            stat = conn.prepareStatement(sql);
            stat.setInt(1, Integer.valueOf(input[0]));
            stat.setString(2, input[1]);
            rs = stat.executeQuery();
            if (rs.first()) {
                currentStudentId = rs.getInt(1);
                currentStudentName = rs.getString(2);
                rs.close();
                stat.close();
                return MainLoop.Position.MAIN_MENU;
            } else {
                rs.close();
                stat.close();
                System.out.println("Sorry, this does not match our records. Check the spelling and try again.");
            }
        } catch (Exception ex) {
            throw new IOException();
        }
        return MainLoop.Position.STARTED;
    }
}
