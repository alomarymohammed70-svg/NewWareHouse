package com.warehouse.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;

public class DataViewController {
    
    @FXML
    private VBox mainContainer;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private TextArea dataTextArea;
    
    @FXML
    public void initialize() {
        applyStyles();
        System.out.println("✅ تم تهيئة واجهة عرض البيانات");
    }
    
    private void applyStyles() {
        try {
            if (mainContainer != null) {
                mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);" +
                        "-fx-padding: 30;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 0);");
            }
            
            if (titleLabel != null) {
                titleLabel.setStyle("-fx-text-fill: white;" +
                        "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 1);");
            }
            
            if (dataTextArea != null) {
                dataTextArea.setStyle("-fx-font-family: 'Courier New', monospace;" +
                        "-fx-font-size: 14px;" +
                        "-fx-control-inner-background: #f8f9fa;" +
                        "-fx-text-fill: #2c3e50;");
            }
        } catch (Exception e) {
            System.err.println("❌ خطأ في تطبيق التنسيقات: " + e.getMessage());
        }
    }
    
    public void displayData(String title, String data) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
        if (dataTextArea != null) {
            dataTextArea.setText(data);
            System.out.println("✅ تم عرض البيانات: " + title);
        }
    }
    
    @FXML
    private void handlePrint() {
        if (dataTextArea != null) {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(dataTextArea.getScene().getWindow())) {
                boolean success = job.printPage(dataTextArea);
                if (success) {
                    job.endJob();
                    showAlert("نجاح", "تمت عملية الطباعة", "تم إرسال البيانات إلى الطابعة بنجاح.");
                } else {
                    showAlert("خطأ", "فشل الطباعة", "فشل في إرسال البيانات إلى الطابعة.");
                }
            }
        }
    }
    
    @FXML
    private void handleClose() {
        if (titleLabel != null && titleLabel.getScene() != null) {
            titleLabel.getScene().getWindow().hide();
            System.out.println("✅ تم إغلاق نافذة البيانات");
        }
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}