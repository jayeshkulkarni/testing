package ListenersClass;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListner implements ITestListener
{

	public void onTestStart(ITestResult result) {
		
		System.out.println("Started Test Cases Details"+result.getName());
	}

	public void onTestSuccess(ITestResult result) {
		System.out.println("Passed Test Cases Details :-  "+result.getName());
		
	}

	public void onTestFailure(ITestResult result) {
		System.out.println("Baby This fails :- "+result.getName());
		
	}

	public void onTestSkipped(ITestResult result) {
		System.out.println("Skipped Test Cases Details"+result.getName());
		
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		
		
	}

	public void onStart(ITestContext context) {
		
		
	}

	public void onFinish(ITestContext context) {
	
		
	}

}
