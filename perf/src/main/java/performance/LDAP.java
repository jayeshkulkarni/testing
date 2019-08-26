package performance;

import static performance.HttpUtils.httpPost;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.jayway.restassured.response.Response;

public class LDAP {

	public LDAP() {

	}

	public void login(String emboldUrl, String userName, String password, int start, int end) throws IOException {
		for (int i = start; i < end; i++) {
			try {
				String apiurl = null;
				apiurl = emboldUrl + "/api/v1/auth";
				Response response = httpPost(apiurl,
						"{\"username\":\"" + userName.replaceAll("#", i + "") + "\",\"password\":\"" + password + "\"}",
						null);
				if (response.getStatusCode() != 200) {
					System.out.println(" Warning : URL: " + apiurl + " return HTTP Code :" + response.getStatusCode());
				}
				System.out.println("Response for :" + userName.replaceAll("#", i + "") + response.getBody().asString());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void createLDIFFile(String emboldUrl, int start, int end) throws IOException {
		String OS = System.getProperty("os.name").toLowerCase();
		File file = null;
		Scanner in = new Scanner(System.in);
		if ((OS.indexOf("win") >= 0)) {
			file = new File(System.getProperty("user.dir") + "\\" + "user" + end + ".ldif");
		} else {
			file = new File(System.getProperty("user.dir") + "//" + "user" + end + ".ldif");
		}
		BufferedWriter bufferedWriter = null;
		try {
			file.createNewFile();
			file = new File("C:\\Users\\Saurabh.Patil\\Downloads\\ldapimport\\user" + end + ".ldif");
			bufferedWriter = new BufferedWriter(new FileWriter((file)));
			String base = "dn: cn=# example,ou=sale,dc=example,dc=org\r\n" + "cn: # example\r\n" + "gidnumber: 501\r\n"
					+ "givenname: #\r\n" + "homedirectory: /home/users/#\r\n" + "loginshell: /bin/sh\r\n"
					+ "mail: #example@mygamma.io\r\n" + "objectclass: inetOrgPerson\r\n"
					+ "objectclass: posixAccount\r\n" + "objectclass: top\r\n" + "sn: example\r\n" + "uid: ex1\r\n"
					+ "uidnumber: 2001\r\n" + "userpassword: {MD5}RcJLNgWVwTjb+XPbV7a5Bw==";
			System.out.println("Base String used for generation of LDAP User : " + base);
			System.out.println(
					"Do you want to continue enter yes(y) or If you want to use your base string enter no(n) " + base);
			String s = in.nextLine();
			if (s.equalsIgnoreCase("N")) {
				System.out.println("Enter your base string make sure it has # for replacement with numbers");
				base = in.nextLine();
				System.out.println("Base String updated for generation of LDAP Users : " + base);
			} else {
				System.out.println("Please Note default password is embold for all users created with default ldif.");
			}

			for (int i = start; i < end; i++) {
				bufferedWriter.write(base.replaceAll("#", "user" + i));
				bufferedWriter.newLine();
				bufferedWriter.newLine();
			}
		} catch (Exception e) {
			System.out.println("Exception occured while creation of default config file." + e.getMessage());
		} finally {
			try {
				in.close();
				bufferedWriter.close();
			} catch (IOException e) {
				System.out.println("Exception occured while closing file." + e.getMessage());
			}
		}

	}

}
