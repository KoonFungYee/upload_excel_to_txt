package com.example.upload_excel_to_txt;


public class ColumnType {
	String fieldName;
	String columnName;
	FieldType fieldType;
	int column;
	EmptyEnum notNull;//允许空
	int length;
	public ColumnType(String fieldName, String columnName, FieldType fieldType, EmptyEnum notNull) {
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.fieldType = fieldType;
		this.notNull=notNull;
	}
	public ColumnType(String fieldName, String columnName, FieldType fieldType, EmptyEnum notNull, int length) {
		super();
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.fieldType = fieldType;
		this.notNull=notNull;
		this.length = length;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public FieldType getFieldType() {
		return fieldType;
	}
	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	
	public EmptyEnum getNotNull() {
		return notNull;
	}
	public void setNotNull(EmptyEnum notNull) {
		this.notNull = notNull;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	@Override
	public String toString() {
		return fieldType+" "+fieldName+";//"+columnName;
	}
	
}
