package com.davemorrissey.labs.subscaleview.sample.basicfeatures;

import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
/**
 * Created by TC34677 on 14/04/2017.
 */
public class ExcelWriter {
    public ExcelWriter(String filePath) {
        try{
            //Read the spreadsheet that needs to be updated
            FileInputStream fsIP= new FileInputStream(new File(filePath));
            //Access the workbook
            HSSFWorkbook wb = new HSSFWorkbook(fsIP);
            //Access the worksheet, so that we can update / modify it.
            HSSFSheet worksheet = wb.getSheetAt(0);
            // declare a Cell object
            Cell cell = null;
            // Access the second cell in second row to update the value
            cell = worksheet.getRow(1).getCell(1);
            // Get current cell value value and overwrite the value
            cell.setCellValue("OverRide existing value");
            //Close the InputStream
            fsIP.close();
            //Open FileOutputStream to write updates
            FileOutputStream output_file =new FileOutputStream(new File(filePath));
            //write changes
            wb.write(output_file);
            //close the stream
            output_file.close();
        } catch (Exception e){
            Log.d("ExcelWriter", e.toString());
        }
    }
}
