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
import java.util.List;

@Controller
public class CsvCheckerController {

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
        List<String> errors = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        int lineNumber = 0;

        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a CSV file to upload.");
            return "csv-checker";
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int expectedColumns = -1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] columns = line.split(",", -1);

                if (expectedColumns == -1) {
                    expectedColumns = columns.length;
                } else if (columns.length != expectedColumns) {
                    errors.add("Line " + lineNumber + ": Expected " + expectedColumns
                            + " columns but found " + columns.length);
                }

                List<String> row = new ArrayList<>();
                for (String col : columns) {
                    row.add(col);
                }
                rows.add(row);
            }

            model.addAttribute("rows", rows);
            model.addAttribute("totalLines", lineNumber);
            model.addAttribute("fileName", file.getOriginalFilename());

            if (errors.isEmpty()) {
                model.addAttribute("success", "CSV file is valid! Total lines: " + lineNumber);
            } else {
                model.addAttribute("errors", errors);
            }

        } catch (Exception e) {
            model.addAttribute("error", "Error reading file: " + e.getMessage());
        }

        return "csv-checker";
    }
}
