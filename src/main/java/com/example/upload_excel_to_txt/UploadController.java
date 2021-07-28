package com.example.upload_excel_to_txt;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {

    @RequestMapping(value = "/upload")
    public String uploadPage() {
        return "uploadPage";
    }

    @RequestMapping(value = "/uploadFile", method = { RequestMethod.POST, RequestMethod.GET })
    public void uploadFile(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        File cacheFile = null;
        BufferedWriter writer1 = null;
        try {
            System.out.println("Oriname:" + file.getOriginalFilename());

            cacheFile = File.createTempFile(file.getOriginalFilename(), ".xlsx");
            file.transferTo(cacheFile);

            FileInputStream excelFIS = null;
            BufferedInputStream excelBIS = null;
            XSSFWorkbook workbook = null;

            excelFIS = new FileInputStream(cacheFile);
            excelBIS = new BufferedInputStream(excelFIS);
            workbook = new XSSFWorkbook(excelBIS);
            XSSFSheet excelSheet = workbook.getSheetAt(0);

            // header
            StringBuffer data = new StringBuffer();
            data.append("1").append(String.format("%-19s", "2263018755")).append("VA1")
                    .append(new SimpleDateFormat("yyyyMMdd").format(new Date()))
                    .append(new SimpleDateFormat("HHmmss").format(new Date())).append("MYR")
                    .append(String.format("%-140s", "SHENMA CREDIT SDN BHD"))
                    .append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append(String.format("%-792s", ""))
                    .append("\r\n");

            // read excel data
            BigDecimal total = BigDecimal.valueOf(0);
            for (int row = 1; row <= excelSheet.getLastRowNum(); row++) {
                XSSFRow excelRow = excelSheet.getRow(row);
                XSSFCell accountNumber = excelRow.getCell(1);
                XSSFCell valueDate = excelRow.getCell(2);
                XSSFCell date = excelRow.getCell(3);
                XSSFCell time = excelRow.getCell(4);
                XSSFCell description = excelRow.getCell(5);
                XSSFCell yourReference = excelRow.getCell(6);
                XSSFCell ourReference = excelRow.getCell(7);
                XSSFCell purposeOfTransfer = excelRow.getCell(8);
                XSSFCell deposit = excelRow.getCell(9);

                Date formatterDate = new SimpleDateFormat("dd/MM/yyyy")
                        .parse(date.getStringCellValue().replaceAll(" ", ""));
                String yymmdd = new SimpleDateFormat("yyyyMMdd").format(formatterDate);
                Date formatterTime = new SimpleDateFormat("hh:mm:ssaa")
                        .parse(time.getStringCellValue().replaceAll(" ", ""));
                String hhmmss = new SimpleDateFormat("HHmmss").format(formatterTime);

                data.append("2").append(yymmdd).append(hhmmss)
                        .append(String.format("%-34s",
                                new BigDecimal(yourReference.getNumericCellValue() + "").setScale(0).toString()))
                        .append(String.format("%-6s", "")).append(String.format("%-34s", ""))
                        .append(String.format("%-6s", "")).append("C").append("T").append("MYR");

                // amount
                BigDecimal depositAmt = new BigDecimal(deposit.getNumericCellValue()).setScale(0, RoundingMode.FLOOR);

                // decimal
                BigDecimal amount = new BigDecimal(deposit.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal decimalToInt = amount.subtract(amount.setScale(0, RoundingMode.FLOOR))
                        .movePointRight(amount.scale());
                total = total.add(amount);

                data.append(String.format("%015d", Integer.parseInt(depositAmt.toString())));
                if (decimalToInt.compareTo(BigDecimal.ZERO) == 0) {
                    data.append(String.format("%-2s", "00"));
                } else {
                    data.append(String.format("%-2s", decimalToInt.toString()));
                }
                data.append(String.format("%015d", Integer.parseInt(depositAmt.toString())));
                if (decimalToInt.compareTo(BigDecimal.ZERO) == 0) {
                    data.append(String.format("%-2s", "00"));
                } else {
                    data.append(String.format("%-2s", decimalToInt.toString()));
                }

                // Client Account Name(can't get it in excel)
                data.append(String.format("%-140s", "")).append(String.format("%-35s", ""))
                        .append(String.format("%-35s", "")).append(String.format("%-40s", ""))
                        .append(String.format("%-40s", ""));

                // Other Payment Reference 2
                // String purpose = purposeOfTransfer.getStringCellValue();
                // if (purpose.length() > 40) {
                // data.append(String.format("%-40s", purpose.substring(1, 41)));
                // } else {
                // data.append(String.format("%-40s", ""));
                // }
                data.append(String.format("%-40s", ""));

                // Other Payment Reference 3(can't get it in excel)
                data.append(String.format("%-40s", ""));

                // Transaction Description(direct get the value from Description column)
                data.append(String.format("%-35s", description.getStringCellValue().substring(1)));

                // Channel
                if (description.getStringCellValue().contains("Cash")) {
                    data.append(String.format("%-20s", "ATM"));
                } else if (description.getStringCellValue().contains("IBG")) {
                    data.append(String.format("%-20s", "IBG"));
                } else {
                    data.append(String.format("%-20s", "BPS"));
                }
                data.append(String.format("%-441s", "")).append("\r\n");
            }
            // Trailer
            data.append("9").append(String.format("%09d", (excelSheet.getLastRowNum() - 1))).append("000000000")
                    .append(String.format("%017d", Integer.parseInt(total.setScale(0, RoundingMode.FLOOR).toString())));

            // decimal of total
            BigDecimal decimalOfTotal = total.setScale(2, RoundingMode.HALF_UP);
            BigDecimal decimalToInt = decimalOfTotal.subtract(decimalOfTotal.setScale(0, RoundingMode.FLOOR))
                    .movePointRight(decimalOfTotal.scale());
            if (decimalToInt.compareTo(BigDecimal.ZERO) == 0) {
                data.append("00");
            } else {
                data.append(String.format("%-2s", decimalToInt.toString()));
            }
            data.append(String.format("%019d", 0)).append(String.format("%-943s", ""));

            // print out result
            System.out.println(data);

            // file name
            String fn = "VA1" + new SimpleDateFormat("yyyyMMdd").format(new Date());

            // Please modify below path to your Desktop path
            String path = "C:\\Users\\Koon Fung Yee\\Desktop\\" + fn + ".txt";
            writer1 = new BufferedWriter(new FileWriter(path));
            writer1.write(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer1 != null) {
                    writer1.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
