package edu.nu.xinda.pages;

import edu.nu.xinda.core.MainLoop;

import java.io.IOException;

public interface Page {
	void onEnter();
	void printPageInfo();
	MainLoop.Position execCommand(String command) throws IOException;
}
