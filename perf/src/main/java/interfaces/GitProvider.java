package interfaces;

import java.io.UnsupportedEncodingException;

@FunctionalInterface
public interface GitProvider {

	public String getReferenceCommitId(String APIUrl, String gitHubUserName, String gitPassword)
			throws UnsupportedEncodingException;
	

}
