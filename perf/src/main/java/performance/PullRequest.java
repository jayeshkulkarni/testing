package performance;

import static performance.HttpUtils.copyDirectory;
import static performance.HttpUtils.httpPost;
import static performance.HttpUtils.createZip;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import groovy.json.JsonException;

public class PullRequest extends GammaAPI implements Callable<Boolean> {

	private static String bitbucketBaseURL = "https://api.bitbucket.org/2.0";

	private static String gitHubBaseURL = "https://api.github.com";
	private Git git;
	private boolean isBitbucket;
	private String repoName;
	private String gitUserName;
	private String gitEmail;
	private String gitPassword;
	private String baseGitPath;
	// private String gitUrl;
	private int numberOfPullRequest;
	// private String gitHubRepoCloneUrl;
	private UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider;

	public PullRequest(String baseUrl, String userName, String password, String gitUrl, String gitUserName,
			String gitPassword, String language, String branch, String projectName, String repoName, String repoType,
			String email, String numberOfPullRequest, boolean isBitbucket) {
		super(baseUrl, userName, password, gitUrl, gitUserName, gitPassword, language, branch, projectName, repoName,
				repoType);
		this.isBitbucket = isBitbucket;
		this.repoName = repoName.toLowerCase();// bitbucket allows only lowercase.
		this.gitUserName = gitUserName;
		this.gitEmail = email;
		this.gitPassword = gitPassword;
		// this.gitUrl = gitUrl;
		this.numberOfPullRequest = Integer.parseInt(numberOfPullRequest);
		if (isBitbucket) {
			this.usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(gitEmail, gitPassword);
		} else {
			this.usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(gitUserName,
					gitPassword);
		}

	}

	private boolean createRepo() {
		try {
			if (isBitbucket) {
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
			} else {
				String apiUrl = gitHubBaseURL + "/user/repos";
				String json = "{\r\n" + "  \"name\": \"" + repoName + "\",\r\n" + "  \"description\": \"" + repoName
						+ "\",\r\n" + "  \"homepage\": \"https://github.com\",\r\n" + "  \"private\": true,\r\n"
						+ "  \"has_issues\": true,\r\n" + "  \"has_projects\": true,\r\n" + "  \"has_wiki\": true\r\n"
						+ "}";
				Response response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitUserName + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				JsonPath jsonpath = new JsonPath(response.getBody().asString());
				gitUrl = jsonpath.getString("clone_url");
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean cloneRepository() {
		if (isBitbucket) {
			return cloneRepository(gitUrl, repoName);
		} else {
			return cloneRepository(gitUrl, repoName);
		}
	}

	private boolean cloneRepository(String gitUrl, String folderName) {
		try {
			File file = new File(folderName);
			if (!file.exists()) {
				file.mkdirs();
				git = Git.cloneRepository().setURI(gitUrl).setCredentialsProvider(usernamePasswordCredentialsProvider)
						.setDirectory(new File(file.getAbsolutePath())).call();
			} else {
				git = Git.open(file);
			}
			baseGitPath = file.getAbsolutePath();
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

	// branchName format to be passed : refs/heads/<branchName>
	private boolean isBranchExists(String branchName) {
		try {
			List<Ref> refs = git.branchList().call();
			for (int i = 0; i < refs.size(); i++) {
				if (refs.get(i).getName().equalsIgnoreCase(branchName)) {
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
			git.branchCreate().setName(branchName).setStartPoint(fromBranch).call();
			return true;
		} catch (GitAPIException e) {
			e.printStackTrace();
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

	private boolean createPullRequest(String branchName, String destinationBranch) {
		try {
			if (isBitbucket) {
				String json = "{\r\n" + "    \"title\": \"" + branchName + "-pullrequest\",\r\n"
						+ "    \"source\": {\r\n" + "        \"branch\": {\r\n" + "            \"name\": \""
						+ branchName + "\"\r\n" + "        }\r\n" + "    },\r\n" + "    \"destination\": {\r\n"
						+ "        \"branch\": {\r\n" + "            \"name\": \"" + destinationBranch + "\"\r\n"
						+ "        }\r\n" + "    }\r\n" + "}";
				String apiUrl = bitbucketBaseURL + "/repositories/" + gitUserName + "/" + repoName + "/pullrequests";
				Response response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitEmail + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				System.out.println("Pullrequest : " + branchName + "-pullrequest" + " created successfully.");
				return true;
			} else {
				String json = "{\r\n" + "  \"title\": \"pullrequest-" + branchName + "\",\r\n"
						+ "  \"body\": \"Please pull this in!\",\r\n" + "  \"head\": \"" + gitUserName + ":"
						+ branchName + "\",\r\n" + "  \"base\": \"" + destinationBranch + "\"\r\n" + "}";
				String apiUrl = gitHubBaseURL + "/repos/" + gitUserName + "/" + repoName + "/pulls";
				Response response = httpPost(apiUrl, json, "Basic "
						+ Base64.getEncoder().encodeToString((gitEmail + ":" + gitPassword).getBytes("utf-8")));
				if (response.getStatusCode() != 201) {
					System.out.println(" Warning : URL: " + apiUrl + " return HTTP Code :" + response.getStatusCode());
					return false;
				}
				System.out.println("Pullrequest : " + branchName + "-pullrequest" + " created successfully.");
				return true;
			}
		} catch (JsonException | UnsupportedEncodingException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Boolean call() throws Exception {
		String sourcePath = null;
		// clone the git which is source of creating new repo and pull request.
		if (cloneRepository(gitUrl, "parent" + repoName)) {
			sourcePath = baseGitPath;
		} else {
			System.out.println("Repository cloning failed for original source code.");
			return false;
		}
		if (createRepo()) {
			if (cloneRepository()) {
				performFirstCommit();// to create master branch
				for (int i = 1; i < (numberOfPullRequest + 1); i++) {
					String branchName="branch" + i;
					createBranch(branchName, "master");
					addFilesToBranchAndCommit(branchName, "automated commit for branch : branchName" + i,
							sourcePath);
					createPullRequest(branchName, "master");
				}
				System.out.println(
						"Pull requests created. Starting phase 2: create repository in gamma and starting gamma scan.");
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
				if (scanRepo()) {
					return true;
				}
			} else {
				System.out.println("Repository cloning failed for newly created repo .");
			}
		} else {
			System.out.println("Repository creation failed.");
		}
		return false;
	}

}
