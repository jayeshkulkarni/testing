package com.acellere.explorer.project;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.acellere.explorer.common.GammaLogin;
import com.acellere.explorer.common.RegisterTenant;

public class SearchProject {
  public static WebDriver driver;
  public static List<WebElement> elements;
  public static WebElement we;
  public static WebDriverWait wdWait;
  public static int project_count;
  public static String project_name;
  public static String project_Search;
  @BeforeClass
  public static void beforeClass() throws InterruptedException, IOException
  {
	  RegisterTenant.selectBrowser();
	  GammaLogin.login();
	  driver = GammaLogin.driver;
	  wdWait = new WebDriverWait(driver,35);
  }
  @Test
  public static void search() 
  {
	  project_Search = "mipp";
	   we = wdWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".project_search_box")));
	   we.sendKeys(project_Search);
	   System.out.println("Searching for Project having substring as :- "+project_Search);
		elements = wdWait.until(ExpectedConditions.
                presenceOfAllElementsLocatedBy(By.cssSelector("div.project_list")));
		project_count = elements.size();
		System.out.println("Project count having substring as :-"+project_Search +" is " +project_count);
		
		
		for(WebElement we1:elements)
		{
			project_name = we1.getAttribute("data-name");
			System.out.println(project_name);
		}
		driver.quit();
	}
	
  }
