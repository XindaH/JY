package edu.nu.xinda.core;

import edu.nu.xinda.pages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainLoop {
	public enum Position {
		STARTED,
		MAIN_MENU,
		TRANSCRIPT,
		ENROLL,
		WITHDRAW,
		PERSONAL_DETAILS,
		EXIT
	}

	private BufferedReader reader;
	private Position currentPosition;

	public MainLoop() {
		this.reader = new BufferedReader(new InputStreamReader(System.in));
		this.currentPosition = null;
	}

	public void start() {
		this.loop(Position.STARTED);
	}

	private void loop(Position position) {
		Page currentPage = getCurrentPage(position);

		if (this.currentPosition != position) currentPage.onEnter();
		this.currentPosition = position;

		currentPage.printPageInfo();

		Position newPosition = position;

		try {
			String command = this.reader.readLine();
			newPosition = currentPage.execCommand(command);
		} catch (IOException e) {
			System.out.print("Invalid input, please try again.\n");
		}
		if (newPosition != Position.EXIT) this.loop(newPosition);
	}

	private Page getCurrentPage(Position position) {
		switch (position) {
			case STARTED:
				return LogIn.getInstance();
			case MAIN_MENU:
				return MainMenu.getInstance();
            case TRANSCRIPT:
                return Transcript.getInstance();
            case ENROLL:
                return Enroll.getInstance();
            case WITHDRAW:
                return Withdraw.getInstance();
            case PERSONAL_DETAILS:
                return PersonalDetail.getInstance();

		}
		// TODO should be sign-in page
		return MainMenu.getInstance();
	}
}
