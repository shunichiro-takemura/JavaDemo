package com.example.demo.model;

import java.util.Map;

/**
 * CSVの1行分のデータを保持するモデルクラス。
 */
public class CsvRowData {

    private int rowNumber;
    private Map<String, String> columns;

    public CsvRowData(int rowNumber, Map<String, String> columns) {
        this.rowNumber = rowNumber;
        this.columns = columns;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    public String getColumnValue(String headerName) {
        return columns.getOrDefault(headerName, "");
    }
}
