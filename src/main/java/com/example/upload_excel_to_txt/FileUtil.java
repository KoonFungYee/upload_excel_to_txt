package com.example.upload_excel_to_txt;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sh01252 on 2021/1/19.
 */
@Service
public class FileUtil {
    
    public static File uploadImportFile(MultipartFile file, String[] acceptFileNameExtensions){
        File cacheFile = null;
        final String fileName = file.getOriginalFilename();
        boolean fileInCorrectFormat = false;
        for(String fileNameExtension : acceptFileNameExtensions){
            if(fileName.toLowerCase().endsWith(fileNameExtension)){
                try {
                    cacheFile=File.createTempFile(file.getOriginalFilename(), fileNameExtension);
                    file.transferTo(cacheFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileInCorrectFormat = true;
                break;
            }
        }
        if(!fileInCorrectFormat){
            System.out.println("error");
        }
        return cacheFile;
    }

    
    public static Map<String,ColumnType> getHeadMap(ColumnType[] columnType){
        Map<String,ColumnType> map=new LinkedHashMap<String,ColumnType>();
        for(ColumnType c:columnType){
            map.put(c.getFieldName(), c);
        }
        return map;
    }

    
    public static Map<String,ColumnType> getValueMap(ColumnType[] ct,AbstractExcelImport abstractExcelImport) {
        int i=abstractExcelImport.getSheet().getFirstRowNum();
        Map<String,ColumnType> map=new LinkedHashMap<String,ColumnType>();
        int cellLen = 0;
        if(abstractExcelImport.getSheet().getRow(i) != null){
            cellLen=abstractExcelImport.getSheet().getRow(i).getLastCellNum();
        }
        for(int j=0;j<cellLen;j++){
            String cellValue=abstractExcelImport.getStringValue(i, j).trim();
            for(ColumnType c:ct){
                String cn=c.getColumnName().trim();
                if(cn.equals(cellValue)){
                    c.setColumn(j);
                    map.put(c.getFieldName(), c);
                }
            }
        }
        return map;
    }

    public static void deleteFile(File cacheFile){
        try {
            if(cacheFile != null){
                cacheFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
