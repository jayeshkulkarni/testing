package com.acellere.explorer.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class GammaLogout {
	// private static Logger Log = Logger.getLogger(Log.class.getName());
	public static WebDriver driver;

	@Test()
	public static void logout() throws InterruptedException {
		Thread.sleep(3000);
		driver = GammaLogin.driver;
		driver.findElement(By.xpath(".//*[@id='user_management']")).click();

		Thread.sleep(3000);
		driver.findElement(By.xpath(".//*[@id='log_out']")).click();
		Reporter.log("Logout Success.....!!!", true);
		Thread.sleep(3000);
	}

	@AfterClass()
	public static void close_browser() {

		Reporter.log("Closing the Browser .......!!!", true);
		driver.quit();

	}
}
