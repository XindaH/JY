package edu.nu.xinda.pages;

import edu.nu.xinda.core.DatabaseManager;
import edu.nu.xinda.core.MainLoop;
import edu.nu.xinda.core.Tools;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Enroll implements Page {
    private static Enroll instance;
    private Connection conn;
    private Map<String, MainLoop.Position> map;
    private int id;
    private int year;
    private int nextYear;
    private String quarter;
    private String nextQuarter;
    private Set<String> set;


    private Enroll() {
        conn = DatabaseManager.getInstance().getConn();
        map = new HashMap<>();
        map.put("enroll", null);
        map.put("menu", MainLoop.Position.MAIN_MENU);
        id = LogIn.getInstance().getCurrentStudentId();
        year = Tools.getYear();
        quarter = Tools.getQuarter(Tools.getMonth());
        nextQuarter = Tools.getNextQuarter(quarter);
        nextYear = quarter.equals("Q1") ? year + 1 : year;
        set = new HashSet<>();
    }

    ;

    public static Enroll getInstance() {
        if (instance == null) instance = new Enroll();
        return instance;
    }

    @Override
    public void onEnter() {
        String sql = "Select Semester, Year, unitofstudy.UoSCode,unitofstudy.UoSName from lecture join unitofstudy on lecture.UoSCode=unitofstudy.UoSCode" +
                " where (Semester=? and Year=?) or (Semester=? and Year=?) order by Year,Semester;";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setString(1,quarter);
            stat.setInt(2,year);
            stat.setString(3,nextQuarter);
            stat.setInt(4,nextYear);
            ResultSet rs = stat.executeQuery();
            System.out.println("The courses you can enroll are as following:");
            System.out.println("Semester\tYear\tCourse Number\tTitle");
            while (rs.next()) {
                set.add(rs.getString(3));
                System.out.print(rs.getString(1) + "\t\t\t");
                System.out.print(rs.getString(2) + "\t");
                System.out.print(rs.getString(3) + "\t\t");
                System.out.print(rs.getString(4));
                System.out.println();
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
        System.out.println("enroll <course number>  enroll course");
        System.out.println("menu  return Main Menu");
    }

    @Override
    public MainLoop.Position execCommand(String command) throws IOException {
        String[] s = command.split(" +");
        if (!map.containsKey(s[0])) {
            throw new IOException();
        }
        if (s[0].equals("enroll")) {
            if (s.length == 1 || !set.contains(s[1])) {
                System.out.println("please input right course number");
                return MainLoop.Position.ENROLL;
            }
            try {
                Statement stat=conn.createStatement();
                CallableStatement cs = conn.prepareCall("{call enrollment(?,?,?,?,?,?,?)}");
                cs.setString(1, s[1]);
                cs.setInt(2, id);
                cs.setInt(3, year);
                cs.setString(4, quarter);
                cs.setInt(5, nextYear);
                cs.setString(6, nextQuarter);
                cs.registerOutParameter("con", Types.INTEGER);
                cs.execute();
                int res = cs.getInt(7);
                String sql1="select * from Warning;";
                ResultSet rs = stat.executeQuery(sql1);
                while(rs.next()){
                    if(rs.getInt(1)==1) {
                        System.out.println("Warning: check constraint on Enrollment Number. Enrollment Number goes below 50% of the MaxEnrollment!");
                        break;
                    }
                }
                switch (res) {
                    case 4:
                        System.out.println("successful enroll");
                        break;
                    case 1:
                        System.out.println("This course enrollment is full");
                        break;
                    case 2:
                        String sql = "select PrereqUoSCode from requires where UoSCode='" + s[1] + "'";
                        ResultSet rs1 = stat.executeQuery(sql);
                        System.out.println("you need to satisfy all of prerequisites:");
                        while (rs1.next()) {
                            System.out.println(rs1.getString(1));
                        }
                        rs1.close();
                        stat.close();
                        break;
                    case 3:
                        System.out.println("you have enrolled this course before!");
                        break;
                }
                rs.close();
                cs.close();
                stat.close();
            }catch (Exception ex) {
                ex.printStackTrace();
                throw new IOException();
            }
            return MainLoop.Position.ENROLL;
        }
        if (s.length != 1) {
            throw new IOException();
        }
        return map.get(s[0]);
    }
}
