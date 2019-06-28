package performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GammaBuild {

	public boolean verifyBuild(String refBranchs,String gitUserName,String gitPassword) throws IOException {
		String OS = System.getProperty("os.name").toLowerCase();
		BufferedReader coronaBuid = null, gwsBuild = null, gammauiBuild = null;
		Github github = new Github();
		String coronaGitHubUrl, gammaUIGitHubUrl, gwsGitHubUrl;

		try {
				coronaGitHubUrl = "https://api.github.com/repos/acellere/corona_pg/git/refs/heads/" + refBranchs;
				gammaUIGitHubUrl = "https://api.github.com/repos/acellere/gamma/git/refs/heads/" + refBranchs;
				gwsGitHubUrl = "https://api.github.com/repos/acellere/gws/git/refs/heads/" + refBranchs;
			if ((OS.indexOf("win") >= 0)) {
				coronaBuid = new BufferedReader(
						new FileReader(getAbsolutePathofBuildFile(new File("C:\\ProgramData\\Gamma\\corona"))));
				gwsBuild = new BufferedReader(new FileReader(
						getAbsolutePathofBuildFile(new File("C:\\ProgramData\\Gamma\\tomcat\\webapps\\gws"))));
				gammauiBuild = new BufferedReader(
						new FileReader(getAbsolutePathofBuildFile(new File("C:\\ProgramData\\Gamma\\gamma_ui"))));
			} else {
				coronaBuid = new BufferedReader(
						new FileReader(getAbsolutePathofBuildFile(new File("//opt//gamma//corona"))));
				gwsBuild = new BufferedReader(
						new FileReader(getAbsolutePathofBuildFile(new File("//opt//gamma//tomcat//webapps//gws"))));
				gammauiBuild = new BufferedReader(
						new FileReader(getAbsolutePathofBuildFile(new File("//opt//gamma//gamma_ui"))));
			}
			int count = 0;
			String commitId = github.getReferenceCommitId(gammaUIGitHubUrl,gitUserName,gitPassword);
			System.out.println("GammaUI commitId on github : " + commitId);
			if (isCommitIdPresent(gammauiBuild, commitId)) {
				System.out.println("GammaUI is latest with above commitId.");
				count++;
			} else {
				System.out.println("GammaUI is not updated.");
			}
			commitId = github.getReferenceCommitId(gwsGitHubUrl,gitUserName,gitPassword);
			System.out.println("GWS commitId on github : " + commitId);
			if (isCommitIdPresent(gwsBuild, commitId)) {
				System.out.println("GWS is latest with above commitId.");
				count++;
			} else {
				System.out.println("GWS is not updated.");
			}
			commitId = github.getReferenceCommitId(coronaGitHubUrl,gitUserName,gitPassword);
			System.out.println("Corona commitId on github : " + commitId);
			if (isCommitIdPresent(coronaBuid, commitId)) {
				System.out.println("Corona is latest with above commitId.");
				count++;
			} else {
				System.out.println("Corona is not updated.");
			}
			if (count == 3) {
				System.out.println("Gamma is updated according to respective branches.");
				return true;
			} else {
				System.err.println("Gamma not updated. Please build the jenkins jobs again.");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			coronaBuid.close();
			gwsBuild.close();
			gammauiBuild.close();
		}
	}

	private boolean isCommitIdPresent(BufferedReader bufferedReader, String commitId) throws IOException {
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(commitId)) {
				System.out.println("Commit Match : " + line);
				return true;
			}
		}
		return false;
	}

	private File getAbsolutePathofBuildFile(File directory) {
		for (File file : directory.listFiles()) {
			if (file.getName().contains("buildNumber")) {
				return file;
			}
		}
		return null;
	}
}