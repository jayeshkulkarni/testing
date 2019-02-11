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

		/*	
		 * File rootDir = new File("." + File.separator + "src" + File.separator +
		 * "main" + File.separator + "resources"); String sourceFile =
		 * rootDir.getCanonicalPath() + File.separator +
		 * "apiReportGeneratorConfig.properties";
		 */		
		//Skipped above temporarily since user will share the configuration file path as a parameter in terminal.
		/* String sourceFile= System.getProperty("path.properties"); */
		
		//To be done:: Provide static paths from Vagrantfile
		String sourceFile= "/vagrant/api/resources/apiReportGeneratorConfig.properties";
		System.out.println("Getting configuration details from: "+sourceFile);

		/*
		 * String destFilePath = System.getenv("JMETER_HOME"); String destFile =
		 * destFilePath + File.separator + "bin" + File.separator + "user.properties";
		 */
		
		String destFile = "/vagrant/api/resources/apache-jmeter-5.0/bin/user.properties";

		/* System.out.println("JHOME: "+System.getenv("JMETER_HOME")); */
		System.out.println("Adding configuration details to: "+destFile);

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
