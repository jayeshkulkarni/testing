package performance;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import interfaces.GitProvider;

public class Github implements GitProvider {

	private String gitHubUserName="saurabhacellere";
	private String gitPassword="Acellere1";

	@Override
	public String getReferenceCommitId(String APIUrl) throws UnsupportedEncodingException {
		
		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		builder.addHeader("Authorization", "Basic "
				+ Base64.getEncoder().encodeToString((gitHubUserName + ":" + gitPassword).getBytes("utf-8")));
		builder.addHeader("Connection", "keep-alive");
		// Setting content type as application/json or application/xml
		builder.setContentType("application/json; charset=UTF-8");

		RequestSpecification requestSpec = builder.build();

		// Making post request with authentication, leave blank in case there
		// are no credentials- basic("","")
		Response response= RestAssured.given().spec(requestSpec).when().get(APIUrl);
		JsonPath jsonpath = new JsonPath(response.getBody().asString());
		return jsonpath.getString("object.sha");
	}

}
