package com.embold.ui.utils;

import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Driver {

	private RemoteWebDriver browser;

	public RemoteWebDriver getBrowser(BrowserType type, String nodeURL) {
		try {
			DesiredCapabilities capability = null;
			switch (type) {
			case CHROME:
				capability = DesiredCapabilities.chrome();
				capability.setBrowserName("chrome");
				break;
			case FIREFOX:
				capability = DesiredCapabilities.firefox();
				capability.setBrowserName("firefox");
				break;
			case SAFARI:
				capability = DesiredCapabilities.safari();
				capability.setBrowserName("safari");
			default:
				break;
			}
			capability.setPlatform(Platform.LINUX);
			browser = new RemoteWebDriver(new URL(nodeURL), capability);
			browser.manage().window().maximize();
		} catch (Exception e) {
			return null;
		}
		return browser;
	}

	public void closeBrowser() {
		browser.close();
	}
}
