package com.example.upload_excel_to_txt;

import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SampleFileExcelCallBack implements ExcelImportCallbcak {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    List<String> errList= Lists.newArrayList();
    List<SampleFileData> retList= Lists.newArrayList();
    
    @Override
    public void callback(Workbook workBook) throws Exception {
        SampleFileExcel idb=new SampleFileExcel(workBook,workBook.getSheetAt(0));
        idb.init();
        errList=idb.getErrList();
        if(errList.size()==0){
            List<SampleFileData> objList=(List<SampleFileData>)idb.doImport();
            retList.addAll(objList);
        }
    }

    @Override
    public Object getResult() {
        return retList;
    }

    @Override
    public List<String> getErrList() {
        return errList;
    }
}
