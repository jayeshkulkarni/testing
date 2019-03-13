package jsonvalidation;

import com.google.gson.JsonObject;

import io.restassured.response.Response;

public class GenerateBearerToken{
	
	DataProvider provider;
	SetGamma setGamma;
	JsonObject loginCredentials;
	public GenerateBearerToken() {
		setGamma = new SetGamma();
		provider = new DataProvider();
		loginCredentials = new JsonObject();
	}
	
	public String getAccessToken()
	{	
        loginCredentials.addProperty("username", setGamma.props.getPropertyValue("gamma_username"));
        loginCredentials.addProperty("password", setGamma.props.getPropertyValue("gamma_password"));
        setGamma.httpRequest.body(loginCredentials.toString());
        Response response = setGamma.httpRequest.post("/auth");
        String tkn = response.jsonPath().getString("token");        
        provider.dataMap.put("bearerToken", tkn);
        return tkn;
	}
	
	public static void main(String[] args) {
		GenerateBearerToken bearerToken = new GenerateBearerToken();
		System.out.println(bearerToken.getAccessToken());
		
	}
}