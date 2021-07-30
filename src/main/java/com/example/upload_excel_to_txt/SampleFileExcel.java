package com.example.upload_excel_to_txt;

import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.*;


public class SampleFileExcel extends AbstractExcelImport {

    public SampleFileExcel(Workbook workBook, Sheet sheet) {
        super(workBook, sheet);
    }

    List<SampleFileData> retList = new ArrayList<SampleFileData>();

    List<String> errList = new ArrayList<String>();

    @Override
    public void init() throws Exception {
        ColumnType[] columnType = getColumns();
        Map<String, ColumnType> headMap = FileUtil.getHeadMap(columnType);
        Map<String, ColumnType> map = FileUtil.getValueMap(columnType, this);
        for (ColumnType column : getColumns()) {
            if (!map.containsKey(column.getFieldName())) {
                String ei = new String("必须包含列[" + headMap.get(column.getFieldName()).getColumnName() + "]");
                errList.add(ei);
            }
        }
        int i = super.getSheet().getFirstRowNum() + 1;
        int lastrow = super.getSheet().getLastRowNum();
        Map<String, Object> valueMap = new HashMap<>();
        for (int j = i; j <= lastrow; j++) {
            Iterator<Map.Entry<String, ColumnType>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ColumnType> e = iterator.next();
                ColumnType ct = e.getValue();
                Object o = null;
                if (ct.getFieldType().equals(FieldType.String)) {
                    try {
                        o = super.getStringValue(j, ct.getColumn()).trim();
                    } catch (Exception e1) {
                        String ei =  new String("列[" + ct.getColumnName() + "]获取错误,期望是文本,影响行[" + (j + 1) + "]");
                        errList.add(ei);
                        continue;
                    }
                }
                if (ct.getFieldType().equals(FieldType.Number)) {
                    try {
                        o = super.getDoubleValue(j, ct.getColumn());
                    } catch (Exception e1) {
                        String ei =  new String("列[" + ct.getColumnName() + "]获取错误,期望是文本,影响行[" + (j + 1) + "]");
                        errList.add(ei);
                        continue;
                    }
                }
                if (ct.getFieldType().equals(FieldType.Date)) {
                    try {
                        o = super.getDateValue(j, ct.getColumn());
                    } catch (Exception e1) {
                        String ei =  new String("列[" + ct.getColumnName() + "]获取错误,期望是文本,影响行[" + (j + 1) + "]");
                        errList.add(ei);
                        continue;
                    }
                }

                valueMap.put(ct.getFieldName(), o);
            }
            JSONObject obj = JSONObject.fromObject(valueMap);
            SampleFileData distributorBlacklist = (SampleFileData)JSONObject.toBean(obj, SampleFileData.class);
            retList.add(distributorBlacklist);
        }
    }

    //新表单
    private ColumnType[] getColumns() {
        return new ColumnType[] {
                new ColumnType("name", "Name", FieldType.String, EmptyEnum.NULL),
                new ColumnType("amount", "Amount", FieldType.Number, EmptyEnum.NULL)
        };
    }

    @Override
    public Object doImport() throws Exception {
        return retList;
    }

    @Override
    public List<String> getErrList() {
        return errList;
    }
}
