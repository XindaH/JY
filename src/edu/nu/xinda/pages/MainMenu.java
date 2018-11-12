package edu.nu.xinda.pages;

import edu.nu.xinda.core.MainLoop;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Author: Enex Tapper
 * Date: 2018/11/12
 * Project: project3
 * Package: edu.nu.xinda.pages
 */
public class MainMenu implements Page {
	private static MainMenu instance;

	private MainMenu() {}

	public static Page getInstance() {
		if (instance == null) {
			instance = new MainMenu();
		}
		return instance;
	}

	@Override
	public void showInfo() {
		System.out.print("Hi, <USER_NAME>!\n\n\n");
		System.out.print("Main Menu\n\n");
		System.out.print("1 Transcript\n");
		System.out.print("2 Enroll\n");
		System.out.print("3 Withdraw\n");
		System.out.print("4 Personal Details\n");
		System.out.print("\nInput the index of menu item to continue: ");
	}

	@Override
	public MainLoop.Position execCommand(String command) throws IOException {
		// some bad cases
		if (command.equals("5")) {
			throw new IOException();
		}
		// then good cases
		System.out.print("You selected " + command + ".\n");
		return MainLoop.Position.EXIT;
	}
}