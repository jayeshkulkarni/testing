package performance;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ApachePOIExcelWrite {
	private Semaphore semaphore = new Semaphore(1);	
	private final String FILE_NAME = "Performance";
	private Object[][] results;
	private int count = 0, rowNum = 0;
	private static int columnCount = 24;
	private int repoSize = 0;

	ApachePOIExcelWrite(int repoSize) {
		this.results = new Object[repoSize + 1][columnCount];
		this.repoSize = repoSize;
		results[0][0] = "Project Name";
		results[0][1] = "RepoName";
		results[0][2] = "Repo URL";
		results[0][3] = "Language";
		results[0][4] = "Source extraction";
		results[0][5] = "Scan start";
		results[0][6] = "Parsing";
		results[0][7] = "Preprocessing";
		results[0][8] = "Metric";
		results[0][9] = "Unit Test";
		results[0][10] = "Issue Detection";
		results[0][11] = "Relevance";
		results[0][12] = "Data aggregation";
		results[0][13] = "Consolidation";
		results[0][14] = "Total Time";
		results[0][15] = "Clone Rating";
		results[0][16] = "Code Quality Rating";
		results[0][17] = "Anti PatternRating";
		results[0][18] = "Metric Rating";
		results[0][19] = "Overall Rating";
		results[0][20] = "Result";
		results[0][21] = "Test Machine";
		results[0][22] = "User Name";
		results[0][23] = "Password";
		this.count++;
	}

	public void addResults(Object[] scanResults) {
			try {
				semaphore.acquire();
				System.out.println("Processed repo count : " + count + " Remaining repo count :" + (repoSize - count));
				results[count++] = scanResults;
			} catch (InterruptedException e) {
			}finally {
				semaphore.release();
			}
	}

	public void storeResults() {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Gamma Performance results");

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
				fileName = System.getProperty("user.dir") + "\\" + FILE_NAME + "_" + calendar.get(Calendar.DAY_OF_MONTH)+ "_" + calendar.get(Calendar.HOUR_OF_DAY)
						+ "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.MILLISECOND) + ".xlsx";
			} else {
				fileName = System.getProperty("user.dir") + "//" + FILE_NAME + "_" + calendar.get(Calendar.HOUR_OF_DAY)
						+ "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.MILLISECOND) + ".xlsx";
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
}
