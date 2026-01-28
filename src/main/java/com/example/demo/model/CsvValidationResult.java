package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * CSVバリデーション結果を保持するモデルクラス。
 */
public class CsvValidationResult {

    private boolean valid;
    private String successMessage;
    private List<String> errorMessages;

    public CsvValidationResult() {
        this.valid = true;
        this.successMessage = "";
        this.errorMessages = new ArrayList<>();
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void addErrorMessage(String message) {
        this.errorMessages.add(message);
    }
}
