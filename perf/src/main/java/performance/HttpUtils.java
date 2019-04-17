package performance;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;



import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import groovy.json.JsonException;

public class HttpUtils {

	public static Response httpGet(String APIUrl, String authToken) throws JsonException, InterruptedException {

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		if (authToken != null) {
			builder.addHeader("Authorization", authToken);
			builder.addHeader("Connection", "keep-alive");
		}
		// Setting content type as application/json or application/xml
		builder.setContentType("application/json; charset=UTF-8");

		RequestSpecification requestSpec = builder.build();

		// Making post request with authentication, leave blank in case there
		// are no credentials- basic("","")
		return RestAssured.given().spec(requestSpec).when().get(APIUrl);
	}

	public static Response httpPost(String APIUrl, String APIBody, String authToken)
			throws JsonException, InterruptedException {

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		// Setting API's body
		builder.setBody(APIBody);
		if (authToken != null) {
			builder.addHeader("Authorization", authToken);
			builder.addHeader("Connection", "keep-alive");
		}
		// Setting content type as application/json or application/xml
		builder.setContentType("application/json; charset=UTF-8");

		RequestSpecification requestSpec = builder.build();

		// Making post request with authentication, leave blank in case there
		// are no credentials- basic("","")
		return RestAssured.given().spec(requestSpec).when().post(APIUrl);
	}

	public static void copyDirectory(File source, File target) {
		if (!target.exists()) {
			target.mkdir();
		}
		CopyOption[] options = new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES };
		for (String f : source.list()) {
			File nestedsource = new File(source, f);
			File nestedtarget = new File(target, f);
			if (nestedsource.isDirectory()) {
				if(!nestedsource.getName().contains(".git")) {
				copyDirectory(nestedsource, nestedtarget);
				}else {
					System.out.println(nestedsource.getName()+" folder skipped from copying.");
				}
			}
			try {
				Files.copy(nestedsource.toPath(), nestedtarget.toPath(), options);
			} catch (IOException e) {
			}
		}
	}

	public static boolean createZip(String sourceDirPath, String zipFilePath) throws IOException {
		Path p = Files.createFile(Paths.get(zipFilePath));
		Path pp = Paths.get(sourceDirPath);
		try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p)); Stream<Path> paths = Files.walk(pp)) {
			paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
				ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
				try {
					zs.putNextEntry(zipEntry);
					Files.copy(path, zs);
					zs.closeEntry();
				} catch (IOException e) {
					System.err.println(e);
				}
			});
			return true;
		}
		catch (Exception e2) {
			System.err.println(e2);
			return false;
		}
	}

	public static boolean cloneRepository(String gitUrl, File file, String userName, String password) {
//		try {
//			Git git;
//			if (!file.exists()) {
//				file.mkdirs();
//				if (userName.length() == 0 || password.length() == 0) {
//					git=Git.cloneRepository().setURI(gitUrl)
//							.setDirectory(new File(file.getAbsolutePath())).call();
//					List<RemoteConfig> remotes = git.remoteList().call();
//					for (RemoteConfig remote : remotes) {
//						git.fetch().setRemote(remote.getName()).setRefSpecs(remote.getFetchRefSpecs())
//								.call();
//					}
//				} else {
//					git=Git.cloneRepository().setURI(gitUrl)
//							.setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName, password))
//							.setDirectory(new File(file.getAbsolutePath())).call();
//					List<RemoteConfig> remotes = git.remoteList().call();
//					for (RemoteConfig remote : remotes) {
//						git.fetch().setRemote(remote.getName()).setRefSpecs(remote.getFetchRefSpecs())
//								.setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName, password)).call();
//					}
//
//				}
//			} else {
//				Git.open(file);
//			}
//			return true;
//		} catch (InvalidRemoteException e) {
//			e.printStackTrace();
//		} catch (TransportException e) {
//			e.printStackTrace();
//		} catch (GitAPIException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return false;
	}

	public static Response httpPut(String APIUrl, String APIBody) throws JsonException, InterruptedException {

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
}
