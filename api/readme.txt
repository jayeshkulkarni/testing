****STEPS TO SETUP AND EXECUTE JMETER SCRIPT from UI Tool*****

1. Download JMeter from https://jmeter.apache.org/download_jmeter.cgi (Binary Release Zip)
Extract downloaded zip to the API Test Project folder

3. Set the environment variable JMETER_HOME pointing to "../bin/.." directory.
For example,
Windows Platform: %JMETER_HOME% = "C:\apache-jmeter-5.0\bin"
Linux Platform: %JMETER_HOME% = "/home/acellere/apache-jmeter-5.0/bin"

4. Create folder "gamma_api" inside /bin. 
5. Create folder: "data" inside /gamma_api.
Folder Structure should look like "../apache-jmeter-5.0/bin/gamma_api/data"

5. Add data files like CSV, Text, JSON, IMG etc. into /data folder (These files will be resources to corresponding APIs in the script)

6. Open JMeter and run the provided script


****STEPS TO SETUP AND EXECUTE JMETER SCRIPT from CommandLine*****
<In_Progress>


****STEPS TO SETUP AND EXECUTE JMETER SCRIPT via Jenkins Job*****
<In_Progress>


