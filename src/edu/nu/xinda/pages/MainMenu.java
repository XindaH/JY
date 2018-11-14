package edu.nu.xinda.pages;

import edu.nu.xinda.core.DatabaseManager;
import edu.nu.xinda.core.MainLoop;

import java.io.IOException;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * Author: Enex Tapper
 * Date: 2018/11/12
 * Project: project3
 * Package: edu.nu.xinda.pages
 */
public class MainMenu implements Page {
	private static MainMenu instance;
	private DatabaseManager dm= DatabaseManager.getInstance();
	private Connection conn=dm.getConn();
	private int studentID;

	private MainMenu() {
	    LogIn l= LogIn.getInstance();

    }

	public static MainMenu getInstance() {
		if (instance == null) {
			instance = new MainMenu();
		}
		return instance;
	}

	@Override
	public void onEnter() {
		// use Login.getInstance().getCurrentStudentId() and query student info
		System.out.print("Hi, <USER_NAME>!\n\n");
		System.out.print("Main Menu\n\n");
		System.out.print("1 Transcript\n");
		System.out.print("2 Enroll\n");
		System.out.print("3 Withdraw\n");
		System.out.print("4 Personal Details\n");
		System.out.print("5 Exit\n");
	}

	@Override
	public void printPageInfo() {
		System.out.print("\nInput the index of menu item to continue: ");
	}

	@Override
	public MainLoop.Position execCommand(String command) throws IOException {
		// some bad cases
		if (command.equals("6")) {
			throw new IOException();
		}
		// exit case
		if (command.equals("5")) {
			return MainLoop.Position.EXIT;
		}
		// then good cases
		System.out.print("You selected " + command + ".\n\n");
		return MainLoop.Position.MAIN_MENU;
	}
}
