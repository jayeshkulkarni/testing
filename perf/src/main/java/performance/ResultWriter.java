package performance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ResultWriter {
	private Semaphore semaphore = new Semaphore(1);
	private final static String FILE_NAME = "Gamma_Scan_Report";
	private Object[][] results;
	private int count = 0, rowNum = 0;
	public static int columnCount = 31;
	private int repoSize = 0;

	ResultWriter(int repoSize) {
		this.results = new Object[repoSize + 1][columnCount];
		this.repoSize = repoSize;
		results[0][0] = "Project_Name";
		results[0][1] = "Repo_Name";
		results[0][2] = "Repo_URL";
		results[0][3] = "Language";
		results[0][4] = "Download_Source";
		results[0][5] = "Start_Scan";
		results[0][6] = "Parsing";
		results[0][7] = "Preprocessing_Parser_Data";
		results[0][8] = "Metric_Calculation";
		results[0][9] = "Unit_Test";
		results[0][10] = "Issue_Detection";
		results[0][11] = "Relevance_Calculation";
		results[0][12] = "Data_Aggregation";
		results[0][13] = "Consolidation";
		results[0][14] = "Total_Time";
		results[0][15] = "Duplication";
		results[0][16] = "Code_Issues_Rating";
		results[0][17] = "Design_Issues_Rating";
		results[0][18] = "Metric_Rating";
		results[0][19] = "Overall_Rating";
		results[0][20] = "totalLoc";
		results[0][21] = "eloc";
		results[0][22] = "components";
		results[0][23] = "Code Issues";
		results[0][24] = "Design Issues";		
		results[0][25] = "Duplication Loc";		
		results[0][26] = "Scan_Result";
		results[0][27] = "Machine_IP";
		results[0][28] = "Gamma_UserName";
		results[0][29] = "Gamma_Password";
		results[0][30] = "SubsystemUID";
		this.count++;
	}

	public void addResults(Object[] scanResults) {
		try {
			semaphore.acquire();
			System.out.println("Processed repo count : " + count + " Remaining repo count :" + (repoSize - count));
			results[count++] = scanResults;
		} catch (InterruptedException e) {
		} finally {
			semaphore.release();
		}
	}

	public int getRepoSize() {
		return repoSize;
	}

	public void setRepoSize(int repoSize) {
		this.repoSize = repoSize;
	}

	public void storeResultsInExcel() {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Gamma_Scan_results");

		System.out.println("Creating excel");

		for (Object[] datatype : results) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (Object field : datatype) {
				Cell cell = row.createCell(colNum++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Integer) {
					cell.setCellValue((Integer) field);
				} else if (field instanceof Long) {
					cell.setCellValue((Long) field);
				} else if (field instanceof Double) {
					cell.setCellValue((Double) field);
				}
			}
		}

		try {
			Calendar calendar = Calendar.getInstance();
			String OS = System.getProperty("os.name").toLowerCase();
			String fileName = null;
			if ((OS.indexOf("win") >= 0)) {
				fileName = System.getProperty("user.dir") + "\\" + FILE_NAME + "_" + (calendar.get(Calendar.MONTH)+1) + "_"
						+ calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_"
						+ calendar.get(Calendar.MINUTE) + ".xlsx";
			} else {
				fileName = System.getProperty("user.dir") + "//" + FILE_NAME + "_" + (calendar.get(Calendar.MONTH)+1) + "_"
						+ calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_"
						+ calendar.get(Calendar.MINUTE) + ".xlsx";
			}
			FileOutputStream outputStream = new FileOutputStream(fileName);
			System.out.println("Creating excel at : " + fileName);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}

	public void storeResultsInCSVFormat() {
		try {
			Calendar calendar = Calendar.getInstance();
			String OS = System.getProperty("os.name").toLowerCase();
			String fileName = null;
			if ((OS.indexOf("win") >= 0)) {
				fileName = System.getProperty("user.dir") + "\\" + FILE_NAME + "_" + (calendar.get(Calendar.MONTH)+1) + "_"
						+ calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_"
						+ calendar.get(Calendar.MINUTE) + ".csv";
			} else {
				fileName = System.getProperty("user.dir") + "//" + FILE_NAME + "_" + (calendar.get(Calendar.MONTH)+1) + "_"
						+ calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_"
						+ calendar.get(Calendar.MINUTE) + ".csv";
			}
			System.out.println("Creating csv at : " + fileName);
			File file = new File(fileName);
			if (file.createNewFile()) {
				System.out.println("Creating CSV");
			} else {
				System.out.println("Error creating csv file");
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			for (Object[] datatype : results) {
				String line = "";
				for (Object field : datatype) {
					line = line + "," + field;
				}
				bw.write(line.replaceFirst(",", ""));
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}
}
