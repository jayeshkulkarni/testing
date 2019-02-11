package com.acellere.explorer.subsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.acellere.explorer.common.GammaLogin;

//import junit.framework.Assert;

public class SubSystem {
	private static Logger Log = Logger.getLogger(Log.class.getName());
	public static String url = "http://localhost:3000/login.html";
	public static WebDriver driver;
	public static int subsystem_count;
	public static int project_count;
	public static int subProjectCount;
	public static String ss_rating, ss_loc_count;
	List<WebElement> subsystem_details = null;
	public static List<WebElement> elements;
	public static WebDriverWait wdWait;
	public static String panelTitleActual = "Subsystems";
	public static String panelTitleExpect;
	public static String sub_project_names = null;
	public static List<String> unsorted_arraylist = new ArrayList<String>();
	public static List<String> sorted_arraylist = new ArrayList<String>();
	public static WebElement we;
	public static String LOC_count, no_of_Subsystems, project_rating, project_loc_count;

	@BeforeClass
	public static void beforeLogin() throws Exception {
		driver = GammaLogin.getWebDriver();
		wdWait = new WebDriverWait(driver, 35);
		Reporter.log("------------------------------Start of Test------------------------------", true);
	}

	@Test(priority = 1)
	public static void count_project() throws InterruptedException {

		elements = wdWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.project_list")));
		project_count = elements.size();
		Log.info("Total Number of Projects :-" + project_count);

	}

	@Test(priority = 2)
	public void Subsystem_Count() throws Exception {
		Thread.sleep(5000);
		for (int i = 1; i <= project_count; i++) {
			driver.findElement(By.cssSelector("div.project_list:nth-child(" + i + ")")).click();
			panelTitleExpect = driver.findElement(By.cssSelector(".panel_title")).getText();
			// Assert.assertEquals(panelTitleExpect, panelTitleActual);
			Thread.sleep(2000);
			subsystem_count = driver.findElements(By.cssSelector(".subsystem_list_container .project_list")).size();
			Log.info(subsystem_count);
			for (int j = 1; j <= subsystem_count; j++) {
				List<WebElement> list_of_subsystem = driver
						.findElements(By.cssSelector("div.project_list:nth-child(" + j + ")"));
				for (WebElement we1 : list_of_subsystem) {
					Log.info("Subsystem Name :-" + we1.getAttribute("data-name"));
					ss_rating = driver
							.findElement(
									By.cssSelector("div.project_list:nth-child(" + j + ") div.project_rating div.h2"))
							.getText();
					ss_loc_count = driver
							.findElement(By.cssSelector("div.project_list:nth-child(" + j + ") div.loc_count"))
							.getText();
					ss_loc_count = ss_loc_count.substring(6, 11);
					Log.info("Rating :- " + ss_rating);
					Log.info("LOC Count :- " + ss_loc_count);
				}
			}
			driver.navigate().back();
			Thread.sleep(2000);
		}
		driver.navigate().back();
		Thread.sleep(2000);

	}

	/// New functions

	private static List<String> get_array(String sortBy) {

		List<String> arrayList = new ArrayList<String>();

		for (int i = 1; i <= subsystem_count; i++) {
			List<WebElement> project_details = driver
					.findElements(By.cssSelector("div.project_list:nth-child(" + i + ")"));

			for (WebElement we1 : project_details) {
				if ("name".equalsIgnoreCase(sortBy)) {
					sub_project_names = we1.getAttribute("data-name");
					arrayList.add(sub_project_names);
				}

				else if ("size".equalsIgnoreCase(sortBy)) {
					project_loc_count = driver
							.findElement(By.cssSelector("div.project_list:nth-child(" + i + ") div.loc_count"))
							.getText();
					project_loc_count = project_loc_count.substring(6, 12);
					// Log.info(project_loc_count);
					arrayList.add(project_loc_count);
				}

				else if ("rating".equalsIgnoreCase(sortBy)) {
					project_rating = driver
							.findElement(
									By.cssSelector("div.project_list:nth-child(" + i + ") div.project_rating div.h2"))
							.getText();
					arrayList.add(project_rating);
				}

			}

		}
		Log.info(arrayList);
		return arrayList;
	}

	@Test(priority = 3)
	public static void sort_by_name() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.project_list:nth-child(2)")))
				.click();
		subsystem_count = driver.findElements(By.cssSelector(".subsystem_list_container .project_list")).size();
		Log.info("subsystem_count");
		Log.info("Sorting by Name :- ");
		Log.info("Unsorted Arraylist");
		unsorted_arraylist = get_array("name");
		we = wdWait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector("div.dropdown_container:nth-child(3) div.dropdown_arrow")));
		we.click();

		we = wdWait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector("div.dropDown_list li:nth-child(1) li div")));
		we.click();

		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button_small:nth-child(1)")));
		we.click();
		Log.info("Sorted Arraylist");
		sorted_arraylist = get_array("name");
		Collections.sort(unsorted_arraylist);
	}

	@Test(priority = 4)
	public static void sort_by_rating() {
		Log.info("Sorting by Rating :- ");
		Log.info("Unsorted Arraylist");
		unsorted_arraylist = get_array("rating");

		we = wdWait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector("div.dropdown_container:nth-child(3) div.dropdown_arrow")));
		we.click();
		we = wdWait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector("div.dropDown_list li:nth-child(2) li div")));
		we.click();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button_small:nth-child(1)")));
		we.click();

		// Log.info("Project List after Sorting by Rating :- ");

		Log.info("Sorted Arraylist");
		sorted_arraylist = get_array("rating");

	}

	@Test(priority = 5)
	public static void sort_by_size() {
		Log.info("Sorting by Size :- ");
		Log.info("Unsorted Arraylist");
		unsorted_arraylist = get_array("size");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		we = wdWait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector("div.dropdown_container:nth-child(3) div.dropdown_arrow")));
		we.click();

		we = wdWait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector("div.dropDown_list li:nth-child(4) li div")));
		we.click();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button_small:nth-child(1)")));
		we.click();

		// Log.info("Project List after Sorting by Size :- ");

		Log.info("Sorted Arraylist");
		sorted_arraylist = get_array("size");
		Log.info("------------------------------END of Test------------------------------");

	}

	@Test(priority = 6)
	public void navigateToHomePage() {
		driver.findElement(By.cssSelector("#project_tab")).click();
	}
}
