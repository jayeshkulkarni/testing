package interfaces;

import java.io.UnsupportedEncodingException;

@FunctionalInterface
public interface GitProvider {

	public String getReferenceCommitId(String refBranch) throws UnsupportedEncodingException;
	

}
