package com.acellere.admin.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.acellere.explorer.common.GammaLogin;
import com.acellere.explorer.common.RegisterTenant;

public class userDetails
{
	public static String url = "http://localhost:3000/login.html";
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static int localSleepTime = 500;
	public static int testServerSleepTime = 1000;
	public static String stringFound;
	public static Properties pro;
 	
	
	
	
	  @BeforeClass
	 	public static void initDriver() throws Exception
	 	{
		  RegisterTenant.selectBrowser();
		  GammaLogin.login();
	 	  driver = GammaLogin.getWebDriver();
	 	  wdWait = new WebDriverWait(driver,35);	
	 	// Specify the file location I used . operation here because
	    //we have object repository inside ProjectCreate directory only
	 	 	File src=new File("\\Gamma_Automation\\Gamma_Explorer_New\\src\\main\\resources\\uiObjects.properties");
	 	 						 
	 	 	// Create  FileInputStream object
	 	 	 FileInputStream fis=new FileInputStream(src);
	 	 						 
	 	// Create Properties class object to read properties file
	 	 	 pro=new Properties();
	 	 						 
	 	 // Load file so we can use into our script
	 	 	 pro.load(fis);
	 	}
	  
  @Test
  public static WebDriver navigateUserDetails() throws IOException, InterruptedException 
  {
	driver.manage().window().maximize();
		
	wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='user_management']"))).click();
    wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[5]/div[2]/div/div[3]"))).click();
     Thread.sleep(2000);
    //wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='content']/div[1]/div/div[7]"))).click();		
	return driver;
  }	
 }

