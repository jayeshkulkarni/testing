package jsonvalidation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.response.Response;

public class JsonValidator {

	JsonObject jValidator;
	Response response;
	GammaAPIs downloadJson = new GammaAPIs();
	String jsonLocation = (".") + File.separator + "src" + File.separator + "test" + File.separator + "resources"
			+ File.separator;

	public Response getAPIResponse() {
		downloadJson.createProject();
		downloadJson.addRepository();
		downloadJson.linkRepository();
		return response;
	}

	// Current file creation logic creates and add data to it. Replace the file if
	// exists and adds data to it.
	public void writeToJsonObjToFile(JsonObject jsonObject, String fileName) {
		try (Writer writer = new FileWriter(jsonLocation + fileName + ".json")) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(jsonObject, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToJsonArrayToFile(String jsonArray) {
		StringBuilder builder = new StringBuilder();
		builder.append(jsonArray);
		jsonArray = builder.toString().replaceAll(System.lineSeparator(), "").replaceAll("\\\\", "");
		try (PrintWriter out = new PrintWriter(jsonLocation + "CodeCheckers.txt")) {
			out.println(jsonArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void readModuleJSON() {
		String rootDir = System.getenv("CORONA_HOME") + File.separator + "auxmod" + File.separator;
		System.out.println(rootDir);
		File dir = new File(rootDir);

		if (dir.exists() && dir.isDirectory()) {
			File arr[] = dir.listFiles();

			System.out.println("**********************************************");
			System.out.println("Files from rootDir : " + dir);
			System.out.println("**********************************************");
			getAllFilesAndFolders(arr, 0);
		}
	}

	static void getAllFilesAndFolders(File[] arr, int level) {
		for (File f : arr) {
			for (int i = 0; i < level; i++)
				System.out.print("\t");

			if (f.isFile())
				System.out.println(f.getName());

			else if (f.isDirectory()) {
				System.out.println("[" + f.getName() + "]");
				getAllFilesAndFolders(f.listFiles(), level + 1);
			}
		}
	}

	public void validateRepositoryConfigJson() {
		this.response = getAPIResponse();
		response = downloadJson.downloadRepoConfiguration();
		jValidator = (new JsonParser()).parse(response.asString()).getAsJsonObject();
		writeToJsonObjToFile(jValidator, "DownloadRepoConfiguration");
		downloadJson.deleteRepository();
		downloadJson.deleteProject();
	}

	public void validateCodeCheckersConfigJson() {
		this.response = getAPIResponse();
		response = downloadJson.downloadCodeCheckersConfiguration();
		String jsonArray = response.getBody().asString();
		// Parse JSON Response which is JSONArray. Save response temporarily in .txt
		// file.
		writeToJsonArrayToFile(jsonArray);
		downloadJson.deleteRepository();
		downloadJson.deleteProject();
	}

	public static void main(String[] args) {
		JsonValidator jsonValidator = new JsonValidator();
		// jsonValidator.validateRepositoryConfigJson();
		// jsonValidator.validateCodeCheckersConfigJson();
		jsonValidator.readModuleJSON();

	}

}
