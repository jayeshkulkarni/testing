package performance;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

public class ProcessExec {

	public static void executeProcessSync(String executable, String[] args, String outFile) throws Exception {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(outFile);
			PumpStreamHandler streamHandler = new PumpStreamHandler(os);
			CommandLine cmdLine = new CommandLine(executable);
			cmdLine.addArguments(args, true);
			DefaultExecutor executor = new DefaultExecutor();
			executor.setStreamHandler(streamHandler);
			executor.execute(cmdLine);
		} catch (IOException e) {
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
