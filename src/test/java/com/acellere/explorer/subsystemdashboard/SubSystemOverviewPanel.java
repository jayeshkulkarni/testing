package com.acellere.explorer.subsystemdashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.acellere.explorer.common.BeforePageLoad;

public class SubSystemOverviewPanel {
	// private static Logger Log = Logger.getLogger(Log.class.getName());
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static WebElement we;
	public static String panelTitleActual = "Subsystem Overview";
	public static String panelTitleExpect;

	@BeforeClass
	public static void initDriver() throws Exception {
		// GammaLogin.selectBrowser();
		// GammaLogin.login();
		driver = BeforePageLoad.assignDriver();
		wdWait = new WebDriverWait(driver, 35);
	}

	@Test(priority = 1)
	public static void navigateSubSystemOverviewPanel() {
		Reporter.log("------------------------------Start of Test------------------------------", true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		we = wdWait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div.project_list:nth-child(1)"))));
		we.click();
		we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.project_list:nth-child(1)")));
		we.click();
		panelTitleExpect = driver.findElement(By.cssSelector(".panel_title")).getText();
		// Thread.sleep(3000);
		we = wdWait.until(
				ExpectedConditions.presenceOfElementLocated(By.cssSelector("li.plugin_details_wrapper:nth-child(1)")));
		we.click();

		// we =
		// wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".plugin_details_wrapper:nth-child(1)")));
		// we.click();

		panelTitleExpect = driver.findElement(By.cssSelector(".panel_title")).getText();
		// Assert.assertEquals(panelTitleExpect, panelTitleActual);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// we =
		// wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#project_tab")));
		// we.click();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Reporter.log("------------------------------END of Test------------------------------");

	}

	/*
	 * @AfterClass public static void navigateHomePage() { homePage.navigateBack();
	 * 
	 * }
	 */
	/*
	 * @Test(priority = 2) public void navigateToHomePage() {
	 * driver.findElement(By.cssSelector("#project_tab")).click(); }
	 */
}
