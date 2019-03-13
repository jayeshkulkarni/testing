package jsonvalidation;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {
	Properties properties;
	
	public PropertiesReader() {
		readProperty();
	}
	
	public void readProperty()
	{	
		properties = new Properties();
		try {
			FileInputStream inputStream = new FileInputStream("./src/main/resources/gamma.properties");
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getGammaURL()
	{
		String str = properties.getProperty("http_port")+"://"+
				properties.getProperty("gamma_ip")+":"+
				properties.getProperty("gamma_port");
		return str;
	}
	
	public String getPropertyValue(String str)
	{
		return properties.getProperty(str);
	}
}
