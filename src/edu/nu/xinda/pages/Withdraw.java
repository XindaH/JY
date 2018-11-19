package edu.nu.xinda.pages;

import edu.nu.xinda.core.DatabaseManager;
import edu.nu.xinda.core.MainLoop;
import edu.nu.xinda.core.Tools;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Withdraw implements Page {
    private static Withdraw instance;
    private Connection conn;
    private Map<String, MainLoop.Position> map;
    private int id;
    private int year;
    private int nextYear;
    private String quarter;
    private String nextQuarter;
    private Set<String> course;

    private Withdraw() {
        conn = DatabaseManager.getInstance().getConn();
        map = new HashMap<>();
        map.put("withdraw", null);
        map.put("menu", MainLoop.Position.MAIN_MENU);
        map.put("exit", MainLoop.Position.EXIT);
        id = LogIn.getInstance().getCurrentStudentId();
        year = Tools.getYear();
        quarter = Tools.getQuarter(Tools.getMonth());
        nextQuarter = Tools.getNextQuarter(quarter);
        nextYear = quarter.equals("Q1") ? year + 1 : year;
    }

    ;

    public static Withdraw getInstance() {
        if (instance == null) {
            instance = new Withdraw();
        }
        return instance;
    }

    @Override
    public void onEnter() {
        System.out.println("you can drop courses in this page");
    }

    @Override
    public void printPageInfo() {
        course = new HashSet<>();
        String sql = "Select UoSCode from transcript" +
                " where StudId=" + id + " and Grade is null and (Year=" + year + " and Semester='" +
                quarter + "')" + " or (Year=" + nextYear + " and Semester='" + nextQuarter + "')" +
                " order by UoSCode;";

        try {
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            System.out.println("Course Number that you can drop");
            while (rs.next()) {
                course.add(rs.getString(1));
                System.out.println(rs.getString(1));
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("\nInput the index of menu item to continue: ");
        System.out.println("withdraw <course number>  withdraw course");
        System.out.println("menu  return Main Menu");
        System.out.println("exit  Exit system");
    }

    @Override
    public MainLoop.Position execCommand(String command) throws IOException {
        String[] s = command.split(" +");
        if (s.length < 1 || !map.containsKey(s[0])) {
            throw new IOException();
        }
        if (s[0].equals("withdraw")) {
            if (s.length < 2 || !course.contains(s[1])) {
                System.out.println("invalid input");
                return MainLoop.Position.WITHDRAW;
            }
            try {
                Statement stat=conn.createStatement();
                CallableStatement cs = conn.prepareCall("{call withdraw(?,?,?,?,?,?,?)}");
                cs.setString(1, s[1]);
                cs.setInt(2, id);
                cs.setInt(3, year);
                cs.setString(4, quarter);
                cs.setInt(5, nextYear);
                cs.setString(6, nextQuarter);
                cs.registerOutParameter("con", Types.INTEGER);
                cs.execute();
                int res = cs.getInt(7);
                switch (res) {
                    case 0:
                        System.out.println("successful withdraw");
                        break;
                    case 1:
                        System.out.println("cannot withdraw this course");
                        break;
                }
                cs.close();
                String sql1="select * from Warning;";
                ResultSet rs = stat.executeQuery(sql1);
                while(rs.next()){
                    if(rs.getInt(1)==1) {
                        System.out.println("Warning: check constraint on Enrollment Number. Enrollment Number goes below 50% of the MaxEnrollment!");
                    }
                }
                rs.close();
                stat.close();
            }catch (Exception ex) {
                throw new IOException();
            }
            return MainLoop.Position.WITHDRAW;
        }
        if (s.length != 1) {
            throw new IOException();
        }
        return map.get(s[0]);
    }
}
