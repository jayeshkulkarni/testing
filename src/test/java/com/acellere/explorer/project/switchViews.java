package com.acellere.explorer.project;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.acellere.explorer.common.GammaLogin;
import com.acellere.explorer.common.RegisterTenant;

public class switchViews {
	// private static Logger Log = Logger.getLogger(Log.class.getName());
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static List<WebElement> dropdown;
	public static List<WebElement> arr[];
	public static WebElement switchViewDropdown;
	public static String ele1;
	public static String ele2;
	public static WebElement we;

	@BeforeClass
	public void assignDriver() {
		RegisterTenant.selectBrowser();
		try {
			GammaLogin.login();
		} catch (InterruptedException | IOException e) {

		}
		driver = GammaLogin.getWebDriver();
		wdWait = new WebDriverWait(driver, 35);
		Reporter.log("------------------------------Start of Test------------------------------", true);
	}

	@Test(priority = 1)
	public static void switchListView() throws InterruptedException {

		we = wdWait.until(ExpectedConditions.elementToBeClickable(
				By.cssSelector(".option_wrapper .dropdown_container:nth-child(2) .dropdown_arrow")));
		we.click();

		we = wdWait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector(".dropDown_list li:nth-child(2) li:nth-child(1)")));
		we.click();

		we = wdWait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector(".option_button_wrapper div.button_small:nth-child(1)")));
		we.click();

		driver.quit();
	}

}
