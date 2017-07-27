package com.acellere.admin.User;

import java.io.IOException;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.acellere.explorer.common.GammaLogin;

public class user
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
		  userDetails.initDriver();
		  
	 	}
	  
  @Test(priority=1)
  public void addnewUSer() throws Exception 
  {
	//driver.manage().window().maximize();
	//  driver = GammaLogin.getWebDriver();
	 driver= userDetails.navigateUserDetails();
     Thread.sleep(2000);
    //wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='content']/div[1]/div/div[7]"))).click();		
	driver.findElement(By.cssSelector(pro.getProperty("gamma.adduser.newuser.cssSelector"))).click();
	driver.findElement(By.cssSelector(pro.getProperty("gamma.adduser.fname.cssSelector"))).sendKeys("Gamma");
	driver.findElement(By.cssSelector(pro.getProperty("gamma.adduser.lname.cssSelector"))).sendKeys("Admin");
	driver.findElement(By.cssSelector(pro.getProperty("gamma.adduser.jobtitle.cssSelector"))).sendKeys("Administrator");
	driver.findElement(By.cssSelector(pro.getProperty("gamma.adduser.email.cssSelector"))).sendKeys("admin@admin.com");
	driver.findElement(By.cssSelector(pro.getProperty("gamma.adduser.password.cssSelector"))).sendKeys("admin123");
	driver.findElement(By.cssSelector(pro.getProperty("gamma.adduser.confirm_password.cssSelector"))).sendKeys("admin123");
	driver.findElement(By.cssSelector(pro.getProperty("gamma.adduser.adduserbtn.cssSelector"))).click();	
  }
  @Test(priority=2)
  public void searchUser() throws IOException, InterruptedException 
  {
	  Thread.sleep(1000);
	  driver.findElement(By.cssSelector( ("gamma.adduser.searchuser.cssselector"))).click();
	  driver.findElement(By.cssSelector( ("gamma.adduser.searchuser.cssselector"))).sendKeys("admin");
	  stringFound = driver.findElement(By.cssSelector("gamma.adduser.userfound.cssselector")).getText();
	  System.out.println(stringFound);
  }
 }

