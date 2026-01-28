package com.example.demo.service;

import com.example.demo.component.CsvParser;
import com.example.demo.component.CsvValidator;
import com.example.demo.model.CsvRowData;
import com.example.demo.model.CsvValidationResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * CSVバリデーションのサービスクラス。
 * CSVファイルの解析とバリデーションを統括する。
 */
@Service
public class CsvValidationService {

    private final CsvParser csvParser;
    private final CsvValidator csvValidator;

    public CsvValidationService(CsvParser csvParser, CsvValidator csvValidator) {
        this.csvParser = csvParser;
        this.csvValidator = csvValidator;
    }

    /**
     * CSVファイルのバリデーションを実行する。
     *
     * @param file アップロードされたCSVファイル
     * @return バリデーション結果
     */
    public CsvValidationResult validateCsvFile(MultipartFile file) {
        CsvValidationResult result = new CsvValidationResult();

        // ファイルの存在チェック
        if (file == null || file.isEmpty()) {
            result.setValid(false);
            result.addErrorMessage("CSVファイルが選択されていません。");
            return result;
        }

        // ファイル拡張子チェック
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".csv")) {
            result.setValid(false);
            result.addErrorMessage("CSVファイルを選択してください。");
            return result;
        }

        try {
            // CSVファイルを解析
            List<CsvRowData> rows = csvParser.parse(file);

            if (rows.isEmpty()) {
                result.setValid(false);
                result.addErrorMessage("CSVファイルにデータ行が存在しません。");
                return result;
            }

            // バリデーション実行
            result = csvValidator.validate(rows);

        } catch (IOException e) {
            result.setValid(false);
            result.addErrorMessage("CSVファイルの読み取りに失敗しました：" + e.getMessage());
        }

        return result;
    }
}
