package com.embold.ui.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage {
	
	@FindBy(xpath="//input[@value=\"SIGN IN\"]")
	public WebElement signIn;

	@FindBy(xpath="//*[@id=\"username\"]")
	public WebElement userName;
	
	@FindBy(xpath="//*[@id=\"password\"]")
	public WebElement password;
	
	@FindBy(xpath="//div/div[.=\"Logout\"]")
	public WebElement logout;
	
	@FindBy(xpath="//div[@class=\"panel_title_text float_left\" and contains(text(),\"Projects\")]")
	public WebElement projectsLabel;
}
