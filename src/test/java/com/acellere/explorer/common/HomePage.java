package com.acellere.explorer.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class HomePage 
{
 public static WebDriver driver;
  @Test
  public static void navigateBack() 
  {
	  driver = GammaLogin.getWebDriver();
	  driver.findElement(By.cssSelector("#project_tab")).click();
  }
 }

