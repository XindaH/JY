package edu.nu.xinda.pages;

import edu.nu.xinda.core.DatabaseManager;
import edu.nu.xinda.core.MainLoop;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Author: Enex Tapper
 * Date: 2018/11/12
 * Project: project3
 * Package: edu.nu.xinda.pages
 */
public class MainMenu implements Page {
    private static MainMenu instance;
    private Connection conn;
    private Map<String, MainLoop.Position> map;

    private MainMenu() {
        conn = DatabaseManager.getInstance().getConn();
        map = new HashMap<>();
        map.put("1", MainLoop.Position.TRANSCRIPT);
        map.put("2", MainLoop.Position.ENROLL);
        map.put("3", MainLoop.Position.WITHDRAW);
        map.put("4", MainLoop.Position.PERSONAL_DETAILS);
        map.put("5", MainLoop.Position.STARTED);
        map.put("6", MainLoop.Position.EXIT);

    }

    public static MainMenu getInstance() {
        if (instance == null) {
            instance = new MainMenu();
        }
        return instance;
    }

    private String getQuarter(int month) {
        String q = null;
        if (month >= 10 && month <= 12) q = "Q1";
        else if (month >= 1 && month <= 3) q = "Q2";
        else if (month >= 4 && month <= 6) q = "Q3";
        else if (month >= 7 && month <= 9) q = "Q4";
        return q;
    }

    @Override
    public void onEnter() {
        // use Login.getInstance().getCurrentStudentId() and query student info
        int id = LogIn.getInstance().getCurrentStudentId();
        String name = LogIn.getInstance().getCurrentStudentName();
        System.out.print("Hi, " + name + "!" + "\n\n");
        System.out.print("Main Menu\n\n");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        String quarter = getQuarter(month);
        String sql = "select unitofstudy.UoSCode,UoSname from transcript,unitofstudy where Semester='" + quarter + "'" +
                " and year=" + year + " and transcript.StudId=" + id + " and unitofstudy.uoscode=transcript.uoscode;";
        try {
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                System.out.print(rs.getString(1) + "\t\t");
                System.out.print(rs.getString(2));
                System.out.println();
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printPageInfo() {
        System.out.print("\nInput the index of menu item to continue: \n");
        System.out.print("1 Transcript\n");
        System.out.print("2 Enroll\n");
        System.out.print("3 Withdraw\n");
        System.out.print("4 Personal Details\n");
        System.out.print("5 Logout\n");
        System.out.print("6 Exit\n");
    }

    @Override
    public MainLoop.Position execCommand(String command) throws IOException {
        MainLoop.Position position = null;
        // some bad cases
        if (!map.containsKey(command)) {
            throw new IOException();
        }
        // exit case
        return map.get(command);
    }
}