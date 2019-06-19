package com.embold.ui.utils;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class IWait {

	public static boolean implicit_wait(WebDriver driver,long time) {
		try {
			driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
		} catch (Exception e) {
			return false;

		}
		return true;

	}

	public static boolean explicit_wait(WebDriver driver, WebElement el) {
		try {
			new WebDriverWait(driver, 10).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(el));

		} catch (Exception e) {
			return false;

		}
		return true;

	}

}
