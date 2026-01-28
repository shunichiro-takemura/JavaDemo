package com.example.demo.component;

import com.example.demo.model.CsvRowData;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CSVファイルを解析するコンポーネント。
 * 1行目をヘッダーとして読み取り、2行目以降をデータ行として解析する。
 */
@Component
public class CsvParser {

    /**
     * CSVファイルを解析し、行データのリストを返す。
     *
     * @param file アップロードされたCSVファイル
     * @return 解析済みの行データリスト
     * @throws IOException ファイル読み取りエラー時
     */
    public List<CsvRowData> parse(MultipartFile file) throws IOException {
        List<CsvRowData> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            // 1行目をヘッダーとして読み取り
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                throw new IOException("CSVファイルにヘッダー行が存在しません。");
            }

            // BOM除去
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }

            String[] headers = splitCsvLine(headerLine);

            // 2行目以降のデータ部を読み取り
            String line;
            int rowNumber = 2;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    rowNumber++;
                    continue;
                }
                String[] values = splitCsvLine(line);
                Map<String, String> columnMap = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String value = (i < values.length) ? values[i].trim() : "";
                    columnMap.put(headers[i].trim(), value);
                }
                rows.add(new CsvRowData(rowNumber, columnMap));
                rowNumber++;
            }
        }

        return rows;
    }

    /**
     * CSV行をカンマで分割する。ダブルクォートで囲まれたフィールドに対応。
     */
    private String[] splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current = new StringBuilder();
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());

        return fields.toArray(new String[0]);
    }
}
