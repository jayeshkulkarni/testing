package com.acellere.admin;

import org.testng.annotations.Test;

import com.acellere.explorer.common.BeforePageLoad;
import com.acellere.explorer.common.GammaLogin;
import com.acellere.explorer.common.RegisterTenant;

import org.testng.annotations.BeforeClass;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;

public class ProjectCreate {
	public static WebDriver driver;
	public static WebDriverWait wdWait;
	public static WebElement we;
	public static List<WebElement> projectList;
	public static WebElement wTable;
	public static WebElement wRow;
	public static WebElement wCol;
	public static int i = 1;

	@BeforeClass
	public static void initDriver() throws Exception {
		RegisterTenant.selectBrowser();
		GammaLogin.login();
		driver = BeforePageLoad.assignDriver();
		wdWait = new WebDriverWait(driver, 35);
	}

	@Test(priority = 1)
	public void navigateToProjectPage() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='user_management']")));
		we.click();
		we = wdWait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector(".right_menu_list .right_menu_item:nth-child(2)")));
		we.click();
		we = wdWait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector(".menu_container .menu_item:nth-child(4)")));
		we.click();
	}

	@Test(priority = 2, enabled = false)
	public void addProject() {
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".add_search_button")));
		we.click();
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#project_name")));
		we.sendKeys("TEST SERVER");
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#project_description")));
		we.sendKeys("New Project");
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".addProjectBtn")));
		we.click();
		System.out.println("Project Added Success !!");

	}

	@Test(priority = 3, enabled = false)
	public void searchProject() {
		we = wdWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#search_box")));
		we.sendKeys("pp");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		System.out.println("Pre Stage 1");
		projectList = driver.findElements(By.cssSelector(".list_container"));
		System.out.println("Stage 1");
		for (WebElement we1 : projectList) {
			we1 = driver.findElement(By.cssSelector(".list_item:nth-child(" + i + ") .list_item_name"));
			System.out.println(we1.getText());
			i++;
		}

	}

	/*
	 * @Test(priority = 4) public void deleteProject() {
	 * 
	 * }
	 * 
	 * 
	 * @Test(priority = 5) public void countProjects() {
	 * 
	 * }
	 */

	@AfterClass
	public void afterClass() {
		driver.quit();
	}

}
