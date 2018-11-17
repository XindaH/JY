package edu.nu.xinda.pages;

import edu.nu.xinda.core.DatabaseManager;
import edu.nu.xinda.core.MainLoop;

import java.io.*;
import java.sql.*;
import java.util.*;

public class PersonalDetail implements Page {
    private static PersonalDetail instance;
    private Connection conn;
    private Map<String, MainLoop.Position> map;
    private int id;

    private PersonalDetail() {
        conn = DatabaseManager.getInstance().getConn();
        map = new HashMap<>();
        map.put("changep", null);
        map.put("changea", null);
        map.put("menu", MainLoop.Position.MAIN_MENU);
        map.put("exit", MainLoop.Position.EXIT);
        id = LogIn.getInstance().getCurrentStudentId();
    }

    ;

    public static PersonalDetail getInstance() {
        if (instance == null) instance = new PersonalDetail();
        return instance;
    }

    @Override
    public void onEnter() {
        String sql = "select Id,Name,Address from student where id=" + id;
        Statement stat;
        System.out.println("Personal Detail Page\n");
        try {
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                System.out.println("ID:" + rs.getString(1));
                System.out.println("Name:" + rs.getString(2));
                System.out.println("Address:" + rs.getString(3));
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printPageInfo() {
        System.out.println("\nInput the index of menu item to continue: ");
        System.out.println("changep <old password> <new password>  change password");
        System.out.println("changea <new address> change address");
        System.out.println("menu  return Main Menu");
        System.out.println("exit  Exit system");
    }

    @Override
    public MainLoop.Position execCommand(String command) throws IOException {
        String[] s = command.split(" +");
        if (s.length < 1 || !map.containsKey(s[0])) {
            throw new IOException();
        }
        if (s[0].equals("changep")) {
            if (s.length != 3) {
                throw new IOException();
            }
            String oldpass = "select Password from student where id=" + id;
            Statement stat;
            try {
                stat = conn.createStatement();
                ResultSet rs = stat.executeQuery(oldpass);
                while (rs.next()) {
                    oldpass = rs.getString(1);
                }
                rs.close();
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (!s[1].equals(oldpass)) {
                System.out.println("old password is wrong!");
                return MainLoop.Position.PERSONAL_DETAILS;
            }
            String updatePass = "update student set Password='" + s[2] + "' where Id=" + id;
            try {
                PreparedStatement pst = conn.prepareStatement(updatePass);
                pst.executeUpdate();
                System.out.println("Set Password successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return MainLoop.Position.PERSONAL_DETAILS;
        }
        if (s[0].equals("changea")) {
            if (s.length != 2) {
                throw new IOException();
            }
            String updateAddress = "update student set Address='" + s[1] + "' where Id=" + id;
            try {
                PreparedStatement pst = conn.prepareStatement(updateAddress);
                pst.executeUpdate();
                System.out.println("Set Address successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return MainLoop.Position.PERSONAL_DETAILS;
        }
        if (s.length != 1) {
            throw new IOException();
        }
        return map.get(s[0]);
    }
}
