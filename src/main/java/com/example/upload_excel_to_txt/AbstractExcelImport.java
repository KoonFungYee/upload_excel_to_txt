package com.example.upload_excel_to_txt;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;


public abstract class AbstractExcelImport {
	
	protected List<String> errList=new ArrayList<String>();
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected Workbook workBook;
	protected Sheet sheet;
	
	
	public AbstractExcelImport(Workbook workBook, Sheet sheet) {
		super();
		this.workBook = workBook;
		this.sheet = sheet;
	}
	public Workbook getWorkBook() {
		return workBook;
	}
	public Sheet getSheet(){
		return sheet;
	}
	
	public abstract void init()  throws Exception ;

	public abstract Object doImport() throws Exception ;
	
	public String getStringValue(int rowNum, int colNum){
		Row row = sheet.getRow(rowNum);
		Cell cell= row.getCell(colNum);
		return getStringValue(cell);
	}

	public String getStringValue(int rowNum, char colNum){
		return getStringValue(rowNum,Character.toUpperCase(colNum)-65);
	}

	public String getStringValue(int rowNum, String colNum){
		int sum=0;
		for(int i=0;i<colNum.length();i++){
			sum+=(Math.pow(26, colNum.length()-i-1))*(Character.toUpperCase(colNum.charAt(i))-64);
		}
		return getStringValue(rowNum,sum-1);
	}

	public boolean getBooleanValue(int rowNum, int colNum){
		Row row = sheet.getRow(rowNum);
		Cell cell= row.getCell(colNum);
		return "是".equals(getStringValue(cell))||"有".equals(getStringValue(cell));
	}

	public boolean getBooleanValue(int rowNum, char colNum){
		return getBooleanValue(rowNum,Character.toUpperCase(colNum)-65);
	}

	public boolean getBooleanValue(int rowNum, String colNum){
		int sum=0;
		for(int i=0;i<colNum.length();i++){
			sum+=(Math.pow(26, colNum.length()-i-1))*(Character.toUpperCase(colNum.charAt(i))-64);
		}
		return getBooleanValue(rowNum,sum-1);
	}

	public String getStringValue(Cell cell){
		try {
			FormulaEvaluator evaluator = getWorkBook().getCreationHelper()
					.createFormulaEvaluator();
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				return ""+ cell.getStringCellValue();
			}else if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
				return "";
			} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
				return ""+cell.getBooleanCellValue();
			} else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
				return ""+cell.getErrorCellValue();
			} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				evaluator.evaluateFormulaCell(cell);
				CellValue cellValue = evaluator.evaluate(cell);
				if (cellValue.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					return ""+ cellValue.getBooleanValue();
				} else if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					return ""+ cellValue.getNumberValue();
				} else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
					return ""+ cellValue.getStringValue();
				} else if (cellValue.getCellType() == Cell.CELL_TYPE_ERROR) {
					return ""+ cellValue.getErrorValue();
				}
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				if (DateUtil.isCellDateFormatted(cell)) {
					return ""+ DateUtils.formatDate2String(cell.getDateCellValue(),DateUtils.COMMON_PATTERN);
				} else {
					String v=""+BigDecimal.valueOf(cell.getNumericCellValue()).setScale(0);
					if(v.matches("\\d+\\.0")){
						v=v.replaceFirst("\\.0$", "");
					}
					return v;
				}
			}
		} catch (Exception e) {

		}
		return "";
	}

	public int getIntValue(int rowNum, int colNum){
		Row row = sheet.getRow(rowNum);
		Cell cell= row.getCell(colNum);
		return getIntValue(cell);
	}

	public int getIntValue(Cell cell){
		return (int)cell.getNumericCellValue();
	}

	public Integer getIntegerValue(int rowNum, int colNum){
		Row row = sheet.getRow(rowNum);
		Cell cell= row.getCell(colNum);
		return getIntegerValue(cell);
	}

	public Integer getIntegerValue(Cell cell){
		String v=this.getStringValue(cell);
		if(StringUtils.isEmpty(v)){
			return null;
		}
		return Integer.valueOf(v);
	}

	public double getDoubleValue(int rowNum, int colNum){
		Row row = sheet.getRow(rowNum);
		Cell cell= row.getCell(colNum);
		return getDoubleValue(cell);
	}

	public double getDoubleValue(int rowNum, char colNum){
		return getDoubleValue(rowNum,Character.toUpperCase(colNum)-65);
	}
	
	public double getDoubleValue(Cell cell){
		return cell.getNumericCellValue();
	}

	public Date getDateValue(Cell cell){
		if(cell==null){
			return null;
		}
		try {
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
		} catch (Exception e) {
			//ignore
		}
		String v=getStringValue(cell);
		if(v.matches("\\d{4}-\\d{2}-\\d{2}")){
			return DateUtils.trans2Date(v,"yyyy-MM-dd");
		}else if(v.matches("\\d{4}-\\d{1,2}-\\d{1,2}")){
			String str[]=v.split("-");
			v=str[0];
			for(int i=1;i<str.length;i++){
				v+="-";
				if(str[i].length()==1){
					v+=("0"+str[i]);
				}
			}
			return DateUtils.trans2Date(v,"yyyy-MM");
		}else if(v.matches("\\d{4}-\\d{2}")){
			return DateUtils.trans2Date(v,"yyyy-MM");
		}else if(v.matches("\\d{4}\\d{2}\\d{2}")){
			return DateUtils.trans2Date(v,"yyyyMMdd");
		}else if(v.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}\\:\\d{2}\\:\\d{2}")){
			return DateUtils.trans2Date(v,"yyyy-MM-dd HH:mm:ss");
		}
		return null;
	}

	public Date getDateValue(int rowNum, int colNum){
		Row row = sheet.getRow(rowNum);
		Cell cell= row.getCell(colNum);
		
		return getDateValue(cell);
	}

	public Date getDateValue(int rowNum, char colNum){
		return getDateValue(rowNum,Character.toUpperCase(colNum)-65);
	}

	public Object getCellValue(int rowNum, int colNum) {
		Row row = sheet.getRow(rowNum);
		Cell cell = row == null ? null : row.getCell(colNum);
		return getCell(cell);
	}

	private Object getCell(Cell cell) {
		Object reObject = new Object();
		FormulaEvaluator evaluator = getWorkBook().getCreationHelper()
				.createFormulaEvaluator();
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			reObject = "";
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			reObject = cell.getBooleanCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
			reObject = cell.getErrorCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			evaluator.evaluateFormulaCell(cell);
			CellValue cellValue = evaluator.evaluate(cell);

			if (cellValue.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
				reObject = cellValue.getBooleanValue();
			} else if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				reObject = cellValue.getNumberValue();
			} else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
				reObject = cellValue.getStringValue();
			} else if (cellValue.getCellType() == Cell.CELL_TYPE_ERROR) {
				reObject = cellValue.getErrorValue();
			}

		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			if (DateUtil.isCellDateFormatted(cell)) {
				reObject = cell.getDateCellValue();
			} else {
				reObject = cell.getNumericCellValue();

			}
		} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			reObject = cell.getStringCellValue();
		}
		return reObject;
	}

	public void checkNotEmpty(String [][] cellMsg) throws Exception{
		for(String[] s:cellMsg){
			String title=s[0];
			char cellInfo=s[1].charAt(0);
			int rowNum=Integer.valueOf(s[1].substring(1));
			String value=getStringValue(rowNum, cellInfo);
			if(StringUtils.isEmpty(value)){
				throw new Exception(onImportErrorMsg(rowNum+1,"["+title+"]不能为空,值["+s[1]+"]"));
			}
		}
	}

	public String onImportErrorMsg(int line,String msg){
		return "工作表["+getSheetName()+"]行["+line+"]导入时发生错误："+msg;
	}

	public String getSheetName(){
		return this.getSheet().getSheetName();
	}
	protected Map<String,ColumnType> getValueMap(ColumnType[] ct) {
		Map<String,ColumnType> map=new LinkedHashMap<String,ColumnType>();
		
		try {
			int i=sheet.getFirstRowNum();
			int cellLen=sheet.getRow(i).getLastCellNum();
			Set<String> checkDuplicateColumn = new HashSet<String>();
			for(int j=0;j<cellLen;j++){
				String cellValue=getStringValue(i, j);
				if(StringUtils.isEmpty(cellValue)){
					continue;
				} else {
					if (checkDuplicateColumn.contains(cellValue)) {
						String ei= new String("10001:"+"第["+j+"]列的"+cellValue+"数据重复 ");
						errList.add(ei);
						continue;
					} else{
						checkDuplicateColumn.add(cellValue);
					}
				}
				for(ColumnType c:ct){
					String cn=c.getColumnName();
					if(cn.equals(cellValue)){
						c.setColumn(j);
						map.put(c.getFieldName(), c);
					}
				}
			}
		} catch (Exception e) {
			String ei=new String("10000:"+"请使用标准模板进行数据导入");
			errList.add(ei);
		}
		return map;
	}
	protected Map<String,ColumnType> getHeadMap(ColumnType[] ct) {
		Map<String,ColumnType> map=new LinkedHashMap<String,ColumnType>();
		for(ColumnType c:ct){
			map.put(c.getFieldName(), c);
		}
		return map;
	}
	public List<String> getErrList() {
		return errList;
	}
	
	
}
