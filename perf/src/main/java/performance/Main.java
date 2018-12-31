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
		System.out.println("Usage : java -jar Scan.jar -c <config file>  ");
		System.out.println("Usage : java -jar Scan.jar -s <config file1>,<config file2>....<config fileN>  ");
		System.out.println("Usage : java -jar Scan.jar -f <config file1>,<config file2>....<config fileN>  ");
		System.out.println("Usage : -c Config file  ");
		System.out.println("Usage : -s Sequential execution of Config files  ");
		System.out.println("Usage : -f Parallel fetching Scan Results  ");
	}

	public static void main(String args[]) {
		if (args.length < 2 || args.length > 2) {
			printUsage();
			System.exit(1);
		}
		ThreadPoolExecutor executor = null;
		String[] configFiles = null;
		switch (args[0]) {
		case "-c":
		case "-C":
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
			executor.submit(new Main(new String[] { "-c", args[1], "false" }));
			break;
		case "-s":
		case "-S":
			List<Future<Boolean>> list = new ArrayList<Future<Boolean>>();
			configFiles = args[1].split(",");
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
			for (int i = 0; i < configFiles.length; i++) {
				list.add(executor.submit(new Main(new String[] { "-c", configFiles[i], "false" })));
			}
			for (Future<Boolean> fut : list) {
				try {
					fut.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case "-f":
		case "-F":
			configFiles = args[1].split(",");
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
			for (int i = 0; i < configFiles.length; i++) {
				executor.submit(new Main(new String[] { "-c", configFiles[i], "true" }));
			}
			break;
		default:
			printUsage();
			System.exit(1);
			break;
		}
		if (executor != null)
			executor.shutdown();
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
		ApachePOIExcelWrite apachePOIExcelWrite = null;
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
			apachePOIExcelWrite = new ApachePOIExcelWrite(repoCounter);
			while ((st = br.readLine()) != null) {
				if (st.startsWith("//") || st.trim().length() == 0)
					continue;
				String[] parameters = st.split(",");
				if(parameters.length!=12) {
					System.out.println(" Invalid number of parameters in config. Please check the configuration file ");
					System.exit(1);
				}
				gammaList.add(new GammaAPI(parameters[0], apachePOIExcelWrite, parameters[1], parameters[2],
						parameters[3], parameters[4], parameters[5], parameters[6], parameters[7],parameters[8],parameters[9],parameters[10],
						Boolean.parseBoolean(parameters[11]), Boolean.parseBoolean(arguments[2])));
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
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(gammaList.size());
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
		apachePOIExcelWrite.storeResultsInCSVFormat();
		executor.shutdown();
	}

	@Override
	public Boolean call() throws Exception {
		scan(arguments);
		return true;
	}

}
