package performance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Performance {

	public static void main(String args[]) {
//		String fileName=Calendar.DAY_OF_MONTH+"-"+Calendar.DAY_OF_MONTH+"-"+Calendar.DAY_OF_MONTH;
//		fileName+=".xlsx";
		storedata("fileName.xlsx", 1, 0, "ProjectName");
	}
	
	public static void storedata(String fileName,int i, int j, String value) {

		try {
			String os = System.getProperty("os.name");
			FileInputStream fis = null;
			File file = null;
			if (os.equalsIgnoreCase("Mac OS X")) {
				file = new File("/Users/Performance/"+fileName);
				if(!file.exists()){
					file.createNewFile();
				}
				fis = new FileInputStream(file);
			} else {
				file = new File("C:\\Performance\\"+fileName);
				if(!file.exists()){
					file.createNewFile();
				}
				fis = new FileInputStream(file);
			}
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sh1 = wb.getSheetAt(0);
			try{
				sh1.getRow(i).createCell(j).setCellValue(value);
			}catch(Exception e){
				sh1.createRow(i).createCell(j).setCellValue(value);
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			wb.write(fos);
			wb.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



}
