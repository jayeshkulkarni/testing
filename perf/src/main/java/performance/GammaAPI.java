package performance;

import static performance.HttpUtils.cloneRepository;
import static performance.HttpUtils.createZip;
import static performance.HttpUtils.httpGet;
import static performance.HttpUtils.httpPost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpHeaders;
import org.json.JSONObject;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import groovy.json.JsonException;

public class GammaAPI implements Callable<Boolean> {
	public static final String GIT = "git";
	public static final String ZIP = "zip";
	public static final String SVN = "svn";
	public static final String REMOTE = "remote";
	public static final String GITHUB = "github";
	public static final String BITBUCKET = "bitbucket";
	public static final String WINDOWS_GAMMA_SCANNER = "C:\\ProgramData\\Gamma\\corona\\scanboxwrapper\\bin\\gammascanner.bat";
	public static final String LINUX_GAMMA_SCANNER = "/opt/gamma/corona/scanboxwrapper/bin/gammascanner";
	public static Semaphore semaphore = new Semaphore(1);
	protected ConcurrentHashMap<String, String> values = new ConcurrentHashMap<>();
	private String baseUrl;
	private String language;
	private List<String> steps, repos;
	private Object[] results;
	private ResultWriter resultWriter;
	private String userName;
	private String password;
	protected String gitUrl;
	private String branch;
	private String projectName;
	private String repoName;
	private String repoType;
	private String repoUserName;
	private String repoPassword;
	private boolean incremental;
	private boolean fetchResults;
	private long remoteRepoTimeStamp;
	private String jiraUserName;
	private String jiraPassword;
	private String jiraUrl;
	private String jiraProjectKey;
	private boolean enableTaskInsights;
	private boolean enableRE;
	protected String commandLineOption;
	private File logFileHandler;

	public GammaAPI() {

	}

	public GammaAPI(String baseUrl, String userName, String password, String gitUrl, String repoUserName,
			String repoPassword, String language, String branch, String projectName, String repoName, String repoType,
			String commandLineOption) {
		this.baseUrl = baseUrl;
		this.userName = userName;
		this.password = password;
		this.gitUrl = gitUrl;
		this.repoUserName = repoUserName;
		this.repoPassword = repoPassword;
		this.language = language;
		this.branch = branch;
		this.projectName = projectName;
		this.repoName = repoName;
		this.repoType = repoType;
		this.commandLineOption = commandLineOption;
	}

	public GammaAPI(String baseUrl, ResultWriter resultWriter, String userName, String password, String gitUrl,
			String repoUserName, String repoPassword, String language, String branch, String projectName,
			String repoName, String repoType, boolean incremental, boolean fetchResults, String jiraUserName,
			String jiraPassword, String jiraUrl, String jiraProjectKey, boolean enableTaskInsights, boolean enableRE,
			String commandLineOption) {
		this.baseUrl = baseUrl;
		this.resultWriter = resultWriter;
		this.userName = userName;
		this.password = password;
		this.gitUrl = gitUrl;
		this.repoUserName = repoUserName;
		this.repoPassword = repoPassword;
		this.language = language;
		this.branch = branch;
		this.projectName = projectName;
		this.repoName = repoName;
		this.repoType = repoType;
		this.incremental = incremental;
		this.fetchResults = fetchResults;
		this.jiraUserName = jiraUserName;
		this.jiraPassword = jiraPassword;
		this.jiraUrl = jiraUrl;
		this.jiraProjectKey = jiraProjectKey;
		this.enableTaskInsights = enableTaskInsights;
		this.enableRE = enableRE;
		this.commandLineOption = commandLineOption;
	}

	public GammaAPI(String baseUrl, ResultWriter resultWriter, String userName, String password, String gitUrl,
			String repoUserName, String repoPassword, String language, String branch, String projectName,
			String repoName, String repoType, boolean incremental, boolean fetchResults, String commandLineOption) {
		this.baseUrl = baseUrl;
		this.resultWriter = resultWriter;
		this.userName = userName;
		this.password = password;
		this.gitUrl = gitUrl;
		this.repoUserName = repoUserName;
		this.repoPassword = repoPassword;
		this.language = language;
		this.branch = branch;
		this.projectName = projectName;
		this.repoName = repoName;
		this.repoType = repoType;
		this.incremental = incremental;
		this.fetchResults = fetchResults;
		this.commandLineOption = commandLineOption;
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
					boolean flag = false;
					do {
						flag = isRepoAnalysisFinished();
						if (!flag) {
							Thread.sleep(10000);
						}
					} while (!flag);
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

	protected boolean login() {
		try {
			String apiurl = null;
			apiurl = baseUrl + "/api/v1/auth";
			Response response = httpPost(apiurl,
					"{\"username\":\"" + userName + "\",\"password\":\"" + password + "\"}", values.get("bearerToken"));
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

	private boolean integrateJira() {
		try {
			String apiurl = null;
			apiurl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/jira/validateproject";
			String json = "{\r\n" + "	\"username\": \""
					+ Base64.getEncoder().encodeToString(jiraUserName.getBytes("utf-8")) + "\",\r\n"
					+ "	\"password\": \"" + Base64.getEncoder().encodeToString(jiraPassword.getBytes("utf-8"))
					+ "\",\r\n" + "	\"project_key\": \"" + jiraProjectKey + "\",\r\n" + "	\"host_url\": \"" + jiraUrl
					+ "\",\r\n" + "	\"type\": \"restricted\",\r\n" + "	\"repository_uid\": \""
					+ values.get("subsystemUUId") + "\",\r\n" + "	\"repository_id\": \"" + values.get("subsystemId")
					+ "\",\r\n" + "	\"repository_name\": \"" + repoName + "\",\r\n"
					+ "	\"taskInsightsCheckoxStatus\": \"" + enableTaskInsights + "\" }";
			Response response = httpPost(apiurl, json, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getBody().asString());
				return false;
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean isProjectExists(String name) {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/api/v1/projects?sortBy=projectName&orderBy=ASC&searchTerm=" + name
					+ "&limit=1&offset=0";
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 204 && response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			steps = jsonpath.getList("projectId");
			repos = jsonpath.getList("projectName");
			if (repos.contains(name)) {
				values.put("projectId", String.valueOf(steps.get(repos.indexOf(name))));
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	private boolean isRepoExists() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/api/v1/repositories?sortBy=repositoryName&searchTerm=" + repoName
					+ "&limit=11&offset=0";
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 204 && response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			repos = jsonpath.getList("repositoryName");
			if (repos.contains(repoName)) {
				values.put("subsystemId", jsonpath.getString("repositoryId"));
				values.put("subsystemUUId", jsonpath.getString("repositoryUid"));
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	protected boolean addProject() {
		try {
			if (!isProjectExists(projectName)) {
				String apiUrl = null;
				apiUrl = baseUrl + "/api/v1/projects";
				Response response = null;

				// Building request using requestSpecBuilder
				RequestSpecBuilder builder = new RequestSpecBuilder();

				if (values.get("bearerToken") != null) {
					builder.addHeader("Authorization", values.get("bearerToken"));
					builder.addHeader("Connection", "keep-alive");
				}
				// Setting content type as application/json or application/xml
				builder.setContentType("application/x-www-form-urlencoded; charset=UTF-8");

				RequestSpecification requestSpec = builder.build();

				// Making post request with authentication, leave blank in case there
				// are no credentials- basic("","")
				response = RestAssured.given().spec(requestSpec).when().formParam("projectName", projectName)
						.formParam("projectDescription", projectName).post(apiUrl);

				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				values.put("projectId", jsonpath.getString("projectId"));
				return true;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private Double getdiff(String endStep, String startStep) {
		try {
			Date endTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
					.parse(repos.get(steps.indexOf(endStep)));
			Date startTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
					.parse(repos.get(steps.indexOf(startStep)));
			Long l = (endTime.getTime() - startTime.getTime());
			if (l > 0) {
				return l.doubleValue() / 1000;
			}
		} catch (Exception e) {
		}
		return 0D;
	}

	private boolean getRepoAnalysis() {
		try {
			if (!repoType.equalsIgnoreCase(REMOTE)) {
				String apiUrl = null;
				apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/scans/"
						+ values.get("scanId");
				Response response = httpGet(apiUrl, values.get("bearerToken"));
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				steps = jsonpath.getList("currentStep");
				repos = jsonpath.getList("startTime");
			}
			results = new Object[ResultWriter.columnCount];
			for (int i = 0; i < ResultWriter.columnCount; i++) {
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
			if (repoType.equalsIgnoreCase(REMOTE)) {
				results[14] = remoteRepoTimeStamp / 1000;
			} else {
				results[14] = getdiff("SCANBOX_CLEANUP_SUCCESS", "QUEUED");
			}
			if (values.get("analysisFinalStep").equalsIgnoreCase("SCANBOX_CLEANUP_SUCCESS") && getQualityRatings()
					&& getLocAndComponentCount()) {
				results[15] = values.get("cloneRating");
				results[16] = values.get("codeQualityRating");
				results[17] = values.get("antiPatternRating");
				results[18] = values.get("metricRating");
				results[19] = values.get("overallRating");
				results[20] = values.get("totalLoc");
				results[21] = values.get("loc");
				results[22] = values.get("components");
				results[23] = getCodeIssuesCount();
				results[24] = getDesignIssuesCount();
				results[25] = getDuplicationLocCount();

				results[26] = "Passed";
				System.out.println("Scan fetch completed for repo : " + repoName);
				results[27] = baseUrl;
				results[28] = userName;
				results[29] = password;
				results[30] = values.get("subsystemUUId");
				resultWriter.addResults(results);
				return true;

			} else {
				results[15] = 0;
				results[16] = 0;
				results[17] = 0;
				results[18] = 0;
				results[19] = 0;
				results[20] = 0;
				results[21] = 0;
				results[22] = 0;
				results[23] = 0;
				results[24] = 0;
				results[25] = 0;
				results[26] = "Failed";
				System.out.println("Scan fetch failed for repo : " + repoName);
				results[27] = baseUrl;
				results[28] = userName;
				results[29] = password;
				results[30] = values.get("subsystemUUId");
				resultWriter.addResults(results);
				return false;
			}
		} catch (Exception e) {
			System.out.println("Error occured in Scan Analysis for repo : " + repoName);
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean getQualityRatings() {
		try {
			String apiUrl = null;

			apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/ratings";
			// System.out.println(apiurl);
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			values.put("cloneRating", jsonpath.getString("cloneRating"));
			values.put("codeQualityRating", jsonpath.getString("codeQualityRating"));
			values.put("antiPatternRating", jsonpath.getString("antiPatternRating"));
			values.put("metricRating", jsonpath.getString("metricRating"));
			values.put("overallRating", jsonpath.getString("overallRating"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isRepoAnalysisFinished() {
		if (repoType.equalsIgnoreCase(REMOTE)) {
			try {
				return isRemoteScanFinished();
			} catch (IOException e) {
				return true;
			}
		} else {
			try {
				String apiUrl = null;
				apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/scans/"
						+ values.get("scanId");
				Response response = httpGet(apiUrl, values.get("bearerToken"));
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				List<String> states = new ArrayList<String>(Arrays.asList("SCANBOX_CLEANUP_SUCCESS",
						"SCM_EXTRACTION_FAIL", "ANALYSER_STARTUP_FAIL", "PARSER_PARSING_FAIL", "SCANBOX_PARSING_FAIL",
						"ANALYSER_COLLECT_METRICS_FAIL", "CLONE_DETECTOR_RUN_MODULES_FAIL",
						"UNIT_TESTS_RUN_MODULES_FAIL", "COVERAGE_RUN_MODULES_FAIL", "CODE_ISSUES_RUN_MODULES_FAIL",
						"RELEVANCE_RUN_MODULES_FAIL", "ANALYSER_CONSOLIDATION_FAIL", "ANALYSER_FAILED",
						"DATA_PUBLISHING_FAILED", "ANALYSER_NOT_REACHABLE", "SCANBOX_NOT_REACHABLE", "FAILED", "FAIL",
						"ABORTED", "ABORT", "ABORTING", "Aborting", "CANCEL", "CANCELLED", "SCANBOX_CLEANUP_FAIL",
						"SCANBOX_CLEANUP_ABORT", "GAMMA_LOC_ERROR", "CONTEXT_NOT_FOUND", "GAMMA_SERVER_ERROR",
						"JIRA_SERVER_ERROR", "CONTEXT_NOT_FOUND"));
				List<String> currentStates = jsonpath.getList("currentStep");
				if (currentStates != null) {
					for (int i = 0; i < currentStates.size(); i++) {
						if (states.contains(currentStates.get(i))) {
							values.put("analysisFinalStep", currentStates.get(i));
							return true;
						}
					}
				}
				return false;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}
	}

	private boolean getLastRepoAnalysis() {
		if (repoType.equalsIgnoreCase(REMOTE)) {
			values.put("analysisFinalStep", "SCANBOX_CLEANUP_SUCCESS");
			return true;
		} else {
			try {
				String apiUrl = null;
				apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId")
						+ "/scans?sortBy=startTime&orderBy=desc";
				Response response = httpGet(apiUrl, values.get("bearerToken"));
				if (response.getStatusCode() != 200) {
					System.out.println("Last repoanalysis:" + " Warning : URL: " + apiUrl + " return HTTP Code :"
							+ response.getStatusCode());
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				if (jsonpath.getList("scanStatus").size() > 0) {
					values.put("scanId", jsonpath.getList("scanId").get(0).toString());
					values.put("analysisFinalStep", jsonpath.getList("endMessage").get(0).toString());
					return true;
				} else {
					System.out.println("Repo is yet to be scan");
					return false;
				}
			} catch (Exception e) {
				System.out.println("Last repoanalysis:" + e.getMessage());
				values.put("analysisFinalStep", "GAMMA_SERVER_ERROR");
				return false;
			}
		}
	}

	protected boolean getSubsystems() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/api/v1/projects/" + values.get("projectId")
					+ "/repositories?sortBy=repositoryName&orderBy=ASC&searchTerm=&offset=0&limit=1000";
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 204 && response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			} else if (response.getStatusCode() == 204) {
				return false;
			} else {
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				List<String> projectSubsystems = jsonpath.getList("uid");
				if (projectSubsystems != null && projectSubsystems.size() > 0) {
					String repoList = String.valueOf(projectSubsystems.get(0));
					for (int i = 1; i < projectSubsystems.size(); i++) {
						repoList += "," + projectSubsystems.get(i);
					}
					values.put("subSystemIds", repoList);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return false;
	}

	private boolean getSnapshotId() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/snapshots";
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			List<String> snapshotIds = jsonpath.getList("id");
			List<String> subsystemIds = jsonpath.getList("subsystemId");
			values.put("snapshotId", String.valueOf(snapshotIds.get(snapshotIds.size() - 1)));
			values.put("subsystemId", String.valueOf(subsystemIds.get(snapshotIds.size() - 1)));
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean getVersionControlAccount(String accountType) {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/api/v1/versioncontrolaccounts";
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			List<String> accountNames = jsonpath.getList("accountName");
			List<String> accountIds = jsonpath.getList("accountId");
			if (accountIds.size() > 0 && accountNames.contains(repoUserName + accountType)) {
				values.put("accountId",
						String.valueOf(accountIds.get(accountNames.indexOf(repoUserName + accountType))));
				System.out.println("Version control with Account Name already exists :" + repoUserName + accountType);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean addVersionControlAccount(String json) {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/api/v1/versioncontrolaccounts";
			Response response = httpPost(apiUrl, json, values.get("bearerToken"));
			if (response.getStatusCode() != 201) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			System.out.println("Version control created with Account Name  :" + repoUserName);
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean getLocAndComponentCount() {
		try {
			getSnapshotId();
			String apiUrl = baseUrl + "/api/views/repositories/" + values.get("subsystemUUId")
					+ "/breadcrumb?repositoryId=" + values.get("subsystemId") + "&nodeId=-1&snapshotId="
					+ values.get("snapshotId");

			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			List<String> nodeIds = jsonpath.getList("id");
			values.put("nodeId", String.valueOf(nodeIds.get(0)));
			apiUrl = baseUrl + "/api/views/repositories/" + values.get("subsystemUUId")
					+ "/summary/locandcomponents?repositoryId=" + values.get("subsystemId") + "&nodeId="
					+ values.get("nodeId") + "&snapshotId=" + values.get("snapshotId");
			response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			jsonpath = new JsonPath(response.getBody().asString());
			values.put("totalLoc", String.valueOf(jsonpath.getInt("total_loc")));
			values.put("loc", String.valueOf(jsonpath.getInt("loc")));
			values.put("components", String.valueOf(jsonpath.getInt("components")));
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	protected boolean getSubsystemUUID() {
		try {
			String apiUrl = baseUrl + "/api/v1/projects/" + values.get("projectId")
					+ "/repositories?sortBy=repositoryName&orderBy=ASC&searchTerm=" + repoName + "&offset=0&limit=1";
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			List<String> subsystemUIds = jsonpath.getList("uid");
			List<String> subsystemIds = jsonpath.getList("repositoryId");
			values.put("subsystemUUId", subsystemUIds.get(0));
			values.put("subsystemId", String.valueOf(subsystemIds.get(0)));
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	protected boolean scanRepo() {
		if (enableRE) {
			integrateJira();
		}
		if (repoType.equalsIgnoreCase(REMOTE)) {
			return startRemoteScan();
		} else {
			try {
				String apiUrl = null;
				apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/scan";
				Response response = httpPost(apiUrl,
						"{\r\n" + "	\"commitId\": \"\",\r\n" + "	\"repoBranchOrTag\": \"" + branch + "\",\r\n"
								+ "	\"snapshotLabel\" : \"" + branch + "\",\r\n" + "	\"enableFastScan\": \""
								+ incremental + "\"\r\n" + "}",
						values.get("bearerToken"));
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				} else {
					System.out.println("Scan started for repo : " + repoName);
				}
				apiUrl = baseUrl + "/api/v1/repositories/scans";
				response = httpGet(apiUrl, values.get("bearerToken"));
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					System.out.println("Scan halted for repo : " + repoName);
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				List<String> repoUids = jsonpath.getList("repoUid");
				List<String> scanIds = jsonpath.getList("scanId");

				values.put("scanId", scanIds.get(repoUids.indexOf(values.get("subsystemUUId"))));

				return true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}

	}

	protected boolean linkProjectwithRepo() {
		try {
			String apiurl = null;
			apiurl = baseUrl + "/api/v1/projects/" + values.get("projectId") + "/repositories/link";
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
			String repoList = "";
			if (values.get("subSystemIds") == null) {
				repoList = values.get("subsystemUUId");
			} else {
				repoList = values.get("subSystemIds") + "," + values.get("subsystemUUId");
			}
			Response response = RestAssured.given().spec(requestSpec).formParam("repositoryUids", repoList).when()
					.post(apiurl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	protected boolean addRepoToProject() {
		try {
			if (!isRepoExists()) {
				String apiurl = null;
				apiurl = baseUrl + "/api/v1/repositories";
				repoType = repoType.toLowerCase();
				String json = null;
				switch (repoType) {
				case GIT:
					json = "{\r\n" + "	\"repoName\": \"" + repoName + "\",\r\n" + "	\"repoBranchOrTag\": \""
							+ branch + "\",\r\n" + "	\"repoLanguage\": \"" + language + "\",\r\n"
							+ "	\"repoUrl\": \"" + gitUrl + "\",\r\n" + "	\"repoType\": \"git\",\r\n"
							+ "	\"username\": \"" + repoUserName + "\",\r\n" + "	\"password\": \"" + repoPassword
							+ "\",\r\n" + "	\"sshKey\": \"\",\r\n" + "	\"sshKeyPassword\": \"\",\r\n"
							+ "	\"accountId\":\"\",\r\n" + "	\"authMode\": \"P\"\r\n" + "}";
					break;

				case ZIP:
					createAndUploadZipFile();
					json = "{\r\n" + "	\"repoName\": \"" + repoName + "\",\r\n" + "	\"repoBranchOrTag\": \""
							+ values.get("originalName") + "\",\r\n" + "	\"repoLanguage\": \"" + language + "\",\r\n"
							+ "	\"repoUrl\": \"" + values.get("serverCodePath") + "\",\r\n"
							+ "	\"repoType\": \"zip\",\r\n" + "	\"username\": \"" + repoUserName + "\",\r\n"
							+ "	\"password\": \"" + repoPassword + "\",\r\n" + "	\"sshKey\": \"\",\r\n"
							+ "	\"sshKeyPassword\": \"\",\r\n" + "	\"accountId\":\"\",\r\n"
							+ "	\"authMode\": \"P\"\r\n" + "}";
					break;

				case SVN:
					json = "{\r\n" + "	\"repoName\": \"" + repoName + "\",\r\n" + "	\"repoBranchOrTag\": \""
							+ branch + "\",\r\n" + "	\"repoLanguage\": \"" + language + "\",\r\n"
							+ "	\"repoUrl\": \"" + gitUrl + "\",\r\n" + "	\"repoType\": \"svn\",\r\n"
							+ "	\"username\": \"" + repoUserName + "\",\r\n" + "	\"password\": \"" + repoPassword
							+ "\",\r\n" + "	\"sshKey\": \"\",\r\n" + "	\"sshKeyPassword\": \"\",\r\n"
							+ "	\"accountId\":\"\",\r\n" + "	\"authMode\": \"P\"\r\n" + "}";
					break;

				case REMOTE:
					json = "{\r\n" + "	\"repoName\": \"" + repoName + "\",\r\n" + "	\"repoBranchOrTag\": \"\",\r\n"
							+ "	\"repoLanguage\": \"" + language + "\",\r\n" + "	\"repoUrl\": \"\",\r\n"
							+ "	\"repoType\": \"remote\",\r\n" + "	\"username\": \"\",\r\n"
							+ "	\"password\": \"\",\r\n" + "	\"sshKey\": \"\",\r\n"
							+ "	\"sshKeyPassword\": \"\",\r\n" + "	\"accountId\":\"\",\r\n"
							+ "	\"authMode\": \"P\"\r\n" + "}";
					break;
				case GITHUB:

					if (!getVersionControlAccount("_github")) {
						json = "{\r\n" + "	\"accountName\": \"" + repoUserName + "_github\",\r\n"
								+ "	\"accountType\": \"1\",\r\n" + "	\"accountTypeName\": \"Github\",\r\n"
								+ "	\"accountUrl\": \"https://github.com/\",\r\n" + "	\"userName\": \"\",\r\n"
								+ "	\"pat\": \"" + repoPassword + "\"" + "}";
						if (!addVersionControlAccount(json)) {
							return false;
						} else {
							getVersionControlAccount("_github");
						}
					}

					json = "{\r\n" + "	\"repoName\": \"" + repoName + "\",\r\n	\"repoBranchOrTag\": \"" + branch
							+ "\",\r\n" + "	\"repoLanguage\": \"" + language + "\",\r\n" + "	\"repoUrl\": \""
							+ gitUrl + "\",\r\n" + "	\"repoType\": \"Github\",\r\n" + "	\"username\": \"\",\r\n"
							+ "	\"password\": \"\",\r\n" + "	\"sshKey\": \"\",\r\n"
							+ "	\"sshKeyPassword\": \"\",\r\n" + "	\"accountId\":\"" + values.get("accountId")
							+ "\",\r\n" + "	\"authMode\": \"P\"\r\n" + "}";

					break;

				case BITBUCKET:

					if (!getVersionControlAccount("_bitbucket")) {
						json = "{\r\n" + "	\"accountName\": \"" + repoUserName + "_bitbucket\",\r\n"
								+ "	\"accountType\": \"2\",\r\n" + "	\"accountTypeName\": \"Bitbucket\",\r\n"
								+ "	\"accountUrl\": \"https://bitbucket.org/\",\r\n" + "	\"userName\": \""
								+ repoUserName + "\",\r\n" + "	\"pat\": \"" + repoPassword + "\"" + "}";
						if (!addVersionControlAccount(json)) {
							return false;
						} else {
							getVersionControlAccount("_bitbucket");
						}
					}

					json = "{\r\n" + "	\"repoName\": \"" + repoName + "\",\r\n	\"repoBranchOrTag\": \"" + branch
							+ "\",\r\n" + "	\"repoLanguage\": \"" + language + "\",\r\n" + "	\"repoUrl\": \""
							+ gitUrl + "\",\r\n" + "	\"repoType\": \"Bitbucket\",\r\n" + "	\"username\": \"\",\r\n"
							+ "	\"password\": \"\",\r\n" + "	\"sshKey\": \"\",\r\n"
							+ "	\"sshKeyPassword\": \"\",\r\n" + "	\"accountId\":\"" + values.get("accountId")
							+ "\",\r\n" + "	\"authMode\": \"P\"\r\n" + "}";
					break;

				default:
					break;
				}
				if (commandLineOption.equalsIgnoreCase("-pr")) {
					json = json.replace("}", ", \"prEnable\": \"true\" }");
				}
				if (commandLineOption.equalsIgnoreCase("-re")) {
					json = json.replace("}", ", \"reEnable\": \"true\" }");
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
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}

				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				values.put("subsystemId", jsonpath.getString("repositoryId"));
				values.put("subsystemUUId", jsonpath.getString("repositoryUid"));
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	private int getDuplicationLocCount() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/duplication";
			Response response = httpGet(apiUrl, values.get("bearerToken"));

			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			return (jsonpath.getInt("duplicate_loc"));
		} catch (JsonException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	private int getDesignIssuesCount() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/designissues/count";
			Response response = httpGet(apiUrl, values.get("bearerToken"));

			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			return (jsonpath.getInt("componentDesignIssues") + jsonpath.getInt("subcomponentDesignIssues"));
		} catch (JsonException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	private int getCodeIssuesCount() {
		try {
			String apiUrl = null;
			apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/codeissues/count";
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			return (jsonpath.getInt("codeIssues"));
		} catch (JsonException | InterruptedException e) {
			return 0;
		}
	}

	private boolean deleteProject() {
		try {

			String apiUrl = baseUrl + "/gamma/api/project/deleteproject?project_id=" + values.get("projectId");
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
				return false;
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean createAndUploadZipFile() {
		try {
			File sourcePath = new File(repoName);
			File zipPath = new File(repoName + ".zip");

			if (cloneRepository(gitUrl, sourcePath, userName, password)) {
				System.out.println("clonning successfull for zip creation : " + gitUrl);
			} else {
				System.out.println("clonning failed for zip creation : " + gitUrl);
			}
			if (createZip(sourcePath.getAbsolutePath(), zipPath.getAbsolutePath())) {
				System.out.println("zip creation done : " + gitUrl);
			} else {
				System.out.println("zip creation failed : " + gitUrl);
			}

			String apiurl = null;
			apiurl = baseUrl + "/api/v1/repositories/upload";
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
					.multiPart("fileToUpload", zipPath, "application/zip").when().post(apiurl);
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode() + ":"
						+ response.getBody().asString());
			}
			JsonPath jsonpath = new JsonPath(response.getBody().asString());
			values.put("serverCodePath", jsonpath.getString("fileUploadPath"));
			values.put("originalName", jsonpath.getString("fileName"));
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean isRemoteScanFinished() throws IOException {
		BufferedReader br = null;
		try {
			String successMessage = "Publish data notification sent successfully to GAMMA.";
			br = new BufferedReader(new FileReader(logFileHandler));
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.contains(successMessage)) {
					remoteRepoTimeStamp = (Calendar.getInstance().getTimeInMillis() - remoteRepoTimeStamp);
					values.put("analysisFinalStep", "SCANBOX_CLEANUP_SUCCESS");
					return true;
				}
				if (line.contains("Process exited")) {
					remoteRepoTimeStamp = (Calendar.getInstance().getTimeInMillis() - remoteRepoTimeStamp);
					values.put("analysisFinalStep", "FAILED");
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return true;
		} finally {
			br.close();
		}
	}

	private boolean startRemoteScan() {
		try {
			File sourcePath = new File(repoName);
			cloneRepository(gitUrl, sourcePath, repoUserName, repoPassword);
			File dataDirPath = new File("Data-" + repoName);
			String apiUrl = baseUrl + "/api/v1/repositories/" + values.get("subsystemUUId") + "/scans/config/download";
			Response response = httpGet(apiUrl, values.get("bearerToken"));
			if (response.getStatusCode() != 200) {
				System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
			}
			JSONObject jsonObject = new JSONObject(response.getBody().asString());
			JSONObject gammaAccess = jsonObject.getJSONObject("gammaAccess");
			gammaAccess.put("url", baseUrl);
			gammaAccess.put("userName", userName);
			gammaAccess.put("password", password);
			JSONObject repositories = jsonObject.getJSONArray("repositories").getJSONObject(0);
			repositories.put("dataDir", sourcePath.getAbsoluteFile());
			JSONObject sources = repositories.getJSONObject("repository").getJSONObject("sources");
			sources.put("baseDir", dataDirPath.getAbsolutePath());
			String OS = System.getProperty("os.name").toLowerCase();
			String fileName = null, gammaScanner = null, logFile = null;
			if ((OS.indexOf("win") >= 0)) {
				gammaScanner = WINDOWS_GAMMA_SCANNER;
				fileName = sourcePath.getAbsolutePath() + "\\gamma.json";
				logFile = sourcePath.getAbsolutePath() + "\\scan.log";
			} else {
				gammaScanner = LINUX_GAMMA_SCANNER;
				fileName = sourcePath.getAbsolutePath() + "/gamma.json";
				logFile = sourcePath.getAbsolutePath() + "/scan.log";
			}
			logFileHandler = new File(logFile);
			if (logFileHandler.exists()) {
				logFileHandler.delete();
			}
			// create log file
			logFileHandler.createNewFile();
			// Write into the file
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)));
			bw.write(jsonObject.toString());
			bw.close();
			System.out.println("Successfully updated gamma config json object to file at path : " + fileName + "...!!");
			System.out.println("Note log file path : " + logFile + "...!!");
			// Start the scan
			String[] cmdArray = new String[2];

			cmdArray[0] = "-c";

			// second argument is a gamma json file
			cmdArray[1] = fileName;

			ProcessExec.executeProcessSync(gammaScanner, cmdArray, logFile);

			System.out.println("Remote scan started for repo : " + repoName);
			remoteRepoTimeStamp = Calendar.getInstance().getTimeInMillis();
			Thread.sleep(100);
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

}
