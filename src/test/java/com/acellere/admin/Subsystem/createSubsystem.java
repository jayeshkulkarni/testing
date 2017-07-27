package com.acellere.admin.Subsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.acellere.explorer.common.GammaLogin;
import com.acellere.explorer.common.RegisterTenant;

public class createSubsystem {
	public static String url = "http://localhost:3000/login.html";
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static String stringFound;
	public static Properties pro;
 


	  	@BeforeClass
	 	public static void initDriver() throws Exception
	 	{
	  		RegisterTenant.selectBrowser();
			GammaLogin.login();
		    driver = GammaLogin.getWebDriver();
		 	wdWait = new WebDriverWait(driver,35);	
		 	File src=new File("\\Gamma_Automation\\Gamma_Explorer_New\\src\\main\\resources\\uiObjects.properties");
				 
	 	 	// Create  FileInputStream object
	 	 	 FileInputStream fis=new FileInputStream(src);
	 	 						 
	 	// Create Properties class object to read properties file
	 	 	 pro=new Properties();
	 	 						 
	 	 // Load file so we can use into our script
	 	 	 pro.load(fis);
		  
	 	}
	  
	
	  	@Test
	  	public void addSubsystem() throws InterruptedException, IOException 
	  	{
	  		driver.manage().window().maximize();
			wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='user_management']"))).click();
	  	    wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[5]/div[2]/div/div[3]"))).click();
	  	  Thread.sleep(1000);
	  	   wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".menu_container .menu_item:nth-child(5)"))).click();
	  	   Thread.sleep(1000);
	  	   driver.findElement(By.cssSelector(".add_search_button")).click();
	  	   driver.findElement(By.cssSelector(".element_wrapper .dropdown[data-label='account_type']")).click();
	  	   Thread.sleep(2000);
	  	   
	  	   List<WebElement> we = driver.findElements(By.cssSelector(".dropDown_list .dropdown_list_item"));
	  	   
	  	   for(WebElement obj:we)
	  	   {
	  		   //System.out.println(obj.getText());
	  		   if(obj.getText().equals("git"))
	  		   {
	  			   System.out.println("git");
	  		   }
	  		   else
	  			 if(obj.getText().equals("svn"))
		  		   {
		  			   System.out.println("svn");
		  		   }
	  			 else
	  			   {
	 	  			   System.out.println("zip");
	 	  		   }

	  	   }
	  	   
	  	   /*
	  	  driver.findElement(By.cssSelector(".dropDown_list .dropdown_list_item .language_text[data-language_id='zip']")).click();
	  	  driver.findElement(By.cssSelector(".dropDown_list .dropdown_list_item .language_text[data-language_id='zip']")).click();
	  	  driver.findElement(By.cssSelector("#subsystem_name")).sendKeys("elliptics_ell");
		  Thread.sleep(2000);
	  	  driver.findElement(By.cssSelector(".element_wrapper .dropdown[data-label='language']")).click(); 	
	   
	  	 // driver.findElement(By.cssSelector(".dropDown_list .dropdown_list_data:nth-child(0)")).click();
	  	wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".dropDown_list .dropdown_list_data:nth-child(2)"))).click();
	 	Thread.sleep(1000);
	  	driver.findElement(By.cssSelector("#codeuploader")).click();
	  	Thread.sleep(1000);
	  	Runtime.getRuntime().exec("C:\\Users\\RahulG\\Desktop\\AutoIT\\uploadZip3.exe");
	  	Thread.sleep(1000);
	  	driver.findElement(By.cssSelector(".addSubsystemBtn")).click();
	  	  //driver.findElement(By.cssSelector("#url")).sendKeys("https://github.com/reverbrain/elliptics.git")
	  	 // driver.findElement(By.cssSelector("#subsystem_name")).sendKeys("Elliptics_git");		
	  	 // driver.findElement(By.cssSelector(".element_wrapper .dropdown[data-label='language']")).click(); 		
	  	 // driver.findElement(By.cssSelector(".dropDown_list .dropdown_list_data:nth-child(1)")).click();
    */
	  	}
}
