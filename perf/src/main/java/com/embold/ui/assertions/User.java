package com.embold.ui.assertions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.embold.ui.pageobjects.LoginPage;
import com.embold.ui.utils.IWait;

public class User {

	public boolean login(WebDriver browser,String url,String userName,String password) {		
		try {
			browser.get(url);
			LoginPage loginPage=PageFactory.initElements(browser, LoginPage.class);
			IWait.explicit_wait(browser,loginPage.signIn);
			loginPage.userName.sendKeys(userName);
			loginPage.password.sendKeys(password);
			loginPage.signIn.click();
			IWait.explicit_wait(browser,loginPage.logout);
			loginPage.logout.click();
			browser.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
