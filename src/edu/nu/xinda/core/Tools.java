package edu.nu.xinda.core;

import java.util.*;


public class Tools {
    public static int getYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    public static int getMonth(){
        return Calendar.getInstance().get(Calendar.MONTH);
    }
    public static String getQuarter(int month){
        String q = null;
        if (month >= 10 && month <= 12) q = "Q1";
        else if (month >= 1 && month <= 3) q = "Q2";
        else if (month >= 4 && month <= 6) q = "Q3";
        else if (month >= 7 && month <= 9) q = "Q4";
        return q;
    }
    public static String getNextQuarter(String quarter){
        Map<String,String> adjacent=new HashMap<>();
        adjacent.put("Q1","Q2");
        adjacent.put("Q2","Q3");
        adjacent.put("Q3","Q4");
        adjacent.put("Q4","Q1");
        return adjacent.get(quarter);
    }
}
