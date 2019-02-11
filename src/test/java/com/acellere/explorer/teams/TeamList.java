package com.acellere.explorer.teams;

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

public class TeamList {
	// private static Logger Log = Logger.getLogger(Log.class.getName());
	public static String url = "http://localhost:3000/login.html";
	// public static String url = "http://192.168.2.100:3001/login";
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static WebElement we;
	public static int team_size;
	public static String pageTitle = "GAMMA - : Team List";
	public static String actualTitle;

	@BeforeClass
	public static void initDriver() throws Exception {
		driver = BeforePageLoad.assignDriver();
		wdWait = new WebDriverWait(driver, 35);
	}

	@Test(priority = 1)
	public static void searchTeams() throws InterruptedException {
		System.out.println("------------------------------Start of Test------------------------------");
		Reporter.log("Executing " + TeamList.class.getName() + ".....!!!", true);
		Thread.sleep(1000);
		/*
		 * JavascriptExecutor js = (JavascriptExecutor) driver; WebElement element =
		 * driver.findElement(By.cssSelector("#loading_overlay"));
		 * js.executeScript("arguments[0].setAttribute('style', 'z-index:-1')",element);
		 */
		we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#team_tab")));
		we.click();
		actualTitle = driver.getTitle();
		// Assert.assertEquals(pageTitle, actualTitle);
		Thread.sleep(1000);
		team_size = driver.findElements(By.cssSelector("div.team_list_container div.teamList_list")).size();
		Thread.sleep(1000);
		System.out.println("Team size is :- " + team_size);

		for (int i = 1; i <= team_size; i++) {

			driver.findElement(By.cssSelector(".team_list_container .teamList_list:nth-child(" + i + ")"));
			we = driver
					.findElement(By.cssSelector(".team_list_container .teamList_list:nth-child(" + i + ") .team_name"));
			we.getText();
			System.out.println("Team name:- " + we.getText());
			for (int j = 1; j <= 4; j++) {
				we = driver.findElement(By.cssSelector(".team_list_container .teamList_list:nth-child(" + i
						+ ") div.team_details_wrapper div.team_detail:nth-child(" + j + ") div.title_wrapper"));
				we.getText();
				System.out.print(we.getText());
				System.out.print(" :- ");
				we = driver.findElement(By.cssSelector(".team_list_container .teamList_list:nth-child(" + i
						+ ") div.team_details_wrapper div.team_detail:nth-child(" + j + ") div.count_wrapper"));
				we.getText();
				System.out.println(we.getText());
			}

			Thread.sleep(2000);
		}
		driver.findElement(By.cssSelector("#project_tab")).click();
		System.out.println("-------------------------------END of Test-------------------------------");
	}

	@AfterClass
	public static void navigateHomePage() {
		HomePage.navigateBack();

	}
}
