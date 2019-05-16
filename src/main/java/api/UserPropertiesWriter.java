package api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class UserPropertiesWriter {

	public static void main(String[] args) throws IOException {

		LogManager logManager = LogManager.getLogManager(); 
		Logger logger = logManager.getLogger(Logger.GLOBAL_LOGGER_NAME);
		
		String sourceFile = System.getProperty("path.properties");
		logger.log(Level.INFO, "Getting configuration details from: " + sourceFile);

		String destFile = System.getenv("JMETER_HOME") + File.separator + "bin" + File.separator + "user.properties";
		
		logger.log(Level.INFO, "JMETER_HOME: " + System.getenv("JMETER_HOME") +"\n"+"API_TEST_DATA: " + System.getenv("API_TEST_DATA")+"\n"+"API_TEST_REPORT: " + System.getenv("API_TEST_REPORT"));
		logger.log(Level.INFO, "Adding configuration details to: " + destFile);

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
