package ListenersClass;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListner implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {

		System.out.println("Started Test Cases Details" + result.getName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		System.out.println("Passed Test Cases Details :-  " + result.getName());

	}

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("Baby This fails :- " + result.getName());

	}

	@Override
	public void onTestSkipped(ITestResult result) {
		System.out.println("Skipped Test Cases Details" + result.getName());

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	@Override
	public void onStart(ITestContext context) {

	}

	@Override
	public void onFinish(ITestContext context) {

	}

}
