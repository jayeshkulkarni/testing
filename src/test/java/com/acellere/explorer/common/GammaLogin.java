package com.acellere.explorer.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class GammaLogin {
	private static Logger Log = Logger.getLogger(Log.class.getName());
	public static String url = "http://localhost:3000/login.html";
	// public static String url = "http://192.168.2.100:3001/login";
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	private static String pageTitle = "GAMMA - : PROJECT LIST";
	public static String loginMsg;

	public static WebDriver getWebDriver() {
		return driver;
	}

	@Test()
	public static void login() throws InterruptedException, IOException {
		driver = RegisterTenant.getWebDriver();
		// Specify the file location I used . operation here because
		// we have object repository inside ProjectCreate directory only
		File src = new File("\\Gamma_Automation\\Gamma_Explorer_New\\src\\main\\resources\\uiObjects.properties");

		// Create FileInputStream object
		FileInputStream fis = new FileInputStream(src);

		// Create Properties class object to read properties file
		Properties pro = new Properties();

		// Load file so we can use into our script
		pro.load(fis);

		System.out.println("Property class loaded");

		Reporter.log("Executing testcases in Firefox.....!!!", true);
		driver.manage().window().maximize();
		driver.navigate().to(pro.getProperty("gamma.login.url"));

		driver.findElement(By.xpath(pro.getProperty("gamma.login.username.xpath"))).sendKeys("account@acellere.com");
		driver.findElement(By.xpath(pro.getProperty("gamma.login.password.xpath"))).sendKeys("account123");
		driver.findElement(By.cssSelector(pro.getProperty("gamma.login.signin.css"))).click();
		Thread.sleep(3000);
		// loginMsg = driver.findElement(By.cssSelector(".login-panel
		// .error-msg")).getText();
		// Reporter.log(loginMsg,true);
		// System.out.println("mess :- "+loginMsg);
		// Reporter.log("Login Success.....!!!",true);
		// Reporter.log("Page Title is :- " +driver.getTitle(),true);
		if ((driver.getTitle().toUpperCase()) == pageTitle)
			Reporter.log("Page Title matches :- " + driver.getTitle().toUpperCase());
		Thread.sleep(3000);

	}

}
