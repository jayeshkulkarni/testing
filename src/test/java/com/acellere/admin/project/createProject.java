package com.acellere.admin.project;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.acellere.explorer.common.RegisterTenant;

public class createProject 
{
	private static Logger Log = Logger.getLogger(Log.class.getName());
	public static String url = "http://localhost:3000/login.html";
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static int localSleepTime = 500;
	public static int testServerSleepTime = 1000;
	public static Properties pro;	
	public static String projectName;
	public static int noOfProjects;
	
  @Test(priority=1)
  public void addProject() throws InterruptedException, IOException 
  {
	  //RegisterTenant.selectBrowser();
	  //GammaLogin.login();
	  Log.info("Creating Project");
	  driver = RegisterTenant.getWebDriver();
	  wdWait = new WebDriverWait(driver,35);	
	  driver.manage().window().maximize();
	
	  wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='user_management']"))).click();;
      wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".right_menu_list .right_menu_item:nth-child(2)"))).click();;
      wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".menu_container .menu_item:nth-child(4)"))).click();
      
      //projectName = driver.findElement(By.cssSelector(".list_container .list_item[data-name='Project_1'] .list_item_name")).getText();
      //System.out.println(projectName+ " already exists");
      
      noOfProjects = driver.findElements(By.cssSelector(".data_container .list_container div.list_item")).size();
      System.out.println("Total projects are :- "+noOfProjects);
      
      projectName ="Project_1";
      if(noOfProjects == 0)
      {
    	  //Thread.sleep(3000);
          wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".createProjectBtn"))).click();
          wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#project_name"))).sendKeys(projectName);
    	  wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#project_description"))).sendKeys(projectName);
    	  driver.findElement(By.cssSelector(".addProjectBtn")).click();
    	  Log.info(projectName);
    	  //Thread.sleep(3000);
      }
      
      else
      {
    	  Log.info("Project already Exists");
      }
     
   // wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".menu_container .menu_item:nth-child(5)"))).click();
}
  
  @Test(priority=2,enabled=false)
  public void addSubSystem()
  {
	  driver.findElement(By.cssSelector(".menu_container .menu_item:nth-child(5)")).click();
  }
}
