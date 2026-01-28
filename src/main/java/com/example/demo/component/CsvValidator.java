package com.example.demo.component;

import com.example.demo.model.CsvRowData;
import com.example.demo.model.CsvValidationResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CSVデータのバリデーションを行うコンポーネント。
 * 「総勤務時間」が8時間超かつ「勤怠メモ」が空白の場合、該当行の「日付」を抽出しエラーとする。
 */
@Component
public class CsvValidator {

    private static final String HEADER_TOTAL_WORK_HOURS = "総勤務時間";
    private static final String HEADER_ATTENDANCE_MEMO = "勤怠メモ";
    private static final String HEADER_DATE = "日付";
    private static final double OVERTIME_THRESHOLD = 8.0;

    /**
     * CSVデータのバリデーションを実行する。
     *
     * @param rows 解析済みCSV行データリスト
     * @return バリデーション結果
     */
    public CsvValidationResult validate(List<CsvRowData> rows) {
        CsvValidationResult result = new CsvValidationResult();

        for (CsvRowData row : rows) {
            String totalWorkHoursStr = row.getColumnValue(HEADER_TOTAL_WORK_HOURS);
            String attendanceMemo = row.getColumnValue(HEADER_ATTENDANCE_MEMO);
            String date = row.getColumnValue(HEADER_DATE);

            double totalWorkHours = parseWorkHours(totalWorkHoursStr);

            // 総勤務時間が8時間よりも高く、かつ勤怠メモが空白の場合
            if (totalWorkHours > OVERTIME_THRESHOLD && isBlanks(attendanceMemo)) {
                String errorMessage = date + "：残業しているが勤怠メモが記載されていません。";
                result.addErrorMessage(errorMessage);
            }
        }

        // エラーがなければ成功メッセージを設定
        if (result.getErrorMessages().isEmpty()) {
            result.setValid(true);
            result.setSuccessMessage("正しく入力されています。今月もお疲れさまでした。");
        } else {
            result.setValid(false);
        }

        return result;
    }

    /**
     * 勤務時間文字列をdoubleに変換する。
     * "8:30" 形式（時:分）と "8.5" 形式（小数）の両方に対応。
     */
    private double parseWorkHours(String workHoursStr) {
        if (workHoursStr == null || workHoursStr.trim().isEmpty()) {
            return 0.0;
        }

        String trimmed = workHoursStr.trim();

        try {
            // "HH:MM" 形式の場合
            if (trimmed.contains(":")) {
                String[] parts = trimmed.split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                return hours + (minutes / 60.0);
            }
            // 小数形式の場合
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * 文字列が空白（null、空文字、スペースのみ）かを判定する。
     */
    private boolean isBlanks(String value) {
        return value == null || value.trim().isEmpty();
    }
}
