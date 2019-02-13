package performance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpHeaders;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import groovy.json.JsonException;

public class GammaLicense {
	private String webSitebaseURL;
	private String emailId;
	private String password;
	private String gammaURL;
	private String gammaPassword;
	private ConcurrentHashMap<String, String> values = new ConcurrentHashMap<>();

	public GammaLicense(String webSitebaseURL, String emailId, String password, String gammaURL, String gammaPassword) {
		this.webSitebaseURL = webSitebaseURL;
		this.emailId = emailId;
		this.password = password;
		this.gammaURL = gammaURL;
		this.gammaPassword = gammaPassword;
	}

	private File getLicenceKey() {
		try {
			String apiurl = null;
			apiurl = webSitebaseURL + "/auth/local";

			// Building request using requestSpecBuilder
			RequestSpecBuilder builder = new RequestSpecBuilder();

			// Setting API's body and Headers
			builder.setBody("{\"email\":\"" + emailId + "\",\"password\":\"" + password + "\"}");
			builder.addHeader("Connection", "keep-alive");
			builder.addHeader("Pragma", "no-cache");
			builder.addHeader("Cache-Control", "no-cache");
			builder.addHeader("Accept", "application/json, text/plain, */*");
			builder.addHeader("X-XSRF-TOKEN", "pDqVIZsSNosAxOfAgNRKHmVUe5k+UbN9s2WU8=");
			builder.addHeader("Authorization", "Bearer j:null");
			builder.addHeader("Content-Type", "application/json;charset=UTF-8");
			builder.addHeader("Accept-Encoding", "gzip, deflate, br");
			builder.addHeader("Accept-Language", " en-US,en;q=0.9");
			builder.addHeader("Cookie",
					" _ga=GA1.2.1878668070.1536561851; __stripe_mid=8e98c409-003b-4871-8cd4-16f5b8087b28; intercom-lou-u63bxbgn=1; connect.sid=s%3APTlLQALnuSr33H0Y1j50XKRh0vtKfC42.krVTjM9V536grH8kyifUgdLfW7DuIeJFUYHTceefyr4; rememberMe=true; _gid=GA1.2.1849538432.1548917365; _fbp=fb.1.1548917365932.711717804; __stripe_sid=6f1a4e5c-b4fc-4be8-bd3b-ff2d372c4e9b; _gat=1; token=j%3Anull; XSRF-TOKEN=pDqVIZsSNosAxOfAgNRKHmVUe5k%2BUbN9s2WU8%3D; mp_02bfc5ab2379cd49a3f157cbf074f7a9_mixpanel=%7B%22distinct_id%22%3A%20%22acf594900ad1018a6cc3635c2602b06e%22%2C%22%24initial_referrer%22%3A%20%22%24direct%22%2C%22%24initial_referring_domain%22%3A%20%22%24direct%22%2C%22%24user_id%22%3A%20%22acf594900ad1018a6cc3635c2602b06e%22%2C%22%24had_persisted_distinct_id%22%3A%20true%2C%22%24device_id%22%3A%20%22acf594900ad1018a6cc3635c2602b06e%22%7D; intercom-session-u63bxbgn=ZzRNdUJQbGRrOUFVQWlpeTB4NWNldWhwNERFa0hoUytTRG1DYzBmVysrTU01MTVuODBsV1o1UEkvdW12V0ZFWS0tOHVqb2htbEdPMnBibFg0dHJ2bEMzUT09--ec5e13d43a32fd512a08714ef86c9079cd88d047");

			RequestSpecification requestSpec = builder.build();

			Response response = RestAssured.given().spec(requestSpec).when().post(apiurl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode());
				return null;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			values.put("websiteBearerToken", "Bearer " + jsonpath.getString("token"));
			apiurl = webSitebaseURL + "/api/tenants/set-machine-key";

			// Building request using requestSpecBuilder
			builder = new RequestSpecBuilder();

			// Setting API's body and Headers
			builder.setBody("{\"machine_key\":\"" + values.get("hostKey") + "\"}");
			builder.addHeader("Connection", "keep-alive");
			builder.addHeader("Pragma", "no-cache");
			builder.addHeader("Cache-Control", "no-cache");
			builder.addHeader("Accept", "application/json, text/plain, */*");
			builder.addHeader("X-XSRF-TOKEN", "pDqVIZsSNosAxOfAgNRKHmVUe5k+UbN9s2WU8=");
			builder.addHeader("Authorization", values.get("websiteBearerToken"));
			builder.addHeader("Content-Type", "application/json;charset=UTF-8");
			builder.addHeader("Accept-Encoding", "gzip, deflate, br");
			builder.addHeader("Accept-Language", " en-US,en;q=0.9");
			builder.addHeader("Cookie",
					" _ga=GA1.2.1878668070.1536561851; __stripe_mid=8e98c409-003b-4871-8cd4-16f5b8087b28; intercom-lou-u63bxbgn=1; connect.sid=s%3APTlLQALnuSr33H0Y1j50XKRh0vtKfC42.krVTjM9V536grH8kyifUgdLfW7DuIeJFUYHTceefyr4; rememberMe=true; _gid=GA1.2.1849538432.1548917365; _fbp=fb.1.1548917365932.711717804; __stripe_sid=6f1a4e5c-b4fc-4be8-bd3b-ff2d372c4e9b; _gat=1; token=j%3Anull; XSRF-TOKEN=pDqVIZsSNosAxOfAgNRKHmVUe5k%2BUbN9s2WU8%3D; mp_02bfc5ab2379cd49a3f157cbf074f7a9_mixpanel=%7B%22distinct_id%22%3A%20%22acf594900ad1018a6cc3635c2602b06e%22%2C%22%24initial_referrer%22%3A%20%22%24direct%22%2C%22%24initial_referring_domain%22%3A%20%22%24direct%22%2C%22%24user_id%22%3A%20%22acf594900ad1018a6cc3635c2602b06e%22%2C%22%24had_persisted_distinct_id%22%3A%20true%2C%22%24device_id%22%3A%20%22acf594900ad1018a6cc3635c2602b06e%22%7D; intercom-session-u63bxbgn=ZzRNdUJQbGRrOUFVQWlpeTB4NWNldWhwNERFa0hoUytTRG1DYzBmVysrTU01MTVuODBsV1o1UEkvdW12V0ZFWS0tOHVqb2htbEdPMnBibFg0dHJ2bEMzUT09--ec5e13d43a32fd512a08714ef86c9079cd88d047");

			requestSpec = builder.build();

			response = RestAssured.given().spec(requestSpec).when().post(apiurl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode());
				return null;
			}

			apiurl = webSitebaseURL + "/api/tenants/getlicensekey";

			// Building request using requestSpecBuilder
			builder = new RequestSpecBuilder();

			// Setting Headers
			builder.addHeader("Connection", "keep-alive");
			builder.addHeader("Pragma", "no-cache");
			builder.addHeader("Cache-Control", "no-cache");
			builder.addHeader("Accept", "application/json, text/plain, */*");
			builder.addHeader("X-XSRF-TOKEN", "pDqVIZsSNosAxOfAgNRKHmVUe5k+UbN9s2WU8=");
			builder.addHeader("Authorization", values.get("websiteBearerToken"));
			builder.addHeader("Content-Type", "application/json;charset=UTF-8");
			builder.addHeader("Accept-Encoding", "gzip, deflate, br");
			builder.addHeader("Accept-Language", " en-US,en;q=0.9");
			builder.addHeader("Cookie",
					" _ga=GA1.2.1878668070.1536561851; __stripe_mid=8e98c409-003b-4871-8cd4-16f5b8087b28; intercom-lou-u63bxbgn=1; connect.sid=s%3APTlLQALnuSr33H0Y1j50XKRh0vtKfC42.krVTjM9V536grH8kyifUgdLfW7DuIeJFUYHTceefyr4; rememberMe=true; _gid=GA1.2.1849538432.1548917365; _fbp=fb.1.1548917365932.711717804; __stripe_sid=6f1a4e5c-b4fc-4be8-bd3b-ff2d372c4e9b; _gat=1; token=j%3Anull; XSRF-TOKEN=pDqVIZsSNosAxOfAgNRKHmVUe5k%2BUbN9s2WU8%3D; mp_02bfc5ab2379cd49a3f157cbf074f7a9_mixpanel=%7B%22distinct_id%22%3A%20%22acf594900ad1018a6cc3635c2602b06e%22%2C%22%24initial_referrer%22%3A%20%22%24direct%22%2C%22%24initial_referring_domain%22%3A%20%22%24direct%22%2C%22%24user_id%22%3A%20%22acf594900ad1018a6cc3635c2602b06e%22%2C%22%24had_persisted_distinct_id%22%3A%20true%2C%22%24device_id%22%3A%20%22acf594900ad1018a6cc3635c2602b06e%22%7D; intercom-session-u63bxbgn=ZzRNdUJQbGRrOUFVQWlpeTB4NWNldWhwNERFa0hoUytTRG1DYzBmVysrTU01MTVuODBsV1o1UEkvdW12V0ZFWS0tOHVqb2htbEdPMnBibFg0dHJ2bEMzUT09--ec5e13d43a32fd512a08714ef86c9079cd88d047");

			requestSpec = builder.build();

			response = RestAssured.given().spec(requestSpec).when().post(apiurl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode());
			}
			jsonpath = new JsonPath(response.getBody().asString());
			values.put("licenseKey", jsonpath.getString("license_key"));
			File file = new File("license.txt");
			if (file.exists()) {
				file.delete();
			}
			if (file.createNewFile()) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				bw.write(values.get("licenseKey"));
				bw.close();
			} else {
				System.out.println("Error occured while creating license file. Please note license key for reference : "
						+ values.get("licenseKey"));
				return null;
			}

			return file;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public boolean activateGamma() {
		try {
			String apiurl = null;
			apiurl = gammaURL + "/api/v1/license/get-machine-key";
			Response response = httpGet(apiurl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			values.put("hostKey", jsonpath.getString("machine_key"));
			System.out.println("Host Key : " + jsonpath.getString("machine_key"));
			File licenseKey = getLicenceKey();
			if (islicenseValid(licenseKey)) {
				setUpAccount(licenseKey);
				return true;
			} else {
				System.out.println(" Invalid Licence Key.");
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean setUpAccount(File file) {
		try {
			String apiurl = null;
			apiurl = gammaURL + "/api/v1/license/setup-account";
			RequestSpecBuilder builder = new RequestSpecBuilder();
			builder.addHeader(HttpHeaders.AUTHORIZATION, "Bearer null");
			builder.addHeader("Accept", "*/*");
			builder.addHeader("Connection", "keep-alive");
			builder.addHeader("X-Requested-With", "XMLHttpRequest");
			builder.setContentType("multipart/form-data; boundary=--------------------------739146750418203808470210");
			RequestSpecification requestSpec = builder.build();
			// Making post request with authentication, leave blank in case there
			// are no credentials- basic("","")
			Response response = RestAssured.given().spec(requestSpec)
					.multiPart("license_key", new File(file.getAbsolutePath()), "text/plain")
					.formParam("machine_key", values.get("hostKey")).formParam("firstName", "gamma")
					.formParam("lastName", "gamma").formParam("password", gammaPassword).when().post(apiurl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode() + ":"
						+ response.getBody().asString());
				return false;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			System.out.println("Message : " + jsonpath.getString("message"));
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean islicenseValid(File licenseKey) {
		try {
			String apiurl = null;
			apiurl = gammaURL + "/api/v1/license/validate";
			RequestSpecBuilder builder = new RequestSpecBuilder();

			// Setting API's body
			// builder.setBody(body);
			if (values.get("bearerToken") != null) {
				builder.addHeader(HttpHeaders.AUTHORIZATION, values.get("bearerToken"));
				builder.addHeader("Accept", "*/*");
				builder.addHeader("Connection", "keep-alive");
			}
			// Setting content type as application/json or application/xml
			builder.setContentType("multipart/form-data; boundary=--------------------------954329218642339707048832");

			RequestSpecification requestSpec = builder.build();

			// Making post request with authentication, leave blank in case there
			// are no credentials- basic("","")
			Response response = RestAssured.given().spec(requestSpec)
					.multiPart("license_key", new File(licenseKey.getAbsolutePath()), "text/plain")
					.formParam("machine_key", values.get("hostKey")).when().post(apiurl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode() + ":"
						+ response.getBody().asString());
				return false;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			System.out.println("Message : " + jsonpath.getString("message"));
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private Response httpGet(String APIUrl) throws JsonException, InterruptedException {

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		if (values.get("bearerToken") != null) {
			builder.addHeader("Authorization", values.get("bearerToken"));
			builder.addHeader("Connection", "keep-alive");
		}
		// Setting content type as application/json or application/xml
		builder.setContentType("application/json; charset=UTF-8");

		RequestSpecification requestSpec = builder.build();

		// Making post request with authentication, leave blank in case there
		// are no credentials- basic("","")
		return RestAssured.given().spec(requestSpec).when().get(APIUrl);
	}

}
