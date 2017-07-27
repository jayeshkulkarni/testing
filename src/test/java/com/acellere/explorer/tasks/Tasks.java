package com.acellere.explorer.tasks;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.acellere.explorer.common.GammaLogin;
import com.acellere.explorer.common.RegisterTenant;

public class Tasks {
  
  //private static Logger Log = Logger.getLogger(Log.class.getName());
  public static WebDriver driver;
  public static WebDriverWait wdWait;
  public static WebElement we;
  
  @Test
  public static void login() throws Exception 
  {
	  RegisterTenant.selectBrowser();
	  GammaLogin.login();  
  }
  
  @Test(priority = 1)
  public static void navigateTasks() throws InterruptedException
  {
	  driver = GammaLogin.driver;
	  wdWait = new WebDriverWait(driver,35);
	    
	  JavascriptExecutor js = (JavascriptExecutor) driver;
		 WebElement element =  driver.findElement(By.cssSelector("#loading_overlay"));
		 js.executeScript("arguments[0].setAttribute('style', 'z-index:-1')",element);
		 
		 we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#tasks_tab")));
		 we.click();
		 	  
		 Thread.sleep(3000);    
		 
  }
  
  
  @Test(priority = 2)
	public void navigateToHomePage()
	{
		driver.findElement(By.cssSelector("#project_tab")).click();
	}
}
