package com.example.upload_excel_to_txt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public final class ExcelHelper {
    public static final int EXCEL_MAX = 60000;

    public static void importExcel(File filePath, boolean xlsx, ExcelImportCallbcak callback) {
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(filePath);
            org.apache.poi.ss.usermodel.Workbook workBook;
            try {
                if (xlsx) {
                    workBook = WorkbookFactory.create(fileStream);
                } else {
                    workBook = new HSSFWorkbook(fileStream);
                }
            } catch (OfficeXmlFileException ex) {
                throw new RuntimeException(ex);
            }

            callback.callback(workBook);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public static void importExcel(InputStream in, boolean xlsx, ExcelImportCallbcak callback) throws Exception {
        try {
            org.apache.poi.ss.usermodel.Workbook workBook;
            try {
                if (xlsx) {
                    workBook = new XSSFWorkbook(in);
                } else {
                    workBook = new HSSFWorkbook(in);
                }
            } catch (OfficeXmlFileException ex) {
                throw new RuntimeException(ex);
            }
            callback.callback(workBook);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public static List<Map<String, Object>> importExcelCovetMap(InputStream in, boolean xlsx, Integer startLine) throws Exception {
        try {
            org.apache.poi.ss.usermodel.Workbook workBook;
            try {
                if (xlsx) {
                    workBook = new XSSFWorkbook(in);
                } else {
                    workBook = new HSSFWorkbook(in);
                }
            } catch (OfficeXmlFileException ex) {
                throw new RuntimeException(ex);
            }
            if (startLine == null) {
                startLine = 0;
            }
            Sheet sheet = workBook.getSheetAt(0); //读取sheet 0

            int firstRowIndex = sheet.getFirstRowNum() + startLine; //第一行是列名，所以不读
            int lastRowIndex = sheet.getLastRowNum();

            List<Map<String, Object>> data = Lists.newArrayList();
            for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) { //遍历行
                Map<String, Object> lineMap = Maps.newHashMap();
                Row row = sheet.getRow(rIndex);
                if (row != null) {
                    int firstCellIndex = row.getFirstCellNum();
                    int lastCellIndex = row.getLastCellNum();
                    for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) { //遍历列
                        Cell cell = row.getCell(cIndex);
                        if (cell != null) {
                            Object obj= getCellValue(cell);
                            lineMap.put("key" + cIndex, obj.toString());
                        }
                    }
                }
                data.add(lineMap);
            }
            return data;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public static Object getCellValue(Cell cell) {
        Object obj = "";
        switch (cell.getCellType()) {
            case 4:
                obj = cell.getBooleanCellValue();
                break;
            case 5:
                obj = cell.getErrorCellValue();
                break;
            case 2:
                try {
                    obj = String.valueOf(cell.getStringCellValue());
                } catch (IllegalStateException e) {
                    String valueOf = String.valueOf(cell.getNumericCellValue());
                    BigDecimal bd = new BigDecimal(Double.valueOf(valueOf));
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    obj = bd;
                }
                break;
            case 0:
                obj = NumberToTextConverter.toText(cell.getNumericCellValue());
                break;
            case 1:
                String value = String.valueOf(cell.getStringCellValue());
                value = value.replace("\n", "");
                value = value.replace("\t", "");
                obj = value;
                break;
            default:
                break;
        }
        return obj;
    }

}
