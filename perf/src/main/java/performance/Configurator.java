package performance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class Configurator {

	public File createDefaultConfigFile(String fileName, String args[]) {
		BufferedWriter bufferedWriter = null;
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			File file = null;
			if ((OS.indexOf("win") >= 0)) {
				file = new File(System.getProperty("user.dir") + "\\" + fileName);
			} else {
				file = new File(System.getProperty("user.dir") + "//" + fileName);
			}
			String prefix = args[1] + "," + args[2] + "," + args[3] + ",";
			String repoPostFix=args[5];
			Calendar calendar = Calendar.getInstance();
			String projectName = "Sanity_" + calendar.get(Calendar.DAY_OF_MONTH) + "_"
					+ calendar.get(Calendar.HOUR_OF_DAY);
			bufferedWriter = new BufferedWriter(new FileWriter((file)));
			bufferedWriter.write(
					"//GIT REPO FORMAT ==> GAMMA_URL,GAMMA_USERNAME,GAMMA_PASSWORD,GIT_URL,GIT_USERNAME,GIT_PASSWORD,LANGUAGE,BRANCH/TAG,PROJECTNAME,REPONAME,GIT|SVN|ZIP|REMOTE,INCREMENTAL");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/carnegiehall/linked-data.git,,,python,master," + projectName
					+ ",python_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/go.git,,,go,v1.1.5," + projectName
					+ ",jsongo_zip_"+repoPostFix+",zip," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "http://svn.apache.org/repos/asf/nutch/trunk,,,java,1.0.9," + projectName
					+ ",nutch_svn_"+repoPostFix+",svn," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/libsodium.git,,,cpp,1.0.9," + projectName
					+ ",libsodium_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/go.git,,,go,v1.1.5," + projectName
					+ ",jsongo_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/goldenorb,,,java,master," + projectName
					+ ",goldenOrb_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/BrowserQuest.git,,,javascript,master,"
					+ projectName + ",browserquest_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/JSPatch.git,,,objective_c,1.1.3," + projectName
					+ ",JSPatch_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/php,,,php,master," + projectName
					+ ",symphony_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/ts_mobx,,,typescript,5.1.0," + projectName
					+ ",mobx_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/kotlin_anko.git,,,kotlin,v0.9.1," + projectName
					+ ",anko_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/utPLSQL.git,,,sql,v3.1.2," + projectName
					+ ",utplsql_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/pyrrha-consensus.git,,,solidity,v0.5.0,"
					+ projectName + ",pyrrha_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/D3-RP.git,,,c_sharp,v0.97," + projectName
					+ ",D3-RP_"+repoPostFix+",git," + args[4]);
			bufferedWriter.newLine();
			return file;
		} catch (Exception e) {
			System.out.println("Exception occured while creation of default config file." + e.getMessage());
			return null;
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				System.out.println("Exception occured while closing file." + e.getMessage());
			}
		}
	}

	public File createDefaultIncrementalConfigFile(String fileName, String args[]) {
		BufferedWriter bufferedWriter = null;
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			File file = null;
			if ((OS.indexOf("win") >= 0)) {
				file = new File(System.getProperty("user.dir") + "\\" + fileName);
			} else {
				file = new File(System.getProperty("user.dir") + "//" + fileName);
			}
			String prefix = args[1] + "," + args[2] + "," + args[3] + ",";
			String repoPostFix=args[4];
			Calendar calendar = Calendar.getInstance();
			String projectName = "Incremental_Sanity_" + calendar.get(Calendar.DAY_OF_MONTH) + "_"
					+ calendar.get(Calendar.HOUR_OF_DAY);
			bufferedWriter = new BufferedWriter(new FileWriter((file)));
			bufferedWriter.write(
					"//GIT REPO FORMAT ==> GAMMA_URL,GAMMA_USERNAME,GAMMA_PASSWORD,GIT_URL,GIT_USERNAME,GIT_PASSWORD,LANGUAGE,BRANCH/TAG,PROJECTNAME,REPONAME,GIT,INCREMENTAL");
			bufferedWriter.newLine();
			bufferedWriter.write(
					prefix + "https://github.com/1shekhar/go.git,,,go,v1.1.5," + projectName + ",jsongo_i_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/vikrant-phadtare1101/pad.git,,,java_i,master," + projectName
					+ ",goldenOrb_i_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(
					prefix + "https://github.com/1shekhar/php,,,php,master," + projectName + ",symphony_i_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/kotlin_anko.git,,,kotlin,v0.9.1," + projectName
					+ ",anko_i_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/utPLSQL.git,,,sql,v3.1.2," + projectName
					+ ",utplsql_i_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/pyrrha-consensus.git,,,solidity,v0.5.0,"
					+ projectName + ",pyrrha_i_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/D3-RP.git,,,c_sharp,v0.97," + projectName
					+ ",D3-RP_i_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(
					prefix + "https://github.com/1shekhar/go.git,,,go,1.1.4," + projectName + ",jsongo_i_"+repoPostFix+",git,true");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/vikrant-phadtare1101/pad.git,,,java_i,releases-1.0,"
					+ projectName + ",goldenOrb_i_"+repoPostFix+",git,true");
			bufferedWriter.newLine();
			bufferedWriter.write(
					prefix + "https://github.com/1shekhar/php,,,php,3.0.0-dev," + projectName + ",symphony_i_"+repoPostFix+",git,true");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/kotlin_anko.git,,,kotlin_i,v0.10.3," + projectName
					+ ",anko_i_"+repoPostFix+",git,true");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/utPLSQL.git,,,sql,v3.0.4," + projectName
					+ ",utplsql_i_"+repoPostFix+",git,true");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/pyrrha-consensus.git,,,solidity,v0.4.4-staging,"
					+ projectName + ",pyrrha_i_"+repoPostFix+",git,true");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/D3-RP.git,,,c_sharp,0.9-alpha3," + projectName
					+ ",D3-RP_i_"+repoPostFix+",git,true");
			return file;
		} catch (Exception e) {
			System.out.println("Exception occured while creation of default config file." + e.getMessage());
			return null;
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				System.out.println("Exception occured while closing file." + e.getMessage());
			}
		}
	}

	public File createDefaultPerformanceConfigFile(String fileName, String args[]) {
		BufferedWriter bufferedWriter = null;
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			File file = null;
			if ((OS.indexOf("win") >= 0)) {
				file = new File(System.getProperty("user.dir") + "\\" + fileName);
			} else {
				file = new File(System.getProperty("user.dir") + "//" + fileName);
			}
			String prefix = args[1] + "," + args[2] + "," + args[3] + ",";
			String repoPostFix=args[4];
			Calendar calendar = Calendar.getInstance();
			String projectName = "Sanity_" + calendar.get(Calendar.DAY_OF_MONTH) + "_"
					+ calendar.get(Calendar.HOUR_OF_DAY);
			bufferedWriter = new BufferedWriter(new FileWriter((file)));
			bufferedWriter.write(
					"//GIT REPO FORMAT ==> GAMMA_URL,GAMMA_USERNAME,GAMMA_PASSWORD,GIT_URL,GIT_USERNAME,GIT_PASSWORD,LANGUAGE,BRANCH/TAG,PROJECTNAME,REPONAME,GIT|SVN|ZIP|REMOTE,INCREMENTAL");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/liferay-portal.git,,,java,master," + projectName
					+ ",liferay_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/opencv.git,,,cpp,master," + projectName
					+ ",opencv_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/react-native-maps.git,,,objective_c,master," + projectName
					+ ",react_native_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/spring-boot.git,,,java,master," + projectName
					+ ",spring_boot_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/corona_pg.git,,,java,master," + projectName
					+ ",corona_redmine_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/cassandra.git,,,java,master,"
					+ projectName + ",cassandra_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/clerezza.git,,,java,master," + projectName
					+ ",clerezza_japanese_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/kotlin.git,,,kotlin,master," + projectName
					+ ",kotlin_lang_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/vscode.git,,,typescript,master," + projectName
					+ ",vscode_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/grafana.git,,,typescript,master," + projectName
					+ ",grafana_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/TypeScript.git,,,typescript,master," + projectName
					+ ",typescriptlang_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/tensorflow.git,,,cpp,master,"
					+ projectName + ",tensorflow_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/phpmyadmin.git,,,php,master," + projectName
					+ ",myadmin_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/mono.git,,,c_sharp,master," + projectName
					+ ",mono_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/PowerShell.git,,,c_sharp,master," + projectName
					+ ",powershell_"+repoPostFix+",git,false");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/kubernetes.git,,,go,master," + projectName
					+ ",kubernetes_"+repoPostFix+",git,false");
			bufferedWriter.newLine();			
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/Quake-III-Arena.git,,,cpp,master," + projectName
					+ ",quake3_"+repoPostFix+",git,false");
			bufferedWriter.newLine();			
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/jdk.git,,,java,master," + projectName
					+ ",openjdk_"+repoPostFix+",git,false");
			bufferedWriter.newLine();	
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/eclipse.jdt.core.git,,,java,master," + projectName
					+ ",eclipsecore_"+repoPostFix+",git,false");
			bufferedWriter.newLine();	
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/eclipse.jdt.ui.git,,,java,master," + projectName
					+ ",eclipseui_"+repoPostFix+",git,false");
			bufferedWriter.newLine();	
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/linux.git,,,cpp,master," + projectName
					+ ",linux_"+repoPostFix+",git,false");
			bufferedWriter.newLine();	
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/ice.git,,,javascript,master," + projectName
					+ ",icejs_"+repoPostFix+",git,false");
			bufferedWriter.newLine();	
			bufferedWriter.write(prefix + "https://github.com/saurabhacellere/react.git,,,javascript,master," + projectName
					+ ",reactjs_"+repoPostFix+",git,false");
			bufferedWriter.newLine();	
					
			return file;
		} catch (Exception e) {
			System.out.println("Exception occured while creation of default config file." + e.getMessage());
			return null;
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				System.out.println("Exception occured while closing file." + e.getMessage());
			}
		}
	}
	public File createDefaultPRConfigFile(String fileName, String args[]) {
		BufferedWriter bufferedWriter = null;
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			File file = null;
			if ((OS.indexOf("win") >= 0)) {
				file = new File(System.getProperty("user.dir") + "\\" + fileName);
			} else {
				file = new File(System.getProperty("user.dir") + "//" + fileName);
			}
			String prefix = args[1] + "," + args[2] + "," + args[3] + ",";
			Calendar calendar = Calendar.getInstance();
			String projectName = "Sanity_" + calendar.get(Calendar.DAY_OF_MONTH) + "_"
					+ calendar.get(Calendar.HOUR_OF_DAY);
			bufferedWriter = new BufferedWriter(new FileWriter((file)));
			bufferedWriter.write(
					"//GAMMA_RE =============> GAMMA_URL,GAMMA_USERNAME,GAMMA_PASSWORD,GIT_URL,GIT_USERNAME,GIT_PASSWORD,LANGUAGE,BRANCH/TAG,PROJECTNAME,REPONAME,GIT,INCREMENTAL,JIRA_USERNAME|JIRA_API_KEY,JIRA_PASSWORD,JIRA_URL,JIRA_PROJECT_CODE");
			bufferedWriter.newLine();
			bufferedWriter.write(prefix + "https://github.com/1shekhar/libsodium.git,,,cpp,1.0.9," + projectName
					+ ",libsodium,git," + args[4]);
			bufferedWriter.newLine();
			return file;
		} catch (Exception e) {
			System.out.println("Exception occured while creation of default config file." + e.getMessage());
			return null;
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				System.out.println("Exception occured while closing file." + e.getMessage());
			}
		}
	}

	public File createOverriddenConfigFile(String fileName, String args[]) {
		BufferedWriter bufferedWriter = null;
		BufferedReader bufferedReader = null;
		String overriddenFile = "overridenconfig.txt";
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			File outputfile = null;
			if ((OS.indexOf("win") >= 0)) {
				outputfile = new File(System.getProperty("user.dir") + "\\" + overriddenFile);
			} else {
				outputfile = new File(System.getProperty("user.dir") + "//" + overriddenFile);
			}
			String prefix = args[1] + "," + args[2] + "," + args[3] + ",";
			bufferedWriter = new BufferedWriter(new FileWriter((outputfile)));
			bufferedWriter.write(
					"//GIT REPO FORMAT ==> GAMMA_URL,GAMMA_USERNAME,GAMMA_PASSWORD,GIT_URL,GIT_USERNAME,GIT_PASSWORD,LANGUAGE,BRANCH/TAG,PROJECTNAME,REPONAME,GIT,INCREMENTAL");
			bufferedWriter.newLine();
			bufferedReader = new BufferedReader(new FileReader(fileName));
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				if (str.startsWith("//") || str.trim().length() == 0)
					continue;
				str = str.substring(str.indexOf(",") + 1);
				str = str.substring(str.indexOf(",") + 1);
				str = str.substring(str.indexOf(",") + 1);
				str = str.substring(0, str.lastIndexOf(","));
				bufferedWriter.write(prefix + str + "," + args[4]);
				bufferedWriter.newLine();
			}
			return outputfile;
		} catch (Exception e) {
			System.out.println("Exception occured while creation of default config file." + e.getMessage());
			return null;
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				System.out.println("Exception occured while closing file." + e.getMessage());
			}
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				System.out.println("Exception occured while closing file." + e.getMessage());
			}
		}
	}

}
