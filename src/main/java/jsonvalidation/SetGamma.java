package jsonvalidation;

import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class SetGamma {
	
	PropertiesReader props = new PropertiesReader();
	RequestSpecification httpRequest;
	JsonObject loginCredentials;
    
	public SetGamma() {
		RestAssured.baseURI = props.getGammaURL();
		RestAssured.basePath = props.getPropertyValue("base_apiPath");
	    httpRequest = RestAssured.given();
	    httpRequest.header("Content-Type", "application/json");
	}
}
