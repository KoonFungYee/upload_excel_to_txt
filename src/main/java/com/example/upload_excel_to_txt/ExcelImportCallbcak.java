package com.example.upload_excel_to_txt;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;


public interface ExcelImportCallbcak
{
    public void callback(Workbook workBook) throws Exception ;
    
    public Object getResult();
    
    public List<String> getErrList();
}
