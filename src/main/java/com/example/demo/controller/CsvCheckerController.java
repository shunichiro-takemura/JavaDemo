package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CsvCheckerController {

    private static final String HEADER_TOTAL_WORK_HOURS = "総勤務時間";
    private static final String HEADER_ATTENDANCE_MEMO = "勤怠メモ";
    private static final String HEADER_DATE = "日付";
    private static final double OVERTIME_THRESHOLD = 8.0;

    @GetMapping("/")
    public String index() {
        return "redirect:/csv-checker";
    }

    @GetMapping("/csv-checker")
    public String csvChecker() {
        return "csv-checker";
    }

    @PostMapping("/csv-checker")
    public String checkCsv(@RequestParam("file") MultipartFile file, Model model) {
        List<List<String>> rows = new ArrayList<>();
        List<String> validationMessages = new ArrayList<>();

        if (file.isEmpty()) {
            model.addAttribute("error", "CSVファイルを選択してください。");
            return "csv-checker";
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;
            Map<String, Integer> headerIndex = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] columns = line.split(",", -1);

                List<String> row = new ArrayList<>();
                for (String col : columns) {
                    row.add(col);
                }
                rows.add(row);

                if (lineNumber == 1) {
                    for (int i = 0; i < columns.length; i++) {
                        headerIndex.put(columns[i].trim(), i);
                    }
                    continue;
                }

                Integer totalWorkHoursIdx = headerIndex.get(HEADER_TOTAL_WORK_HOURS);
                Integer attendanceMemoIdx = headerIndex.get(HEADER_ATTENDANCE_MEMO);
                Integer dateIdx = headerIndex.get(HEADER_DATE);

                if (totalWorkHoursIdx == null || attendanceMemoIdx == null || dateIdx == null) {
                    model.addAttribute("error", "必要なヘッダー（総勤務時間、勤怠メモ、日付）が見つかりません。");
                    return "csv-checker";
                }

                String totalWorkHoursStr = columns[totalWorkHoursIdx].trim();
                String attendanceMemo = columns[attendanceMemoIdx].trim();
                String date = columns[dateIdx].trim();

                double totalWorkHours = parseWorkHours(totalWorkHoursStr);

                if (totalWorkHours > OVERTIME_THRESHOLD && attendanceMemo.isEmpty()) {
                    validationMessages.add(date + "：残業しているが勤怠メモが記載されていません。");
                }
            }

            model.addAttribute("rows", rows);
            model.addAttribute("totalLines", lineNumber);
            model.addAttribute("fileName", file.getOriginalFilename());

            if (validationMessages.isEmpty()) {
                model.addAttribute("success", "正しく入力されています。今月もお疲れさまでした。");
            } else {
                model.addAttribute("validationMessages", validationMessages);
            }

        } catch (Exception e) {
            model.addAttribute("error", "ファイル読み込みエラー: " + e.getMessage());
        }

        return "csv-checker";
    }

    private double parseWorkHours(String workHoursStr) {
        if (workHoursStr == null || workHoursStr.isEmpty()) {
            return 0.0;
        }

        try {
            if (workHoursStr.contains(":")) {
                String[] parts = workHoursStr.split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                return hours + (minutes / 60.0);
            }
            return Double.parseDouble(workHoursStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
