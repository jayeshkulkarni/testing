package com.acellere.explorer.subsystemoverviewpanel;

import java.util.List;

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

//import junit.framework.Assert;

public class SubSystemOverviewPanelNew {
	// private static Logger Log = Logger.getLogger(Log.class.getName());
	public static int i = 0;
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static WebElement we;
	public static String panelTitleActual = "Subsystem Overview";
	public static String panelTitleExpect;
	public static String[] myStringArray = new String[] { "Subsystem Overview", "Overall Rating",
			"Hotspot Distribution", "Hotspots", "Heatmap", "Largest 30 Components", "Duplication Distribution",
			"Duplication Percentage" };

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

		we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.project_list:nth-child(1)")));
		we.click();
		System.out.println("Page Title is :- " + driver.getTitle());

		we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.project_list:nth-child(1)")));
		we.click();

		List<WebElement> we1 = wdWait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".dashboard_plugin_name")));
		// .dashboard_plugin_name
		// div.content_div li.plugin_details_wrapper
		System.out.println("Comparing the Elements");
		for (WebElement we3 : we1) {
			System.out.println("Actual Value :- " + we3.getText());
			System.out.println("Expected Value :- " + myStringArray[i]);
			// Assert.assertEquals(we3.getText(),myStringArray[i]);
			i++;
		}

		/*
		 * Log.info("Page Title is :- "+driver.getTitle()); panelTitleExpect =
		 * driver.findElement(By.cssSelector(".panel_title")).getText(); //
		 * Thread.sleep(3000);
		 * 
		 * Log.info("Following Panels are present :- "); Log.info(""); List<WebElement>
		 * list_web_ele2 = driver.findElements(By.cssSelector("div.issue_name")); int
		 * i=0;
		 * 
		 * for(WebElement we2:list_web_ele2) { Log.info(we2.getText());
		 * Assert.assertEquals(we2.getText(),myStringArray[i]); i++; }
		 * 
		 * 
		 * we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(
		 * "li.plugin_details_wrapper:nth-child(1)"))); we.click();
		 * Log.info("Page Title is :- "+GammaLogin.driver.getTitle());
		 * 
		 * //we =
		 * wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(
		 * ".plugin_details_wrapper:nth-child(1)"))); // we.click(); panelTitleExpect =
		 * driver.findElement(By.cssSelector(".panel_title")).getText();
		 * Assert.assertEquals(panelTitleExpect, panelTitleActual);
		 * 
		 */
		Reporter.log("------------------------------END of Test------------------------------");

	}

	@AfterClass
	public static void navigateHomePage() {
		HomePage.navigateBack();

	}
}
