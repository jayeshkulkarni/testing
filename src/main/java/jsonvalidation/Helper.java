package jsonvalidation;

import com.google.gson.JsonObject;

import io.restassured.specification.RequestSpecification;

public class Helper {
	
	//Generic method to set request parameters
	//on-hold
	
	public void configureRequest(JsonObject jsonObj, RequestSpecification httpRequest, String token, int param)
	{
		httpRequest.header("Authorization","Bearer "+token);
		for (int i = 0;i< param; i++) 
		{
			jsonObj.addProperty("abc", "abc");
			//Get number of parameters from child class method
		}
		
	}

}
