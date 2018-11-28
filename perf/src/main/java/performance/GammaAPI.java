package performance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpHeaders;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import groovy.json.JsonException;

public class GammaAPI implements Callable<Boolean> {
	private static Semaphore semaphore = new Semaphore(1);
	private ConcurrentHashMap<String, String> values = new ConcurrentHashMap<>();
	private String baseUrl;
	private int columnCount = 24;
	private String language;
	private List<String> steps, started_on;
	private Object[] results;
	private ApachePOIExcelWrite apachePOIExcelWrite;
	private String userName;
	private String password;
	private String gitUrl;
	private String branch;
	private String projectName;
	private String repoName;
	private String repoType;
	private String repoUserName;
	private String repoPassword;	
	private boolean incremental;
	private boolean fetchResults;

	public GammaAPI(String baseUrl, ApachePOIExcelWrite apachePOIExcelWrite, String userName, String password,
			String gitUrl,String repoUserName,String repoPassword, String language, String branch, String projectName, String repoName, String repoType,
			boolean incremental, boolean fetchResults) {
		this.baseUrl = baseUrl;
		this.apachePOIExcelWrite = apachePOIExcelWrite;
		this.userName = userName;
		this.password = password;
		this.gitUrl = gitUrl;
		this.repoUserName=repoUserName;
		this.repoPassword=repoPassword;		
		this.language = language;
		this.branch = branch;
		this.projectName = projectName;
		this.repoName = repoName;
		this.repoType = repoType;
		this.incremental = incremental;
		this.fetchResults = fetchResults;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			semaphore.acquire();
			login();
			addProject();
			if (addRepoToProject()) {
				getSubsystems();
				linkProjectwithRepo();
			} else {
				getSubsystemUUID();
			}
			semaphore.release();
			if (!fetchResults) {
				if (scanRepo()) {
					boolean flag = true;
					do {
						flag = isRepoAnalysisFinished();
						Thread.sleep(10000);
					} while (flag);
					if (getRepoAnalysis()) {
						System.out.println("Report generated for repo :" + repoName);
					} else {
						System.out.println("Error occured in report generation of repo :" + repoName);
					}
				} else {
					return false;
				}
			} else {
				if (getLastRepoAnalysis() && getRepoAnalysis()) {
					System.out.println("Report fetched for repo :" + repoName);
				} else {
					System.out.println("Error occured in fetching report  of repo :" + repoName);
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean login() {

		try {
			String apiurl = null;
			apiurl = baseUrl + "/api/v1/auth?username=" + userName + "&password=" + password;
			Response response = httpPost(apiurl,
					"{\"username\":\"" + userName + "\",\"password\":\"" + password + "\"}");
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			values.put("bearerToken", "Bearer " + jsonpath.getString("token"));
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public boolean isProjectExists(String name) {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/gamma/api/projectlist/getprojectlist";
			Response response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			steps = jsonpath.getList("project_id");
			started_on = jsonpath.getList("project_name");
			if (started_on.contains(name)) {
				values.put("projectId", String.valueOf(steps.get(started_on.indexOf(name))));
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public boolean addProject() {
		try {
			if (!isProjectExists(projectName)) {
				String apiUrl = null;
				apiUrl = baseUrl + "/gamma/api/project/addproject";
				Response response = httpPost(apiUrl, "{\r\n" + "	\"project_name\": \"" + projectName + "\",\r\n"
						+ "	\"project_description\": \"" + projectName + "\"\r\n" + "}");
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				values.put("projectId", jsonpath.getString("project_id"));
				return true;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public Double getdiff(String endStep, String startStep) {
		try {
			Date endTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
					.parse(started_on.get(steps.indexOf(endStep)));
			Date startTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
					.parse(started_on.get(steps.indexOf(startStep)));
			Long l = (endTime.getTime() - startTime.getTime());
			if (l > 0) {
				return l.doubleValue() / 1000;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return 0D;
	}

	public boolean getRepoAnalysis() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/gamma/api/analysis/getanalysisqueuedetails?subsystem_id=" + values.get("subsystemId")
					+ "&analysis_id=" + values.get("analysisReqId");
			Response response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			steps = jsonpath.getList("current_step");
			started_on = jsonpath.getList("started_on");
			results = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) {
				results[i] = new Object();
			}
			results[0] = projectName;
			results[1] = repoName;
			results[2] = gitUrl;
			results[3] = language;
			results[4] = getdiff("SCM_EXTRACTION_SUCCESS", "UPDATING_SOURCES");
			results[5] = getdiff("SCANBOX_SCHEDULED_SUCCESS", "SCANBOX_SCHEDULED_STARTED");
			results[6] = getdiff("PARSER_PARSING_SUCCESS", "PARSER_PARSING_START");
			results[7] = getdiff("ANALYSER_PARSING_DATA_PREPROCESS_SUCCESS", "PARSER_PARSING_SUCCESS");
			results[8] = getdiff("ANALYSER_COLLECT_METRICS_SUCCESS", "ANALYSER_PARSING_DATA_PREPROCESS_SUCCESS");
			results[9] = getdiff("UNIT_TESTS_RUN_MODULES_SUCCESS", "UNIT_TESTS_RUN_MODULES_START");
			results[10] = getdiff("CODE_ISSUES_RUN_MODULES_SUCCESS", "CODE_ISSUES_RUN_MODULES_START");
			results[11] = getdiff("RELEVANCE_RUN_MODULES_SUCCESS", "RELEVANCE_RUN_MODULES_START");
			results[12] = getdiff("ANALYSER_RUN_CHECKS_SUCCESS", "RELEVANCE_RUN_MODULES_SUCCESS");
			results[13] = getdiff("ANALYSER_CONSOLIDATION_SUCCESS", "ANALYSER_RUN_CHECKS_SUCCESS");
			results[14] = getdiff("SCANBOX_CLEANUP_SUCCESS", "QUEUED");
			if (getQualityRatings() && values.get("analysisFinalStep").equalsIgnoreCase("SCANBOX_CLEANUP_SUCCESS")) {
				results[15] = values.get("cloneRating");
				results[16] = values.get("codeQualityRating");
				results[17] = values.get("antiPatternRating");
				results[18] = values.get("metricRating");
				results[19] = values.get("overallRating");
				results[20] = "Passed";
				System.out.println("Scan fetch completed for repo : " + repoName);
				results[21] = baseUrl;
				results[22] = userName;
				results[23] = password;
				apachePOIExcelWrite.addResults(results);
				return true;

			} else {
				results[15] = 0;
				results[16] = 0;
				results[17] = 0;
				results[18] = 0;
				results[19] = 0;
				results[20] = "Failed";
				System.out.println("Scan fetch failed for repo : " + repoName);
				results[21] = baseUrl;
				results[22] = userName;
				results[23] = password;
				apachePOIExcelWrite.addResults(results);
				return false;
			}
		} catch (Exception e) {
			System.out.println("Error occured in Scan Analysis for repo : " + repoName);
			System.out.println(e.getMessage());
			return false;
		}
	}

	public boolean getQualityRatings() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/gamma/api/subsystemlist/getsubsystemlist?project_id=" + values.get("projectId");
			// System.out.println(apiurl);
			Response response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			List<String> uiSubsystems = jsonpath.getList("subsystem_name");
			List<String> uiSubsystemId = jsonpath.getList("subsystem_id");

			values.put("uiSubSystemId", String.valueOf(uiSubsystemId.get(uiSubsystems.indexOf(repoName))));

			apiUrl = baseUrl + "/gamma/api/snapshots/getsnapshots?subsystem_uid=" + values.get("subsystemUUId");
			response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			jsonpath = new JsonPath(response.getBody().asString());
			String snapshotId = jsonpath.getString("id");
			snapshotId = snapshotId.substring(1, snapshotId.length() - 1);
			apiUrl = baseUrl + "/gamma/api/breadcrumb/getdata?project_id=" + values.get("uiSubSystemId")
					+ "&node_id=-1&snapshot_id=" + snapshotId.split(",")[0];
			response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			jsonpath = new JsonPath(response.getBody().asString());
			String nodeId = jsonpath.getString("id");
			nodeId = nodeId.substring(1, nodeId.length() - 1);
			apiUrl = baseUrl + "/gamma/api/nodesummary/getnodesummary?project_id=" + values.get("uiSubSystemId")
					+ "&node_id=" + nodeId.split(",")[0] + "&snapshot_id=" + snapshotId.split(",")[0];
			response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			jsonpath = new JsonPath(response.getBody().asString());
			values.put("cloneRating", jsonpath.getString("ratings.cloneRating"));
			values.put("codeQualityRating", jsonpath.getString("ratings.codeQualityRating"));
			values.put("antiPatternRating", jsonpath.getString("ratings.antiPatternRating"));
			values.put("metricRating", jsonpath.getString("ratings.metricRating"));
			values.put("overallRating", jsonpath.getString("ratings.overallRating"));
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean isRepoAnalysisFinished() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/gamma/api/analysis/getLatestAnalysisStatus?subsystem_uid="
					+ values.get("subsystemUUId");
			Response response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			List<String> states = new ArrayList<String>(Arrays.asList("SCANBOX_CLEANUP_SUCCESS", "SCM_EXTRACTION_FAIL",
					"ANALYSER_STARTUP_FAIL", "PARSER_PARSING_FAIL", "SCANBOX_PARSING_FAIL",
					"ANALYSER_COLLECT_METRICS_FAIL", "CLONE_DETECTOR_RUN_MODULES_FAIL", "UNIT_TESTS_RUN_MODULES_FAIL",
					"COVERAGE_RUN_MODULES_FAIL", "CODE_ISSUES_RUN_MODULES_FAIL", "RELEVANCE_RUN_MODULES_FAIL",
					"ANALYSER_CONSOLIDATION_FAIL", "ANALYSER_FAILED", "DATA_PUBLISHING_FAILED",
					"ANALYSER_NOT_REACHABLE", "SCANBOX_NOT_REACHABLE", "FAILED", "FAIL", "ABORTED", "ABORT", "ABORTING",
					"Aborting", "CANCEL", "CANCELLED", "SCANBOX_CLEANUP_FAIL", "SCANBOX_CLEANUP_ABORT",
					"GAMMA_LOC_ERROR", "CONTEXT_NOT_FOUND", "GAMMA_SERVER_ERROR", "JIRA_SERVER_ERROR"));

			if (states.contains(jsonpath.getString("current_step"))) {
				values.put("analysisReqId", jsonpath.getString("analysis_req_id"));
				values.put("analysisFinalStep", jsonpath.getString("current_step"));
				return false;
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return true;
		}
	}

	public boolean getLastRepoAnalysis() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/gamma/api/repository/getsubsystemanalysishistorydata?subsystem_uid="
					+ values.get("subsystemUUId");
			Response response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			if (jsonpath.getList("analysis_req_id").size() != 0) {
				values.put("analysisReqId", jsonpath.getList("analysis_req_id").get(0).toString());
				values.put("analysisFinalStep", "SCANBOX_CLEANUP_SUCCESS");
				return true;
			} else {
				System.out.println("Repo is yet to be scan");
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			values.put("analysisFinalStep", "GAMMA_SERVER_ERROR");
			return false;
		}
	}

	public boolean getSubsystems() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/gamma/api/subsystemlist/getsubsystemlist?project_id=" + values.get("projectId");
			Response response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			List<String> projectSubsystems = jsonpath.getList("subsystem_name");
			if (projectSubsystems != null && projectSubsystems.size() > 0) {
				// System.out.println("subsystems fetched : " +
				// jsonpath.getString("subsystem_id"));
				try {
					apiUrl = baseUrl + "/gamma/api/repository/getsubsystems";
					// System.out.println(apiurl);
					response = httpGet(apiUrl);
					if (response.getStatusCode() != 200) {
						System.out.println(
								" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					}
					jsonpath = new JsonPath(response.getBody().asString());
					List<String> subsystemIds = jsonpath.getList("subsystem_id");
					List<String> subsystemNames = jsonpath.getList("subsystem_name");
					String repoList = String
							.valueOf(subsystemIds.get(subsystemNames.indexOf(projectSubsystems.get(0))));
					for (int i = 1; i < projectSubsystems.size(); i++) {
						repoList += ","
								+ String.valueOf(subsystemIds.get(subsystemNames.indexOf(projectSubsystems.get(i))));
					}
					// System.out.println("final subsystems fetched : " + repoList);
					values.put("subSystemIds", repoList);
					return true;
				} catch (Exception e) {
					System.out.println(e.getMessage());
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return false;
	}

	public boolean getSubsystemUUID() {
		try {
			String apiUrl = baseUrl + "/gamma/api/repository/getsubsystems";
			// System.out.println(apiurl);
			Response response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			List<String> subsystemUIds = jsonpath.getList("subsystem_uid");
			List<String> subsystemNames = jsonpath.getList("subsystem_name");
			List<String> subsystemIds = jsonpath.getList("subsystem_id");
			String subsystemUUId = String.valueOf(subsystemUIds.get(subsystemNames.indexOf(repoName)));
			String subsystemId = String.valueOf(subsystemIds.get(subsystemNames.indexOf(repoName)));
			// System.out.println("subsystemUUId fetched : " + subsystemUUId);
			values.put("subsystemUUId", subsystemUUId);
			values.put("subsystemId", subsystemId);
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public boolean scanRepo() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/gamma/api/analysis/analysesubsystem";
			Response response = httpPost(apiUrl,
					"{\r\n" + "	\"subsystem_uid\": \"" + values.get("subsystemUUId") + "\",\r\n"
							+ "	\"branch_name\": \"" + branch + "\",\r\n" + "	\"snapshot_label\" : \"" +branch
							 + "\",\r\n" + "	\"fast_scan\": \"" + incremental
							+ "\"\r\n" + "}");
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			} else {
				System.out.println("Scan started for repo : " + repoName);
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public boolean linkProjectwithRepo() {
		try {
			String apiurl = null;
			apiurl = baseUrl + "/gamma/api/repository/linksubsystemstoproject";
			// System.out.println(apiurl);
			// String body="project_id="++"subsystem_id=["+values.get("subsystemId")+"]";
			// Building request using requestSpecBuilder
			RequestSpecBuilder builder = new RequestSpecBuilder();

			// Setting API's body
			// builder.setBody(body);
			if (values.get("bearerToken") != null) {
				builder.addHeader(HttpHeaders.AUTHORIZATION, values.get("bearerToken"));
				builder.addHeader("Accept", "*/*");
				builder.addHeader("Connection", "keep-alive");
			}
			// Setting content type as application/json or application/xml
			builder.setContentType("application/x-www-form-urlencoded; charset=UTF-8");

			RequestSpecification requestSpec = builder.build();

			// Making post request with authentication, leave blank in case there
			// are no credentials- basic("","")
			String repoList;
			if (values.get("subSystemIds") == null) {
				repoList = "[" + values.get("subsystemId") + "]";
			} else {
				repoList = "[" + values.get("subSystemIds") + "," + values.get("subsystemId") + "]";
			}
			// System.out.println("subsystems linked : " + repoList);
			Response response = RestAssured.given().spec(requestSpec).formParam("project_id", values.get("projectId"))
					.formParam("subsystem_id", repoList).when().post(apiurl);
			String loginResponse = response.getBody().asString();
			// System.out.println(loginResponse);
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

//	public boolean addRemoteRepoToProject() {
//		try {
//			String apiurl = null;
//			apiurl = baseUrl + "/gamma/api/repository/addsubsystem";
//			String uuid = UUID.randomUUID().toString();
//			String json = "{\r\n" + "	\"subsystem_name\": \"" + repoName + "\",\r\n" + "	\"subsystem_uid\": \""
//					+ uuid + "\",\r\n" + "	\"branch_name\": \"refs/heads/master\",\r\n" + "	\"language_name\": \""
//					+ language + "\",\r\n" + "	\"url\": \"\",\r\n" + "	\"account_type\": \"remote\",\r\n"
//					+ "	\"user_name\": \"\",\r\n" + "	\"password\": \"\",\r\n" + "	\"ssh_key\": \"\",\r\n"
//					+ "	\"ssh_password\": \"\",\r\n" + "	\"account_id\":\"\",\r\n"
//					+ "	\"authentication_mode\": \"P\"\r\n" + "}";
//
//			// Building request using requestSpecBuilder
//			RequestSpecBuilder builder = new RequestSpecBuilder();
//
//			// Setting API's body
//			builder.setBody(json);
//			if (values.get("bearerToken") != null) {
//				builder.addHeader(HttpHeaders.AUTHORIZATION, values.get("bearerToken"));
//				builder.addHeader("Accept", "*/*");
//				builder.addHeader("Connection", "keep-alive");
//			}
//			// Setting content type as application/json or application/xml
//			builder.setContentType("application/json");
//
//			RequestSpecification requestSpec = builder.build();
//
//			// Making post request with authentication, leave blank in case there
//			// are no credentials- basic("","")
//			Response response = RestAssured.given().spec(requestSpec).when().post(apiurl);
//			JsonPath jsonpath = new JsonPath(response.getBody().asString());
//			values.put("subsystemId", jsonpath.getString("subsystem_id"));
//			values.put("subsystemUUId", uuid);
//			values.put("subsystemUUId", uuid);
//			return true;
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			return false;
//		}
//	}


	public boolean addRepoToProject() {
		try {
			String apiurl = null;
			apiurl = baseUrl + "/gamma/api/repository/addsubsystem";
			repoType = repoType.toLowerCase();
			String json = null;
			String uuid = UUID.randomUUID().toString();
			new String(Base64.getEncoder().encodeToString("Acellere@1".getBytes()));
			switch (repoType) {
			case "git":
				json = "{\r\n" + "	\"subsystem_name\": \"" + repoName + "\",\r\n" + "	\"subsystem_uid\": \"" + uuid
						+ "\",\r\n" + "	\"branch_name\": \"refs/heads/master\",\r\n" + "	\"language_name\": \""
						+ language + "\",\r\n" + "	\"url\": \"" + gitUrl + "\",\r\n"
						+ "	\"account_type\": \"git\",\r\n" + "	\"user_name\": \"\",\r\n" + "	\"password\": \"\",\r\n"
						+ "	\"ssh_key\": \"\",\r\n" + "	\"ssh_password\": \"\",\r\n" + "	\"account_id\":\"\",\r\n"
						+ "	\"authentication_mode\": \"P\"\r\n" + "}";
				break;
			case "zip":
				fileUpload(gitUrl);
				json = "{\r\n" + "	\"subsystem_name\": \"" + repoName + "\",\r\n" + "	\"subsystem_uid\": \"" + uuid
						+ "\",\r\n" + "	\"branch_name\": \"" + values.get("originalName") + "\",\r\n"
						+ "	\"language_name\": \"" + language + "\",\r\n" + "	\"url\": \""
						+ values.get("serverCodePath") + "\",\r\n" + "	\"account_type\": \"zip\",\r\n"
						+ "	\"user_name\": \"\",\r\n" + "	\"password\": \"\",\r\n" + "	\"ssh_key\": \"\",\r\n"
						+ "	\"ssh_password\": \"\",\r\n" + "	\"account_id\":\"\",\r\n"
						+ "	\"authentication_mode\": \"P\"\r\n" + "}";
				break;

			case "svn":
				json = "{\r\n" + "	\"subsystem_name\": \"" + repoName + "\",\r\n" + "	\"subsystem_uid\": \"" + uuid
						+ "\",\r\n" + "	\"branch_name\": \"refs/heads/master\",\r\n" + "	\"language_name\": \""
						+ language + "\",\r\n" + "	\"url\": \"" + gitUrl + "\",\r\n"
						+ "	\"account_type\": \"svn\",\r\n" + "	\"user_name\": \"\",\r\n" + "	\"password\": \"\",\r\n"
						+ "	\"ssh_key\": \"\",\r\n" + "	\"ssh_password\": \"\",\r\n" + "	\"account_id\":\"\",\r\n"
						+ "	\"authentication_mode\": \"P\"\r\n" + "}";
				break;

			case "remote":
				json ="{\r\n" + "	\"subsystem_name\": \"" + repoName + "\",\r\n" + "	\"subsystem_uid\": \""
						+ uuid + "\",\r\n" + "	\"branch_name\": \"refs/heads/master\",\r\n" + "	\"language_name\": \""
						+ language + "\",\r\n" + "	\"url\": \"\",\r\n" + "	\"account_type\": \"remote\",\r\n"
						+ "	\"user_name\": \"\",\r\n" + "	\"password\": \"\",\r\n" + "	\"ssh_key\": \"\",\r\n"
						+ "	\"ssh_password\": \"\",\r\n" + "	\"account_id\":\"\",\r\n"
						+ "	\"authentication_mode\": \"P\"\r\n" + "}";
				break;

			default:
				break;
			}

			// Building request using requestSpecBuilder
			RequestSpecBuilder builder = new RequestSpecBuilder();

			// Setting API's body
			builder.setBody(json);
			if (values.get("bearerToken") != null) {
				builder.addHeader(HttpHeaders.AUTHORIZATION, values.get("bearerToken"));
				builder.addHeader("Accept", "*/*");
				builder.addHeader("Connection", "keep-alive");
			}
			// Setting content type as application/json or application/xml
			builder.setContentType("application/json");

			RequestSpecification requestSpec = builder.build();

			// Making post request with authentication, leave blank in case there
			// are no credentials- basic("","")
			Response response = RestAssured.given().spec(requestSpec).when().post(apiurl);
			if (response.getStatusCode() != 200 && response.getStatusCode() != 500) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode());
			}

			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			values.put("subsystemId", jsonpath.getString("subsystem_id"));
			values.put("subsystemUUId", uuid);
			return true;
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			return false;
		}
	}

	public boolean deleteProject() {
		try {

			String apiUrl = null;
			apiUrl = baseUrl + "/gamma/api/project/deleteproject?project_id=" + values.get("projectId");
			Response response = httpGet(apiUrl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public Response httpGet(String APIUrl) throws JsonException, InterruptedException {

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

	public Response httpPost(String APIUrl, String APIBody) throws JsonException, InterruptedException {

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		// Setting API's body
		builder.setBody(APIBody);
		if (values.get("bearerToken") != null) {
			builder.addHeader("Authorization", values.get("bearerToken"));
			builder.addHeader("Connection", "keep-alive");
		}
		// Setting content type as application/json or application/xml
		builder.setContentType("application/json; charset=UTF-8");

		RequestSpecification requestSpec = builder.build();

		// Making post request with authentication, leave blank in case there
		// are no credentials- basic("","")
		return RestAssured.given().spec(requestSpec).when().post(APIUrl);
	}

	public Response httpPut(String APIUrl, String APIBody) throws JsonException, InterruptedException {

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		// Setting API's body
		builder.setBody(APIBody);

		// Setting content type as application/json or application/xml
		builder.setContentType("application/json; charset=UTF-8");

		RequestSpecification requestSpec = builder.build();

		// Making post request with authentication, leave blank in case there
		// are no credentials- basic("","")
		return RestAssured.given().spec(requestSpec).when().put(APIUrl);

	}

	public boolean fileUpload(String filePath) {
		try {
			String apiurl = null;
			apiurl = baseUrl + "/gamma/api/repository/uploadcode";
			RequestSpecBuilder builder = new RequestSpecBuilder();

			// Setting API's body
			// builder.setBody(body);
			if (values.get("bearerToken") != null) {
				builder.addHeader(HttpHeaders.AUTHORIZATION, values.get("bearerToken"));
				builder.addHeader("Accept", "*/*");
				builder.addHeader("Connection", "keep-alive");
			}
			// Setting content type as application/json or application/xml
			builder.setContentType("multipart/form-data; boundary=----WebKitFormBoundarydTJr97I4NsxatX75");

			RequestSpecification requestSpec = builder.build();

			// Making post request with authentication, leave blank in case there
			// are no credentials- basic("","")
			Response response = RestAssured.given().spec(requestSpec).multiPart("fileToUpload", new File(filePath))
					.when().post(apiurl);
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			values.put("serverCodePath", jsonpath.getString("updated_path"));
			values.put("originalName", jsonpath.getString("originalname"));

			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

}
