package edu.nu.xinda.pages;

import edu.nu.xinda.core.MainLoop;

import java.io.IOException;

public interface Page {
	void showInfo();
	MainLoop.Position execCommand(String command) throws IOException;
}
