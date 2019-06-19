package com.acellere.explorer.common;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class BeforePageLoad {
	public static WebDriver driver;
	public static WebDriverWait wdWait;

	@Test
	public static WebDriver assignDriver() throws InterruptedException, IOException {

		driver = GammaLogin.getWebDriver();
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		wdWait = new WebDriverWait(driver, 35);
		return driver;
	}
}
