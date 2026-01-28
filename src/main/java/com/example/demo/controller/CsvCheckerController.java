package com.example.demo.controller;

import com.example.demo.model.CsvValidationResult;
import com.example.demo.service.CsvValidationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * CSVチェッカー画面のコントローラークラス。
 * 画面からのリクエストを最初に受け取り、サービスに処理を委譲する。
 */
@Controller
public class CsvCheckerController {

    private final CsvValidationService csvValidationService;

    public CsvCheckerController(CsvValidationService csvValidationService) {
        this.csvValidationService = csvValidationService;
    }

    /**
     * CSVチェッカー画面の初期表示。
     */
    @GetMapping("/csv-checker")
    public String showCsvChecker() {
        return "csv-checker";
    }

    /**
     * CSVファイルのチェック処理。
     * 画面からアップロードされたCSVファイルのバリデーションを実行し、結果を画面に返す。
     */
    @PostMapping("/csv-checker/check")
    public String checkCsv(@RequestParam("csvFile") MultipartFile csvFile, Model model) {
        CsvValidationResult result = csvValidationService.validateCsvFile(csvFile);

        model.addAttribute("result", result);
        model.addAttribute("fileName", csvFile.getOriginalFilename());
        model.addAttribute("checked", true);

        return "csv-checker";
    }
}
