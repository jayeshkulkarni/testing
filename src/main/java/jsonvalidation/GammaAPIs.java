package jsonvalidation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.lang3.RandomStringUtils;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class GammaAPIs {

	GenerateBearerToken bearerToken;
	JsonObject projectObj,repoObj;
	RequestSpecification httpRequest;
	String _name = new SimpleDateFormat("ddMMMyy.sss").format(new Date())+"_"+RandomStringUtils.randomAlphanumeric(6);
	Response response;
	String token,prId,repoUID,repoConfigJSON,codeCheckersConfigJSON;
	HashMap<String, String> apiDataMap;
	boolean flag;
	ArrayList<String> scanStates;
	
	public GammaAPIs() {	
		bearerToken = new GenerateBearerToken();
		token = bearerToken.getAccessToken();
		httpRequest = bearerToken.setGamma.httpRequest;
		httpRequest.header("Authorization","Bearer "+token);
		projectObj = new JsonObject();
		repoObj = new JsonObject();
		apiDataMap = bearerToken.provider.dataMap;
	}
	
	public void createProject()
	{	
		projectObj.addProperty("projectName", _name);
		projectObj.addProperty("projectDescription",_name);
		httpRequest.body(projectObj.toString());
        response = httpRequest.post("/projects");
        prId = response.jsonPath().getString("projectId");
        apiDataMap.put("newProjectID", prId);
	}
	

	public void addRepository()
	{
		repoObj.addProperty("repoName", _name);
		repoObj.addProperty("repoUrl", "https://github.com/1shekhar/goldenorb.git");
		repoObj.addProperty("repoType", "git");
		repoObj.addProperty("repoLanguage", "Java");
		repoObj.addProperty("repoBranchOrTag", "refs/heads/master");
		httpRequest.body(repoObj.toString());
		response = httpRequest.post("/repositories");
        apiDataMap.put("newrepoUID", response.jsonPath().getString("repositoryUid"));
	}
	
	public void linkRepository()
	{	    
		repoUID = apiDataMap.get("newrepoUID");
		repoObj.addProperty("repositoryUids", repoUID);
		httpRequest.body(repoObj.toString());
        response = httpRequest.post("/projects/"+prId+"/repositories/link");
	}
	
	public void scanRepository() throws InterruptedException
	{	  
		repoObj.addProperty("repositoryUids", repoUID);
		httpRequest.body(repoObj.toString());
        response = httpRequest.post("/repositories/"+repoUID+"/scan");
        //On-Hold - getScanStatus()- response = httpRequest.get("/repositories/scans");
        //Add Thread.sleep() for now
	}
	
	public Response downloadRepoConfiguration()
	{
		response = httpRequest.get("/repositories/"+repoUID+"/scans/config/download");
		//(temp) put json in map for possible use
		repoConfigJSON = response.getBody().asString();
        apiDataMap.put("repoConfigJSON", repoConfigJSON);
		return response;
	}
	
	public void deleteRepository()
	{	
        response = httpRequest.delete("/repositories/"+repoUID);
	}
	
	public void deleteProject()
	{	
        response = httpRequest.delete("/projects/"+prId);
	}
	
	public Response downloadCodeCheckersConfiguration()
	{
		response = httpRequest.get("/repositories/"+repoUID+"/codecheckers/modules?subsystem_uid="+repoUID);
		response = httpRequest.get("/repositories/"+repoUID+"/codecheckers/rules?moduleId=6");
		response = httpRequest.get("/repositories/"+repoUID+"/codecheckers/download");
		//(temp) put json in map for possible use
		codeCheckersConfigJSON = response.getBody().asString();
        apiDataMap.put("codeCheckersConfigJSON", codeCheckersConfigJSON);
//        System.out.println("codeCheckersConfigJSON: "+codeCheckersConfigJSON);
		return response;
	}
		
	public static void main(String[] args) throws InterruptedException {
		GammaAPIs apIs = new GammaAPIs();

		apIs.createProject();
		apIs.addRepository();
		apIs.linkRepository();
		apIs.scanRepository();
		apIs.downloadRepoConfiguration();
		apIs.downloadCodeCheckersConfiguration();
		apIs.deleteRepository();
		apIs.deleteProject();
	}
}
