package com.acellere.explorer.hotspot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.acellere.explorer.common.BeforePageLoad;
import com.acellere.explorer.common.HomePage;



public class HotSpotPage {
	//private static Logger Log = Logger.getLogger(Log.class.getName());
    public static WebDriver driver;
    public static WebDriverWait wdWait;
    public static WebElement we;
    public static String panelTitleActual = "Hotspot Distribution";
    public static String panelTitleExpected;
  

    @BeforeClass
 	public static void initDriver() throws Exception
 	{
 		driver = BeforePageLoad.assignDriver();
 		wdWait = new WebDriverWait(driver,35);	
 	}
  
  @Test(priority = 1)
  public static void navigateHotSpotDistribution() throws InterruptedException
  {
	  System.out.println("------------------------------Start of Test------------------------------");
	  Reporter.log("Executing "+HotSpotPage.class.getName()+".....!!!",true);	
	  Thread.sleep(500);
	  	 we = wdWait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div.project_list:nth-child(1)"))));
		 we.click();
		 		 
		 we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.project_list:nth-child(1)")));
		 we.click();
		 
		  we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li.plugin_details_wrapper:nth-child(2)")));
		  we.click();
		  
		  panelTitleExpected = driver.findElement(By.cssSelector("div.panel_title")).getText();
		  System.out.println("Panel Title is:- "+panelTitleActual);
		  Assert.assertEquals(panelTitleActual, panelTitleExpected, "Title Matches");
		  Thread.sleep(500);
		  driver.findElement(By.cssSelector("#project_tab")).click();
		  System.out.println("-------------------------------END of Test-------------------------------");
  }
  
  
  @AfterClass
  public static void navigateHomePage()
  {
	  HomePage.navigateBack();
	 
  }
}
