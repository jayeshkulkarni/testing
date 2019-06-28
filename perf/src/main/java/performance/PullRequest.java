package performance;

import static performance.HttpUtils.copyDirectory;
import static performance.HttpUtils.httpGet;
import static performance.HttpUtils.httpPost;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;

import groovy.json.JsonException;

public class PullRequest extends GammaAPI implements Callable<Boolean> {

	private static String bitbucketBaseURL = "https://api.bitbucket.org/2.0";

	private static String gitHubBaseURL = "https://api.github.com";
	private Git git;
	private String gitVendor;
	private String repoName;
	private String gitUserName;
	private String gitEmail;
	private String gitPassword;
	private String baseGitPath;
	private String forkParentUserName;
	private List<String> commitsIdsHash = new ArrayList<>();
	private List<String> branches;
	private UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider;
	private int pullRequestCount;

	public PullRequest(String baseUrl, String userName, String password, String gitUrl, String gitUserName,
			String gitPassword, String language, String branch, String projectName, String repoName, String repoType,
			String email, String gitVendor, int pullRequestCount, String commandLineOption) {
		super(baseUrl, userName, password, gitUrl, gitUserName, gitPassword, language, branch, projectName, repoName,
				repoType, commandLineOption);
		this.gitVendor = gitVendor.toLowerCase();
		this.repoName = repoName.toLowerCase();// bitbucket allows only lowercase.
		this.gitUserName = gitUserName;
		this.gitEmail = email;
		this.gitPassword = gitPassword;
		this.pullRequestCount = pullRequestCount;
		switch (gitVendor) {
		case BITBUCKET:
			this.usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(gitEmail, gitPassword);

			break;
		case GITHUB:
			this.usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(gitUserName,
					gitPassword);

			break;

		default:
			System.out.println("Invalid Git Vendors");
			break;
		}
	}

	private boolean createRepo() {
		try {
			switch (gitVendor) {
			case BITBUCKET:
				String apiUrl = bitbucketBaseURL + "/repositories/" + gitUserName + "/" + repoName;
				String json = "{\r\n" + "    \"scm\": \"git\",\r\n" + "    \"is_private\":\"true\",\r\n"
						+ "    \"project\": {\r\n" + "        \"key\": \"PROJ\"\r\n" + "    }\r\n" + "}";
				Response response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitEmail + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				gitUrl = "https://" + gitUserName + "@bitbucket.org/" + gitUserName + "/" + repoName + ".git";
				break;
			case GITHUB:
				apiUrl = gitHubBaseURL + "/user/repos";
				json = "{\r\n" + "  \"name\": \"" + repoName + "\",\r\n" + "  \"description\": \"" + repoName
						+ "\",\r\n" + "  \"homepage\": \"https://github.com\",\r\n" + "  \"private\": true,\r\n"
						+ "  \"has_issues\": true,\r\n" + "  \"has_projects\": true,\r\n" + "  \"has_wiki\": true\r\n"
						+ "}";
				response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitUserName + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				gitUrl = jsonpath.getString("clone_url");
				break;

			default:
				System.out.println("Invalid Git Vendors");
				break;
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean cloneRepository() {
		return cloneRepository(gitUrl, repoName);
	}

	private boolean cloneRepository(String gitUrl, String folderName) {
		try {
			File file = new File(folderName);
			if (file.exists()) {
				System.out.println("Deleting old clone.");
				file.delete();
			}
			if (!file.exists()) {
				file.mkdirs();
				git = Git.cloneRepository().setURI(gitUrl).setCredentialsProvider(usernamePasswordCredentialsProvider)
						.setDirectory(new File(file.getAbsolutePath())).call();
			} else {
				git = Git.open(file);
			}
			baseGitPath = file.getAbsolutePath();
			List<RemoteConfig> remotes = git.remoteList().call();
			for (RemoteConfig remote : remotes) {
				git.fetch().setRemote(remote.getName()).setRefSpecs(remote.getFetchRefSpecs())
						.setCredentialsProvider(usernamePasswordCredentialsProvider).call();
			}
			return true;
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean performFirstCommit(String branchName, String commitMessage, String filesTobeAddedDirectory,
			Git parentGit, Git git) {
		try {
			File source = new File(filesTobeAddedDirectory);
			File destination = new File(baseGitPath);
			copyDirectory(source, destination);
			git.add().addFilepattern(".").call();
			git.commit().setMessage(commitMessage).call();
			git.push().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
			return true;
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean performFirstCommit() {
		try {
			git.add().addFilepattern(".git");
			git.commit().setMessage("First commit").call();
			git.push().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
			return true;
		} catch (GitAPIException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean isBranchExists(String branchName) {
		try {
			List<Ref> refs = git.branchList().call();
			for (int i = 0; i < refs.size(); i++) {
				if (refs.get(i).getName().equalsIgnoreCase("refs/heads/" + branchName)) {
					return true;
				}
			}
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean createBranch(String branchName, String fromBranch) {
		try {
			if (!isBranchExists(branchName)) {
				git.branchCreate().setName(branchName.substring(branchName.lastIndexOf("/") + 1))
						.setStartPoint(fromBranch).call();
			}
			return true;
		} catch (GitAPIException e) {
			// e.printStackTrace();
			return false;
		}
	}

	private boolean createBranchOnCloud(String branchName, String targetHash) {
		try {
			switch (gitVendor) {
			case BITBUCKET:
				String apiUrl = bitbucketBaseURL + "/repositories/" + gitUserName + "/" + repoName + "/refs/branches";
				String json = "{\"name\":\"" + branchName + "\",\"target\":{\"hash\":\"" + targetHash + "\"}}";
				Response response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitEmail + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				break;
			case GITHUB:
				apiUrl = gitHubBaseURL + "/repos/" + gitUserName + "/" + repoName + "/git/refs";
				json = " {\r\n" + "   \"ref\": \"refs/heads/" + branchName + "\",\r\n" + "   \"sha\": \"" + targetHash
						+ "\"\r\n" + " }";
				response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitUserName + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				break;

			default:
				System.out.println("Invalid Git Vendors");
				break;
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean getBranchesFromCloud() {
		try {
			switch (gitVendor) {
			case BITBUCKET:
				String apiUrl = bitbucketBaseURL + "/repositories/" + gitUserName + "/" + repoName + "/refs/branches";
				Response response = httpGet(apiUrl, "Basic "
						+ Base64.getEncoder().encodeToString((gitEmail + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				branches = jsonpath.getList("values.name");

				break;
			case GITHUB:
				apiUrl = gitHubBaseURL + "/repos/" + gitUserName + "/" + repoName + "/branches";
				response = httpGet(apiUrl, "Basic "
						+ Base64.getEncoder().encodeToString((gitUserName + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				jsonpath = new JsonPath(response.getBody().asString());
				branches = jsonpath.getList("name");
				break;

			default:
				System.out.println("Invalid Git Vendors");
				break;
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean addFilesToBranchAndCommit(String branchName, String commitMessage, String filesTobeAddedDirectory) {
		try {
			File source = new File(filesTobeAddedDirectory);
			File destination = new File(baseGitPath);
			git.checkout().setName(branchName).call();
			copyDirectory(source, destination);
			git.add().addFilepattern(".").call();
			git.commit().setMessage(commitMessage).call();
			git.push().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
			return true;
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean addFilesToBranchAndCommit(String branchName, String commitMessage, String filesTobeAddedDirectory,
			Git parentGit, Git git) {
		try {
			File source = new File(filesTobeAddedDirectory);
			File destination = new File(baseGitPath);
			parentGit.branchCreate().setName(branchName.substring(branchName.lastIndexOf("/") + 1))
					.setStartPoint("origin/master").call();
			parentGit.checkout().setName(branchName).setStartPoint("origin/master").call();
			git.checkout().setName(branchName.substring(branchName.lastIndexOf("/") + 1)).call();
			copyDirectory(source, destination);
			git.add().addFilepattern(".").call();
			git.commit().setMessage(commitMessage).call();
			git.push().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
			return true;
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean getCommitIds(String branch) {
		try {
			String apiUrl = null;
			Response response = null;
			JsonPath jsonpath = null;
			switch (gitVendor) {
			case BITBUCKET:
				for (int i = 1; i <= 50; i++) {
					apiUrl = bitbucketBaseURL + "/repositories/" + gitUserName + "/" + repoName + "/commits?page=" + i;
					response = httpGet(apiUrl, "Basic "
							+ Base64.getEncoder().encodeToString((gitEmail + ":" + gitPassword).getBytes("utf-8")));
					if (response.getStatusCode() != 200) {
						System.out.println(
								" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
						return false;
					}
					jsonpath = new JsonPath(response.getBody().asString());
					List<String> commits = jsonpath.getList("values.hash");
					if (commits.size() != 0) {
						commitsIdsHash.addAll(commits);
					} else {
						break;
					}
				}
				break;

			case GITHUB:
				apiUrl = gitHubBaseURL + "/repos/" + gitUserName + "/" + repoName + "/commits";
				response = httpGet(apiUrl, "Basic "
						+ Base64.getEncoder().encodeToString((gitUserName + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				apiUrl = response.getHeader("Link").split(";")[0].replace("<", "").replace(">", "").replace("2", "");
				Headers headers = response.getHeaders();
				jsonpath = new JsonPath(response.getBody().asString());
				List<String> commits = jsonpath.getList("sha");
				commitsIdsHash.addAll(commits);
				for (int i = 2; i < 50; i++) {
					response = httpGet(apiUrl + i, "Basic "
							+ Base64.getEncoder().encodeToString((gitUserName + ":" + gitPassword).getBytes("utf-8")));
					if (response.getStatusCode() != 200) {
						System.out.println(
								" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
						return false;
					}
					jsonpath = new JsonPath(response.getBody().asString());
					commits = jsonpath.getList("sha");
					if (commits.size() != 0) {
						commitsIdsHash.addAll(commits);
					} else {
						break;
					}
				}
				break;
			default:
				System.out.println("Invalid Git Vendors");
				break;
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}

	}

	private boolean createForkRepo() {
		try {
			String[] parts = gitUrl.split("/");
			repoName = parts[parts.length - 1].replace(".git", "");
			forkParentUserName = parts[parts.length - 2];
			switch (gitVendor) {
			case BITBUCKET:
				String apiUrl = bitbucketBaseURL + "/repositories/" + forkParentUserName + "/" + repoName + "/forks";
				String json = "{\r\n" + "    \"scm\": \"git\",\r\n" + "    \"is_private\":true,\r\n"
						+ "    \"project\": {\r\n" + "        \"key\": \"PROJ\"\r\n" + "    }\r\n" + "}";
				Response response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitEmail + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				gitUrl = "https://" + gitUserName + "@bitbucket.org/" + gitUserName + "/" + repoName + ".git";
				break;

			case GITHUB:
				apiUrl = gitHubBaseURL + "/repos/" + forkParentUserName + "/" + repoName + "/forks";
				json = "{\r\n" + "  \"name\": \"" + repoName + "\",\r\n" + "  \"description\": \"" + repoName
						+ "\",\r\n" + "  \"homepage\": \"https://github.com\",\r\n" + "  \"private\": true,\r\n"
						+ "  \"has_issues\": true,\r\n" + "  \"has_projects\": true,\r\n" + "  \"has_wiki\": true\r\n"
						+ "}";
				response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitUserName + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 202) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				gitUrl = jsonpath.getString("clone_url");
				break;

			default:
				System.out.println("Invalid Git Vendors");
				break;
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}

	}

	private boolean createPullRequest(String branchName, String destinationBranch) {
		try {
			switch (gitVendor) {
			case BITBUCKET:
				String json = "{\r\n" + "    \"title\": \"" + branchName + "-" + destinationBranch + "\",\r\n"
						+ "    \"source\": {\r\n" + "        \"branch\": {\r\n" + "            \"name\": \""
						+ branchName + "\"\r\n" + "        }\r\n" + "    },\r\n" + "    \"destination\": {\r\n"
						+ "        \"branch\": {\r\n" + "            \"name\": \"" + destinationBranch + "\"\r\n"
						+ "        }\r\n" + "    }\r\n" + "}";
				String apiUrl = bitbucketBaseURL + "/repositories/" + gitUserName + "/" + repoName + "/pullrequests";
				Response response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitEmail + ":" + gitPassword).getBytes("utf-8")));
				// write small function to process this code
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode()
							+ "Body : " + response.getBody().asString());
					return false;
				}
				System.out.println("Pullrequest : " + branchName + "-" + destinationBranch + "-pullrequest"
						+ " created successfully.");
				break;
			case GITHUB:
				json = "{\r\n" + "  \"title\": \"" + branchName + "-" + destinationBranch + "\",\r\n"
						+ "  \"body\": \"Please pull this in!\",\r\n" + "  \"head\": \"" + gitUserName + ":"
						+ branchName + "\",\r\n" + "  \"base\": \"" + destinationBranch + "\"\r\n" + "}";
				apiUrl = gitHubBaseURL + "/repos/" + gitUserName + "/" + repoName + "/pulls";
				response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitEmail + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode()
							+ "Body : " + response.getBody().asString());
					return false;
				}
				System.out.println("Pullrequest : " + branchName + "-" + destinationBranch + "-pullrequest"
						+ " created successfully.");
				break;
			default:
				System.out.println("Invalid Git Vendors");
				break;
			}
			return true;
		} catch (JsonException | UnsupportedEncodingException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Boolean call() throws Exception {

		createForkRepo();
		Thread.sleep(10000);
		getBranchesFromCloud();
		if (branches == null || branches.size() == 0) {
			System.out.println("No Branches found");
			return false;
		}
		String parentBranch = branches.get(branches.size() - 1);
		getCommitIds(parentBranch);
		if ((commitsIdsHash.size() - 1) < pullRequestCount) {
			System.out.println("Number of Pull requests are more than commits. Creating " + (commitsIdsHash.size() - 1)
					+ " Pull requests.");
			pullRequestCount = commitsIdsHash.size() - 1;
		}
		int noOfcommitsPerBranch = commitsIdsHash.size() / pullRequestCount;
		for (int i = 1; i < pullRequestCount + 1; i++) {
			int commitNumber = i * noOfcommitsPerBranch;
			if (commitNumber >= commitsIdsHash.size()) {
				commitNumber = commitsIdsHash.size() - 1;
			}
			String branchName = "branch_" + commitsIdsHash.get(commitNumber).substring(
					commitsIdsHash.get(commitNumber).length() - 8, commitsIdsHash.get(commitNumber).length() - 1);
			createBranchOnCloud(branchName, commitsIdsHash.get(commitNumber));
			createPullRequest(parentBranch, branchName);
		}

		System.out.println(
				"Pull requests created. Starting phase 2: create repository in gamma and starting gamma scan.");
		semaphore.acquire();
		login();
		addProject();
		addRepoToProject();
		getSubsystems();
		linkProjectwithRepo();
		semaphore.release();
		scanRepo();

		return false;
	}

}
