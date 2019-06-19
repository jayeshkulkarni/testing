package com.acellere.explorer.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ProjectListOld {
	private static Logger Log = Logger.getLogger(Log.class.getName());
	public static String url = "http://localhost:3000/login.html";
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static int project_count;
	public static List<WebElement> project_details;
	public static String project_names = null;
	public static String LOC_count, no_of_Subsystems, project_rating, project_loc_count;
	public static String project_name_arr[] = null;
	public static String rating;
	public static List<String> unsorted_arraylist = new ArrayList<String>();
	public static List<String> sorted_arraylist = new ArrayList<String>();
	public static List<WebElement> elements;

	@BeforeClass
	public static void login_to_gamma() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().window().maximize();
		wdWait = new WebDriverWait(driver, 5);
		driver.navigate().to(url);
		driver.findElement(By.xpath(".//*[@id='username']")).sendKeys("account@acellere.com");
		driver.findElement(By.xpath(".//*[@id='password']")).sendKeys("account123");
		Log.info("Waiting for Login ................");
		driver.findElement(By.cssSelector(".signin-button")).click();
		Log.info("Login Successfully");

	}

	@Test(priority = 1)
	public static void count_project() throws InterruptedException {

		elements = wdWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.project_list")));
		project_count = elements.size();
		Log.info("Total Number of Projects :-" + project_count);

	}

	@Test(priority = 2)
	public static void displayProjectDetails() {
		for (int i = 1; i <= project_count; i++) {
			List<WebElement> project_details = driver
					.findElements(By.cssSelector("div.project_list:nth-child(" + i + ")"));

			for (WebElement we : project_details) {
				Log.info("Project name :-" + we.getAttribute("data-name"));

				project_rating = driver
						.findElement(By.cssSelector("div.project_list:nth-child(" + i + ") div.project_rating div.h2"))
						.getText();
				project_loc_count = driver
						.findElement(By.cssSelector("div.project_list:nth-child(" + i + ") div.loc_count")).getText();
				project_loc_count = project_loc_count.substring(6, 11);
				no_of_Subsystems = driver
						.findElement(By.cssSelector("div.project_list:nth-child(" + i + ") div.project_count"))
						.getText();
				Log.info("Project Rating :- " + project_rating);
				Log.info("Project LOC Count :- " + project_loc_count);
				Log.info("No of Subsystems :-" + no_of_Subsystems);

			}
		}

	}

	private static List<String> get_array(String sortBy) {

		List<String> arrayList = new ArrayList<String>();

		for (int i = 1; i <= project_count; i++) {
			List<WebElement> project_details = driver
					.findElements(By.cssSelector("div.project_list:nth-child(" + i + ")"));

			for (WebElement we1 : project_details) {
				if ("name".equalsIgnoreCase(sortBy)) {
					project_names = we1.getAttribute("data-name");
					arrayList.add(project_names);
				}

				else if ("size".equalsIgnoreCase(sortBy)) {
					project_loc_count = driver
							.findElement(By.cssSelector("div.project_list:nth-child(" + i + ") div.loc_count"))
							.getText();
					project_loc_count = project_loc_count.substring(6, 11);
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
	public static void sort_by_name() throws Exception {
		// Log.info("Project List after Sorting by Name :- ");
		Log.info("Unsorted Arraylist");
		unsorted_arraylist = get_array("name");
		driver.findElement(By.cssSelector("div.dropdown_container div.dropdown_arrow")).click();
		driver.findElement(By.cssSelector(".dropdown_list_data:nth-child(1)")).click();
		Thread.sleep(2000);
		driver.findElement(By.cssSelector(".button_small:nth-child(1)")).click();
		Log.info("Sorted Arraylist");
		sorted_arraylist = get_array("name");
		Collections.sort(unsorted_arraylist);
		Log.info(unsorted_arraylist);
		// Assert.assertEquals(sorted_arraylist, unsorted_arraylist);
		for (int i = 1; i <= project_count; i++) {
			List<WebElement> project_details = driver
					.findElements(By.cssSelector("div.project_list:nth-child(" + i + ")"));

			for (WebElement we1 : project_details) {
				project_names = we1.getAttribute("data-name");
				sorted_arraylist.add(project_names);
				Log.info(sorted_arraylist);
				project_rating = driver
						.findElement(By.cssSelector("div.project_list:nth-child(" + i + ") div.project_rating div.h2"))
						.getText();
				Log.info(project_rating);
			}
		}
	}

	@Test(priority = 4)
	public static void sort_by_rating() throws Exception {
		Log.info("Unsorted Arraylist");
		unsorted_arraylist = get_array("rating");

		Thread.sleep(2000);
		driver.findElement(By.cssSelector("div.dropdown_container div.dropdown_arrow")).click();

		Thread.sleep(2000);
		driver.findElement(By.cssSelector(".dropdown_list_data:nth-child(2)")).click();
		Thread.sleep(2000);

		driver.findElement(By.cssSelector(".button_small:nth-child(1)")).click();
		Thread.sleep(2000);
		// Log.info("Project List after Sorting by Rating :- ");

		Log.info("Sorted Arraylist");
		sorted_arraylist = get_array("rating");

		for (int i = 1; i <= project_count; i++) {
			List<WebElement> project_details = driver
					.findElements(By.cssSelector("div.project_list:nth-child(" + i + ")"));
			for (@SuppressWarnings("unused")
			WebElement we1 : project_details) {
				project_rating = driver
						.findElement(By.cssSelector("div.project_list:nth-child(" + i + ") div.project_rating div.h2"))
						.getText();
				Log.info(project_rating);
			}
		}

	}

	/*
	 * @Test(priority = 4) public static void sort_by_language() throws Exception {
	 * Thread.sleep(2000); GammaLogin.driver.findElement(By.
	 * cssSelector("div.dropdown_container div.dropdown_arrow")).click();
	 * 
	 * Thread.sleep(2000); GammaLogin.driver.findElement(By.cssSelector(
	 * ".dropdown_list_data:nth-child(3)")).click(); Thread.sleep(2000);
	 * 
	 * GammaLogin.driver.findElement(By.cssSelector(".button_small:nth-child(1)")).
	 * click(); Thread.sleep(2000);
	 * Log.info("Project List after Sorting by Rating :- "); for (int i = 1; i <=
	 * Project.project_count; i++) { List<WebElement> project_details =
	 * GammaLogin.driver.findElements(By.cssSelector("div.project_list:nth-child("+i
	 * +")")); for (WebElement we1 : project_details) { project_rating =
	 * GammaLogin.driver.findElement(By.cssSelector(
	 * "div.project_languages:nth-child("+i+") div.project_rating div.h2"))
	 * .getText(); Log.info(project_rating); } } GammaLogin.close_browser(); }
	 */

	@Test(priority = 5)
	public static void sort_by_size() throws Exception {
		Log.info("Unsorted Arraylist");
		unsorted_arraylist = get_array("size");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector("div.dropdown_container div.dropdown_arrow")).click();

		Thread.sleep(2000);
		driver.findElement(By.cssSelector(".dropdown_list_data:nth-child(4)")).click();
		Thread.sleep(2000);

		driver.findElement(By.cssSelector(".button_small:nth-child(1)")).click();
		Thread.sleep(2000);
		// Log.info("Project List after Sorting by Size :- ");

		Log.info("Sorted Arraylist");
		sorted_arraylist = get_array("size");
		driver.quit();
	}

	/*
	 * for (int i = 1; i <= Project.project_count; i++) { List<WebElement>
	 * project_details =
	 * GammaLogin.driver.findElements(By.cssSelector("div.project_list:nth-child("+i
	 * +")")); for (WebElement we1 : project_details) { LOC_count =
	 * GammaLogin.driver.findElement(By.cssSelector("div.project_list:nth-child("+
	 * i+") div.loc_count")).getText(); LOC_count = LOC_count.substring(6, 11);
	 * Log.info("LOC Count :- "+LOC_count); } } GammaLogin.close_browser(); }
	 */

	/*
	 * @Test(priority = 4) public static void switch_to_listView() throws Exception
	 * { wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(
	 * "#List_View")));
	 * GammaLogin.driver.findElement(By.cssSelector("#List_View")).click();
	 * 
	 * }
	 * 
	 * @Test(priority = 5) public static void switch_to_gridView() throws Exception
	 * { wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(
	 * "#Grid_View")));
	 * GammaLogin.driver.findElement(By.cssSelector("#Grid_View")).click();
	 * 
	 * 
	 * }
	 */

}
