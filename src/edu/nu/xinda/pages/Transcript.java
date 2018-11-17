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
        map.put("exit", MainLoop.Position.EXIT);
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
                System.out.print(g + (g == null?"\t":"\t\t"));
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
        System.out.println("exit  Exit system");
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
                    " where uf.UoSCode=us.UoSCode and us.UoSCode=t.UoSCode and uf.InstructorId=f.id and t.Year=uf.Year and t.Semester=uf.Semester and t.StudId=" + id +
                    " and uf.UoSCode='" + s[1]+"'";

            Statement stat;
            try {
                stat = conn.createStatement();
                ResultSet rs = stat.executeQuery(sql);
                while (rs.next()) {
                    System.out.print(rs.getString(1) + "\t");
                    System.out.print(rs.getString(2) + "\t");
                    System.out.print(rs.getString(3) + "\t");
                    System.out.print(rs.getString(4) + "\t");
                    System.out.print(rs.getString(5) + "\t");
                    System.out.print(rs.getString(6) + "\t");
                    System.out.print(rs.getString(7) + "\t");
                    System.out.print(rs.getString(8) + "\t");
                    System.out.println();
                }
                rs.close();
                stat.close();
                return MainLoop.Position.TRANSCRIPT;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map.get(s[0]);
    }
}

