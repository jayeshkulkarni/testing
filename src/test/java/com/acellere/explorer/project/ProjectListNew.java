package com.acellere.explorer.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

import junit.framework.Assert;


public class ProjectListNew {
	//private static Logger Log = Logger.getLogger(Log.class.getName());
	public static String url = "http://localhost:3000/login.html";
	public static WebDriver driver;
	public static WebDriverWait wdWait ;
	public static int project_count;
	public static List<WebElement> project_details;
	public static String project_names = null;
	public static String LOC_count, no_of_Subsystems, project_rating, project_loc_count;
	public static String project_name_arr[] = null;
	public static String rating;
	public static List<String> unsorted_arraylist = new ArrayList<String>();
	public static List<String> sorted_arraylist = new ArrayList<String>();
	public static List<WebElement> elements;
	public static String panelTitleActual = "Projects";
	public static String panelTitleExpect;
	public static WebElement we;

	
	 @BeforeClass
		public static void initDriver() throws Exception
		{
			driver = BeforePageLoad.assignDriver();
			driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			wdWait = new WebDriverWait(driver,35);	
		}
	/*@BeforeClass
	public static void login_to_gamma() throws Exception 
	{
		//GammaLogin.selectBrowser();
		//GammaLogin.login();
		driver = GammaLogin.getWebDriver();
		wdWait = new WebDriverWait(driver,35);
		
	}*/


	@Test(priority = 1)
	public static void count_project(){
		Reporter.log("------------------------------Start of Test------------------------------",true);
		Reporter.log("Executing "+ProjectListNew.class.getName()+".....!!!",true);
		
		//wdWait = new WebDriverWait(driver,10);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		elements = wdWait.until(ExpectedConditions.
                presenceOfAllElementsLocatedBy(By.cssSelector("div.project_list")));
		project_count = elements.size();
		System.out.println("Total Number of Projects :-" +project_count);
		panelTitleExpect = driver.findElement(By.cssSelector(".panel_title")).getText();
		Assert.assertEquals(panelTitleExpect, panelTitleActual);
	}

	
	@Test(priority = 2)
	public static void displayProjectDetails()
	{
		for (int i = 1; i <= project_count; i++)
		{
			List<WebElement> project_details = driver.findElements(By.cssSelector("div.project_list:nth-child("+i+")"));

			for (WebElement we : project_details)
			{
				System.out.println("Project name :-" + we.getAttribute("data-name"));
				
				project_rating = driver
						.findElement(By.cssSelector("div.project_list:nth-child("+i+") div.project_rating div.h2"))
						.getText();
				project_loc_count = driver
						.findElement(By.cssSelector("div.project_list:nth-child("+i+") div.loc_count")).getText();
				project_loc_count = project_loc_count.substring(6, project_loc_count.length());
				no_of_Subsystems = driver
						.findElement(By.cssSelector("div.project_list:nth-child("+i+") div.project_count"))
						.getText();
				System.out.println("Project Rating :- " + project_rating);
				System.out.println("Project LOC Count :- " + project_loc_count);
				System.out.println("No of Subsystems :-" + no_of_Subsystems);

			}
		}
		
	}
	

	

	
	private static List<String> get_array(String sortBy){
		
		List<String> arrayList = new ArrayList<String>();
		
		for (int i = 1; i <= project_count; i++) 
		{
			List<WebElement> project_details = driver.findElements(By.cssSelector("div.project_list:nth-child("+i+")"));
			
			for (WebElement we1 : project_details) 
			{
				if("name".equalsIgnoreCase(sortBy))
				{
				project_names = we1.getAttribute("data-name");
				arrayList.add(project_names);
				}
				
				else if("size".equalsIgnoreCase(sortBy))
				{
					project_loc_count = driver
							.findElement(By.cssSelector("div.project_list:nth-child("+i+") div.loc_count")).getText();
					project_loc_count = project_loc_count.substring(6, 12);
					arrayList.add(project_loc_count);
				}
				
				else if("rating".equalsIgnoreCase(sortBy))
				{
					project_rating = driver
							.findElement(By.cssSelector("div.project_list:nth-child("+i+") div.project_rating div.h2"))
							.getText();
					arrayList.add(project_rating);
				}
				
				
			}
			
		}
	 System.out.println(arrayList);
		return arrayList;
	}
	
	
	@Test(priority = 3)
	public static void sort_by_name() 
	{
			System.out.println("Sorting by Name :- ");
			System.out.println("Unsorted Arraylist");
			unsorted_arraylist = get_array("name");
			we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dropdown_container:nth-child(3) div.dropdown_arrow")));
			we.click();
			
			we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dropDown_list li:nth-child(1) li div")));
			we.click();
			
			we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button_small:nth-child(1)")));
			we.click();
			System.out.println("Sorted Arraylist");
			sorted_arraylist = get_array("name");
			Collections.sort(unsorted_arraylist);
			
			//Assert.assertEquals(sorted_arraylist, unsorted_arraylist);
		/*	for (int i = 1; i <= project_count; i++) 
			{
				List<WebElement> project_details = driver.findElements(By.cssSelector("div.project_list:nth-child("+i+")"));
				
				for (WebElement we1 : project_details) 
				{
					project_names = we1.getAttribute("data-name");
					sorted_arraylist.add(project_names);
					Reporter.log(sorted_arraylist);
					project_rating = driver
							.findElement(By.cssSelector("div.project_list:nth-child("+i+") div.project_rating div.h2"))
							.getText();
					Reporter.log(project_rating);
				}
			}*/
		}
	
	
	@Test(priority = 4)
	public static void sort_by_rating()
	{
		System.out.println("Sorting by Rating :- ");
		System.out.println("Unsorted Arraylist");
		unsorted_arraylist = get_array("rating");
		
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dropdown_container:nth-child(3) div.dropdown_arrow")));
		we.click();
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dropDown_list li:nth-child(2) li div")));
		we.click();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button_small:nth-child(1)")));
		we.click();
		

		//Reporter.log("Project List after Sorting by Rating :- ");
		
		System.out.println("Sorted Arraylist");
		sorted_arraylist = get_array("rating");
		
	/*	for (int i = 1; i <= project_count; i++)
		{
			List<WebElement> project_details = driver.findElements(By.cssSelector("div.project_list:nth-child("+i+")"));
				for (@SuppressWarnings("unused") WebElement we1 : project_details) 
				{
					project_rating = driver.findElement(By.cssSelector("div.project_list:nth-child("+i+") div.project_rating div.h2"))
							.getText();
					Reporter.log(project_rating);
				}
			}*/
				
    	} 
	
	
	
	@Test(priority = 5)
	public static void sort_by_size()
	{
		System.out.println("Sorting by Size :- ");
		System.out.println("Unsorted Arraylist");
		unsorted_arraylist = get_array("size");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dropdown_container:nth-child(3) div.dropdown_arrow")));
		we.click();
		
	    we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dropDown_list li:nth-child(3) li div")));
		we.click();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button_small:nth-child(1)")));
        we.click();
		
        System.out.println("Sorted Arraylist");
		sorted_arraylist = get_array("size");
		//driver.findElement(By.cssSelector("#project_tab")).click();
		Reporter.log("------------------------------END of Test------------------------------",true);
		//driver.quit();
	}
	
	@AfterClass
	  public static void navigateHomePage()
	  {
		  HomePage.navigateBack();
		 
	  }
}


