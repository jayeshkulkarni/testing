package com.acellere.explorer.subsystemdashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.acellere.explorer.common.BeforePageLoad;
import com.acellere.explorer.common.HomePage;

public class SubSystemDashboard {
	// private static Logger Log = Logger.getLogger(Log.class.getName());
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static WebElement we;
	public static String panelTitleExpect;
	public static String[] panelTitleActual = new String[] { "Design", "Metrics", "Duplication", "Code Quality" };

	@BeforeClass
	public static void initDriver() throws Exception {
		// GammaLogin.selectBrowser();
		// GammaLogin.login();
		driver = BeforePageLoad.assignDriver();
		wdWait = new WebDriverWait(driver, 35);
	}

	@Test(priority = 1)
	public static void navigateSubSystemOverviewPanel() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Reporter.log("------------------------------Start of Test------------------------------", true);
		we = wdWait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div.project_list:nth-child(1)"))));
		we.click();
		we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.project_list:nth-child(1)")));
		we.click();
		panelTitleExpect = driver.findElement(By.cssSelector(".panel_title")).getText();

		// Assert.assertEquals("Title mismatch !!",panelTitleExpect, panelTitleActual);
		// Thread.sleep(3000);
		we = wdWait.until(
				ExpectedConditions.presenceOfElementLocated(By.cssSelector("li.plugin_details_wrapper:nth-child(1)")));
		we.click();

		// driver.quit();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(priority = 2)
	public static void verifyPanels() {
		for (int i = 1; i <= 4; i++) {
			we = driver.findElement(By
					.cssSelector("div.project_issue_container:nth-child(" + i + ") .project_issue_title .issue_name"));
			System.out.print("Panel Name is :- ");
			System.out.println(we.getText());
		}
	}

	@AfterClass
	public static void navigateHomePage() {
		HomePage.navigateBack();

	}

	/*
	 * @Test(priority = 3) public void navigateToHomePage() {
	 * driver.findElement(By.cssSelector("#project_tab")).click(); }
	 */
}
