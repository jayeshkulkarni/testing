package com.acellere.explorer.common;


import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RegisterTenant {
	private static Logger Log = Logger.getLogger(Log.class.getName());
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static String url="http://localhost:3000/register";	
	public static String popupMsg;
	public static String actualMsg = "Company 'Acellere' already exists. Please try another name.";
	
	
	public static WebDriver getWebDriver() {
		return driver;
	}
	
	@BeforeClass()
	public static void selectBrowser() 
	{
		driver = new FirefoxDriver();
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		wdWait = new WebDriverWait(driver,35);
		driver.manage().window().maximize();
	}
	
  @Test
  public void RegistrationDetails() throws InterruptedException 
  {
	  driver.navigate().to(url);
	  wdWait = new WebDriverWait(driver,20);
	  Log.info("------------------------------Start of Test------------------------------");
	  Log.info("Registering Tenant User");
	  driver.findElement(By.xpath(".//*[@id='company_name']")).sendKeys("Acellere"/*+companyName*/);
	  driver.findElement(By.xpath(".//*[@id='company_address']")).sendKeys("Downtown - The City Centre, (DTC),, Mhatre Bridge, Vakil Nagar, Erandwane, Pune, Maharashtra 411004");
	  driver.findElement(By.xpath(".//*[@id='company_website']")).sendKeys("http://www.acellere.com");
	  driver.findElement(By.xpath(".//*[@id='first_name']")).sendKeys("Account");
	  driver.findElement(By.xpath(".//*[@id='last_name']")).sendKeys("Manager");
	  driver.findElement(By.xpath(".//*[@id='phone']")).sendKeys("9890098900");
	  driver.findElement(By.xpath(".//*[@id='job_title']")).sendKeys("Manager");
	  driver.findElement(By.xpath(".//*[@id='email']")).sendKeys("account@acellere.com");
	  driver.findElement(By.xpath(".//*[@id='password']")).sendKeys("account123");
	  driver.findElement(By.xpath(".//*[@id='confirmPassword']")).sendKeys("account123");
	  driver.findElement(By.cssSelector(".button_register")).click();
	  //driver.switchTo().alert();
	  popupMsg = driver.findElement(By.cssSelector("div.error_message .error_message_text")).getText();
	  //Reporter.log(popupMsg,true);
	  
	  if(actualMsg == popupMsg)
	   {
		  Log.info(popupMsg);
	   }
	  
	  wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#close"))).click();
	  //driver.findElement(By.cssSelector("#close")).click();

  }
}
