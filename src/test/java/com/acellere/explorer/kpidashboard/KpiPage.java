package com.acellere.explorer.kpidashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class KpiPage {
	//private static Logger Log = Logger.getLogger(Log.class.getName());
    public static WebDriver driver;
    public static WebDriverWait wdWait;
    public static WebElement we;
    public static String panelTitleActual = "KPI Dashboard";
    public static String panelTitleExpected;
    
    @BeforeClass
 	public static void initDriver() throws Exception
 	{
 		driver = BeforePageLoad.assignDriver();
 		wdWait = new WebDriverWait(driver,35);	
 	}
    
     
  @Test(priority = 1)
  public static void navigateKpiDashboard() throws InterruptedException
  {
	  System.out.println("------------------------------Start of Test------------------------------");
	  Reporter.log("Executing "+KpiPage.class.getName()+".....!!!",true);
	     Thread.sleep(500);
	  	 we = wdWait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div.project_list:nth-child(1)"))));
		 we.click();
		 we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.project_list:nth-child(1)")));
		 we.click();
		 JavascriptExecutor js = (JavascriptExecutor) driver;
		 WebElement element =  driver.findElement(By.cssSelector("#loading_overlay"));
		 js.executeScript("arguments[0].setAttribute('style', 'z-index:-1')",element);
		 Thread.sleep(500);
		 we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.dashboard .hand_cursor:nth-child(3)")));
		 we.click();
		 Thread.sleep(500);
		 panelTitleExpected = driver.findElement(By.cssSelector("div.panel_title")).getText();
		 System.out.println("Panel Title is:- "+panelTitleActual);
		 Assert.assertEquals(panelTitleActual, panelTitleExpected, "Title Mismatch");
		 driver.findElement(By.cssSelector("#project_tab")).click();
		 System.out.println("-------------------------------END of Test-------------------------------");
  }
  
  
  @AfterClass
  public static void navigateHomePage()
  {
	  HomePage.navigateBack();
	 
  }
}

