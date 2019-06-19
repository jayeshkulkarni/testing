package com.embold.ui.com.embold.ui.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.embold.ui.assertions.User;
import com.embold.ui.utils.BrowserType;
import com.embold.ui.utils.Driver;

import junit.framework.Assert;

public class UserTest {

	@DataProvider(name = "UserData")
	public Object[][] getDataFromDataprovider() {
		return new Object[][] {
				{ "http://192.168.10.10:4445/wd/hub", BrowserType.CHROME, "http://192.168.3.76:3000",
						"saurabh.patil@acellere.com", "admin" },
				{ "http://192.168.10.10:4445/wd/hub", BrowserType.FIREFOX, "http://192.168.3.76:3000",
						"saurabh.patil@acellere.com", "admin" } };
	}

	// ,invocationCount = 10
	@Test(dataProvider = "UserData")
	public void f(String gridUrl, BrowserType type, String gammaUrl, String userName, String password) {
		Assert.assertTrue(new User().login(new Driver().getBrowser(type, gridUrl), gammaUrl, userName, password));

	}

	@Test(dataProvider = "UserData")
	public void f1(String gridUrl, BrowserType type, String gammaUrl, String userName, String password) {
		Assert.assertTrue(new User().login(new Driver().getBrowser(type, gridUrl), gammaUrl, userName, password));

	}

	@Test(dataProvider = "UserData")
	public void f2(String gridUrl, BrowserType type, String gammaUrl, String userName, String password) {
		Assert.assertTrue(new User().login(new Driver().getBrowser(type, gridUrl), gammaUrl, userName, password));

	}

	@Test(dataProvider = "UserData")
	public void f3(String gridUrl, BrowserType type, String gammaUrl, String userName, String password) {
		Assert.assertTrue(new User().login(new Driver().getBrowser(type, gridUrl), gammaUrl, userName, password));

	}
	@Test(dataProvider = "UserData")
	public void f4(String gridUrl, BrowserType type, String gammaUrl, String userName, String password) {
		Assert.assertTrue(new User().login(new Driver().getBrowser(type, gridUrl), gammaUrl, userName, password));

	}
}
