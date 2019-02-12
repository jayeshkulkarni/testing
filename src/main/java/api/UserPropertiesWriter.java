package api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserPropertiesWriter {

	public static void main(String[] args) throws IOException {

		String sourceFile = System.getProperty("path.properties");
		System.out.println("Getting configuration details from: " + sourceFile);

		String destFilePath = System.getenv("JMETER_HOME");
		String destFile = destFilePath + File.separator + "bin" + File.separator + "user.properties";
		System.out.println("JMETER_HOME: " + System.getenv("JMETER_HOME"));
		System.out.println("API_TEST_DATA: " + System.getenv("API_TEST_DATA"));
		System.out.println("API_TEST_REPORT: " + System.getenv("API_TEST_REPORT"));
		System.out.println("Adding configuration details to: " + destFile);

		File fins = new File(sourceFile);
		FileInputStream fileInputStream = new FileInputStream(fins);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
		FileWriter fileWriter = new FileWriter(destFile, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		String data = null;
		while ((data = bufferedReader.readLine()) != null) {
			bufferedWriter.write("\n" + data);
		}

		bufferedReader.close();
		bufferedWriter.close();

	}

}
