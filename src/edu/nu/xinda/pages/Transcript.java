package edu.nu.xinda.pages;

import edu.nu.xinda.core.DatabaseManager;
import edu.nu.xinda.core.MainLoop;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Transcript implements Page {
    private static Transcript instance;
    private Connection conn;
    private Map<String, MainLoop.Position> map;
    private int id;

    private Transcript() {
        conn = DatabaseManager.getInstance().getConn();
        map = new HashMap<>();
        map.put("detail", null);
        map.put("menu", MainLoop.Position.MAIN_MENU);
        id = LogIn.getInstance().getCurrentStudentId();
    }

    ;

    public static Transcript getInstance() {
        if (instance == null) {
            instance = new Transcript();
        }
        return instance;
    }

    @Override
    public void onEnter() {
        String sql = "Select transcript.UoSCode, UoSName,Grade from transcript,unitofstudy" +
                " where transcript.UoSCode=unitofstudy.UoSCode and transcript.StudId=" + id +
                " order by transcript.Year,transcript.Semester,transcript.UoSCode;";

        try {
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            System.out.println("Course Number\tGrade\tTitle\t");
            while (rs.next()) {
                System.out.print(rs.getString(1) + "\t\t");
                String g = rs.getString(3);
                System.out.print(g + (g == null ? "\t" : "\t\t"));
                System.out.print(rs.getString(2) + "\t");
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
        System.out.println("detail <course number>  get the detail of course");
        System.out.println("menu  return Main Menu");
    }

    @Override
    public MainLoop.Position execCommand(String command) throws IOException {
        // some bad cases
        String[] s = command.split(" ");
        if (!map.containsKey(s[0])) {
            throw new IOException();
        }
        // exit case
        if (s[0].equals("detail")) {
            String sql = "SELECT distinct uf.UoSCode,us.UoSname,uf.Year,uf.Semester,uf.Enrollment,uf.MaxEnrollment,f.Name,t.Grade" +
                    " FROM uosoffering as uf, unitofstudy as us,faculty as f, transcript as t" +
                    " where uf.UoSCode=us.UoSCode and us.UoSCode=t.UoSCode and uf.InstructorId=f.id and t.Year=uf.Year and t.Semester=uf.Semester and t.StudId=? and uf.UoSCode=?";

            PreparedStatement stat;
            ResultSet rs;
            try {
                stat = conn.prepareStatement(sql);
                stat.setInt(1,id);
                stat.setString(2,s[1]);
                rs = stat.executeQuery();
                while (rs.next()) {
                    System.out.println("Course Number:\t\t" + rs.getString(1));
                    System.out.println("Course Name:\t\t" + rs.getString(2));
                    System.out.println("Year:\t\t\t\t" + rs.getString(3));
                    System.out.println("Semester:\t\t\t" + rs.getString(4));
                    System.out.println("Enrollment:\t\t\t" + rs.getString(5));
                    System.out.println("Maximum Enrollment:\t" + rs.getString(6));
                    System.out.println("Lecturer:\t\t\t" + rs.getString(7));
                    System.out.println("Grade:\t\t\t\t" + rs.getString(8));
                    System.out.println();
                }
                rs.close();
                stat.close();
                return MainLoop.Position.TRANSCRIPT;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new IOException();
            }
        }
        if (s.length != 1) {
            throw new IOException();
        }
        return map.get(s[0]);
    }
}

