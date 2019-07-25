package performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class Main implements Callable<Boolean> {

	private String arguments[];

	public Main(String arguments[]) {
		this.arguments = arguments;
	}

	public static void printUsage() {
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println("Usage : java -jar RepoScanner.jar -c <config file>  ");
		System.out.println("Usage : java -jar RepoScanner.jar -re <config file>  ");
		System.out.println("Usage : java -jar RepoScanner.jar -p <config file>  ");
		System.out.println("Usage : java -jar RepoScanner.jar -f <config file1>,<config file2>....<config fileN>  ");
		System.out.println(
				"Usage : java -jar RepoScanner.jar -a <gammawebSitebaseURL> <gammawebsiteUserName> <gammawebsitepassword> <gammaURL> <gammaPassword>");
		System.out.println("Usage : java -jar RepoScanner.jar -verifybuild <branch-Name> <gitusername> <gitPassword> ");
		System.out.println(
				"Usage : java -jar RepoScanner.jar -sanity <gamma-url with port> <gammaUsername> <gammaPassword> <isIncremental:booleanvalue> <optional paramater:repo_prefix>");
		System.out.println(
				"Usage : java -jar RepoScanner.jar -sanitywithaccessstoken <gamma-url with port> <gammaAccessToken> <isIncremental:booleanvalue> <optional paramater:repo_prefix>");
		System.out.println(
				"Usage : java -jar RepoScanner.jar -incrementalsanity <gamma-url with port> <gammaUsername> <gammaPassword> <optional paramater:repo_prefix>");
		System.out.println(
				"Usage : java -jar RepoScanner.jar -performance <gamma-url with port> <gammaUsername> <gammaPassword> <optional paramater:repo_prefix>");
		System.out.println(
				"Usage : java -jar RepoScanner.jar -incrementalsanitywithaccesstoken <gamma-url with port> <gammaAccessToken> <optional paramater:repo_prefix>");
		System.out.println("Usage : java -jar RepoScanner.jar -downloadsanityfile  ");
		System.out.println("Usage : java -jar RepoScanner.jar -downloadincrementalsanityfile  ");
		System.out.println(
				"Usage : java -jar RepoScanner.jar -override <config-file> <gamma-url with port> <gammaUsername> <gammaPassword> <isIncremental:booleanvalue> ");
		System.out.println("Parameter : -c => executes the given config file");
		System.out.println("Parameter : -f => fetching Scan Results from given config file");
		System.out.println("Parameter : -p => Sequential execution of repos in config file for performance testing.");
		System.out.println("Parameter : -a => activate Gamma through CLI.");
		System.out.println("Parameter : -re => gamma RE with Jira integration/Redmine Integration.");
		System.out.println(
				"Parameter : -verifybuild => verfies the build.properties stored at default location with github commitid for particular branch.");
		System.out.println("Parameter : -sanity| sanitywithaccessstoken => runs inbuild set of repos for sanity .");
		System.out.println(
				"Parameter : -incrementalsanity | incrementalsanitywithaccesstoken => runs inbuild set of repos for incremental sanity .");
		System.out.println("Parameter : -downloadsanityfile  => download default sanity file for reference.");
		System.out.println(
				"Parameter : -downloadincrementalsanityfile  => download default incremental sanity file for reference.");
		System.out.println("Parameter : -performance  => runs inbuild performance repos sequentially.");
		System.out.println(
				"Parameter : -override  => It replaces <gamma-url with port> <gammaUsername> <gammaPassword> <isIncremental:booleanvalue> in given config file  .");
		System.out.println("----------------------------------------------------------------------------------------");
	}

	public static void printInvalidOptionMessage(String args[], boolean InvalidOption) {
		System.out.println("");
		if (InvalidOption) {
			if (args.length > 0) {
				System.out.println("Invalid option : " + args[0]);
			}
		} else {
			System.out.println("Invalid number of parameters for option : " + args[0]);
		}
		printUsage();
		System.exit(1);
	}

	public static boolean validateParameters(String args[], int[] paramCount) {
		for (int i = 0; i < paramCount.length; i++) {
			if (args.length == paramCount[i]) {
				return true;
			}
		}
		printInvalidOptionMessage(args, false);
		return false;
	}

	public static boolean validateParameters(String args[], int paramCount) {
		if (args.length == paramCount) {
			return true;
		} else {
			printInvalidOptionMessage(args, false);
			return false;
		}
	}

	public static void main(String args[]) throws IOException {
		if (args.length == 0) {
			printInvalidOptionMessage(args, true);
		}
		ThreadPoolExecutor executor = null;
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		File file = null;
		Configurator configurator = new Configurator();
		switch (args[0]) {

		case "-performance":
			int[] paramCount2 = { 4, 5 };
			if (validateParameters(args, paramCount2)) {
				if (args.length == 4) {
					args = increaseSizeOfArray(args, 5);
					args[4] = "";
					file = configurator.createDefaultPerformanceConfigFile("defaultperformanceconfig.txt", args);
				} else {
					file = configurator.createDefaultPerformanceConfigFile("defaultperformanceconfig.txt", args);
					if (file != null) {
						System.out.println("Default Performance Config file created for reference at :" + file.getAbsolutePath());
						executor.submit(new Main(new String[] { "-p", file.getAbsolutePath(), "false", "true" }));
					} else {
						System.out.println("Error occured during default config file creation.");
					}
				}
			}
			break;

		case "-sanitywithaccesstoken":
			int[] paramCount1 = { 4, 5 };
			if (validateParameters(args, paramCount1)) {
				if (args.length == 4) {
					String sample1[] = { args[0], args[1], "", args[2], args[3], "" };
					file = configurator.createDefaultConfigFile("defaultconfig.txt", sample1);
				} else {
					String sample1[] = { args[0], args[1], "", args[2], args[3], args[4] };
					file = configurator.createDefaultConfigFile("defaultconfig.txt", sample1);
				}
				if (file != null) {
					System.out.println("Default Config file created for reference at :" + file.getAbsolutePath());
					executor.submit(new Main(new String[] { "-c", file.getAbsolutePath(), "false", "true" }));
				} else {
					System.out.println("Error occured during default config file creation.");
				}
			}
			break;

		case "-incrementalsanitywithaccesstoken":
			int[] paramCount3 = { 3, 4 };
			if (validateParameters(args, paramCount3)) {
				if (args.length == 3) {
					String sample1[] = { args[0], args[1], "", args[2], "" };
					file = configurator.createDefaultIncrementalConfigFile("defaultconfig.txt", sample1);
				} else {
					String sample1[] = { args[0], args[1], "", args[2], args[3] };
					file = configurator.createDefaultIncrementalConfigFile("defaultconfig.txt", sample1);
				}
				if (file != null) {
					System.out.println(
							"Default Incremental Config file created for reference at :" + file.getAbsolutePath());
					executor.submit(new Main(new String[] { "-p", file.getAbsolutePath(), "false", "true" }));
				} else {
					System.out.println("Error occured during default config file creation.");
				}
			}
			break;
		case "-incrementalsanity":
			int[] paramCount4 = { 5, 4 };
			if (validateParameters(args, paramCount4)) {
				if (args.length == 4) {
					args = increaseSizeOfArray(args, 5);
					args[4] = "";
				}
				file = configurator.createDefaultIncrementalConfigFile("defaultconfig.txt", args);
				if (file != null) {
					System.out.println(
							"Default Incremental Config file created for reference at :" + file.getAbsolutePath());
					executor.submit(new Main(new String[] { "-p", file.getAbsolutePath(), "false", "false" }));
				} else {
					System.out.println("Error occured during default config file creation.");
				}
			}
			break;

		case "-override":
			if (validateParameters(args, 6)) {
				String sampleArguments1[] = { args[1], args[2], args[3], args[4], args[5] };
				file = configurator.createOverriddenConfigFile(args[1], sampleArguments1);
				if (file != null) {
					System.out.println("Overridden Config file created for reference at :" + file.getAbsolutePath());
					executor.submit(new Main(new String[] { "-c", file.getAbsolutePath(), "false", "false" }));
				} else {
					System.out.println("Error occured during default config file creation.");
				}
			}
			break;
		case "-downloadincrementalsanityfile":
			String sampleArguments2[] = { "-downloadincrementalsanityfile", "http://localhost:3000", "<gammausername>",
					"<gammaPassword>","" };
			file = configurator.createDefaultIncrementalConfigFile("defaultIncrementalconfig.txt", sampleArguments2);
			if (file != null) {
				System.out
						.println("Default Incremental Config file created for reference at :" + file.getAbsolutePath());
			} else {
				System.out.println("Error occured during default config file creation.");
			}
			break;
		case "-downloadsanityfile":
			String sampleArguments3[] = { "-downloadsanityfile", "http://localhost:3000", "<gammausername>",
					"<gammaPassword>", "<incrementalFlag:true/false>","" };
			file = configurator.createDefaultConfigFile("defaultconfig.txt", sampleArguments3);
			if (file != null) {
				System.out.println("Default Config file created for reference at :" + file.getAbsolutePath());
			} else {
				System.out.println("Error occured during default config file creation.");
			}
			break;
		case "-sanity":
			int[] paramCount5 = { 5, 6 };
			if (validateParameters(args, paramCount5)) {
				if (args.length == 5) {
					args = increaseSizeOfArray(args, 6);
					args[5] = "";
				}
				file = configurator.createDefaultConfigFile("defaultconfig.txt", args);
				if (file != null) {
					System.out.println("Default Config file created for reference at :" + file.getAbsolutePath());
					executor.submit(new Main(new String[] { "-c", file.getAbsolutePath(), "false", "false" }));
				} else {
					System.out.println("Error occured during default config file creation.");
				}
			}
			break;
		case "-verifybuild":
			if (validateParameters(args, 4)) {
				new GammaBuild().verifyBuild(args[1], args[2], args[3]);
			}
			break;
		case "-a":
		case "-A":
			if (validateParameters(args, 6)) {
				new GammaLicense(args[1], args[2], args[3], args[4], args[5]).activateGamma();
			}
			break;
		case "-pr":
		case "-PR":
			if (validateParameters(args, 2)) {
				executor.submit(new Main(new String[] { "-pr", args[1], "false" }));
			}
			break;
		case "-re":
		case "-RE":
			if (validateParameters(args, 2)) {
				executor.submit(new Main(new String[] { "-re", args[1], "false", "false" }));
			}
			break;

		case "-p":
		case "-P":
			if (validateParameters(args, 2)) {
				executor.submit(new Main(new String[] { "-p", args[1], "false", "false" }));
			}
			break;

		case "-c":
		case "-C":
			if (validateParameters(args, 2)) {
				executor.submit(new Main(new String[] { "-c", args[1], "false", "false" }));
			}
			break;

		case "-f":
		case "-F":
			if (validateParameters(args, 2)) {
				executor.submit(new Main(new String[] { "-c", args[1], "true", "false" }));
			}
			break;
		default:
			printInvalidOptionMessage(args, true);
			break;
		}
		if (executor != null)
			executor.shutdown();
	}

	public static String[] increaseSizeOfArray(String args[], int length) {
		String temp[] = new String[length];

		for (int i = 0; i < args.length; i++) {
			temp[i] = args[i];
		}
		args = temp;
		return args;
	}

	public void scan(String arguments[]) {
		try {
			System.out.println("path :" + System.getProperty("user.dir"));
			File file = new File(arguments[1]);
			if (!file.exists()) {
				System.out.println("configuration file not found at path :" + arguments[1]);
				System.exit(1);
			} else {
				System.out.println("configuration file found at path :" + arguments[1]);
			}

		} catch (Exception e2) {
			System.out.println("Error occured while accessing configuration file : " + e2.getMessage());
			System.exit(1);
		}
		List<GammaAPI> gammaList = new ArrayList<GammaAPI>();
		ResultWriter apachePOIExcelWrite = null;
		BufferedReader br = null;
		int repoCounter = 0;
		try {
			File file = new File(arguments[1]);
			br = new BufferedReader(new FileReader(file));
			String st;
			while ((st = br.readLine()) != null) {
				if (st.startsWith("//") || st.trim().length() == 0) {
					continue;
				}
				repoCounter++;
			}
			br.close();
			br = new BufferedReader(new FileReader(file));
			apachePOIExcelWrite = new ResultWriter(repoCounter);
			while ((st = br.readLine()) != null) {
				if (st.startsWith("//") || st.trim().length() == 0)
					continue;
				String[] parameters = st.split(",");
				switch (arguments[0]) {
				case "-p":
				case "-c":
					if (parameters.length != 12) {
						System.out.println(
								" Invalid number of parameters in config. It should be 12. Please check the configuration file.");
						System.exit(1);
					} else {
						gammaList.add(new GammaAPI(parameters[0], apachePOIExcelWrite, parameters[1], parameters[2],
								parameters[3], parameters[4], parameters[5], parameters[6], parameters[7],
								parameters[8], parameters[9], parameters[10], Boolean.parseBoolean(parameters[11]),
								Boolean.parseBoolean(arguments[2]), "-c", Boolean.parseBoolean(arguments[3])));
					}
					break;
				case "-pr":
					if (parameters.length == 14) {
						gammaList.add(new PullRequest(parameters[0], parameters[1], parameters[2], parameters[3],
								parameters[4], parameters[5], parameters[6], parameters[7], parameters[8],
								parameters[9], parameters[10], parameters[11], parameters[12],
								Integer.parseInt(parameters[13]), "-pr"));
					} else if (parameters.length == 12) {
						gammaList.add(new GammaAPI(parameters[0], apachePOIExcelWrite, parameters[1], parameters[2],
								parameters[3], parameters[4], parameters[5], parameters[6], parameters[7],
								parameters[8], parameters[9], parameters[10], Boolean.parseBoolean(parameters[11]),
								Boolean.parseBoolean(arguments[2]), "-pr", Boolean.parseBoolean(arguments[3])));
					} else {
						System.out.println(
								" Invalid number of parameters in config. It should be 14 for creation and 12 for normal scan. Please check the configuration file.");
						System.exit(1);
					}
					break;
				case "-re":
					if (parameters.length != 16) {
						System.out.println(
								" Invalid number of parameters in config. It should be 16. Please check the configuration file.");
						System.exit(1);
					} else {
						gammaList.add(new GammaAPI(parameters[0], apachePOIExcelWrite, parameters[1], parameters[2],
								parameters[3], parameters[4], parameters[5], parameters[6], parameters[7],
								parameters[8], parameters[9], parameters[10], Boolean.parseBoolean(parameters[11]),
								Boolean.parseBoolean(arguments[2]), parameters[12], parameters[13], parameters[14],
								parameters[15], Boolean.parseBoolean("true"), "-re",
								Boolean.parseBoolean(arguments[3])));
					}
					break;
				default:
					printInvalidOptionMessage(arguments, true);
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Please check configuration file");
			System.exit(1);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("something went wrong while reading config file.");
				System.exit(1);
			}
		}
		ThreadPoolExecutor executor = null;
		if (arguments[0].equalsIgnoreCase("-p")) {
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

		} else {
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(gammaList.size());

		}
		List<Future<Boolean>> list = new ArrayList<Future<Boolean>>();
		for (int i = 0; i < repoCounter; i++) {
			list.add(executor.submit(gammaList.get(i)));
		}
		for (Future<Boolean> fut : list) {
			try {
				fut.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!arguments[0].equalsIgnoreCase("-pr")) {
			apachePOIExcelWrite.storeResultsInCSVFormat();
		}
		executor.shutdown();
	}

	@Override
	public Boolean call() throws Exception {
		scan(arguments);
		return true;
	}

}
