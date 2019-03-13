package jsonvalidation;

import java.util.HashMap;

public class DataProvider {
	
	HashMap<String, String> dataMap = new HashMap<String, String>();

	public HashMap<String, String> storeData(String key, String val)
	{	
		dataMap.put(key, val);
		return dataMap;
	}
	

};
