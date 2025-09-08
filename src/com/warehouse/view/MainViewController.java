package com.warehouse.view;

import com.warehouse.model.Products;
import com.warehouse.model.Transaction;
import com.warehouse.service.WarehouseManager;
import java.io.File;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class MainViewController {

    @FXML
    private AnchorPane mainContentPane;

    private WarehouseManager warehouseManager = new WarehouseManager();

    @FXML
    private void initialize() {
        System.out.println("🚀 بدء تشغيل الواجهة الرئيسية...");
        loadView("ProductView.fxml");
    }

    @FXML
    private void handleProductsButton() {
        System.out.println("📦 نقر على زر المنتجات");
        loadView("ProductView.fxml");
    }

    @FXML
    private void handleCustomersButton() {
        System.out.println("👥 نقر على زر العملاء");
        loadView("CustomersView.fxml");
    }

    @FXML
    private void handleSuppliersButton() {
        System.out.println("🏢 نقر على زر الموردين");
        loadView("SupplierView.fxml");
    }
    
    @FXML
    private void handleSaleButton() {
        System.out.println("💰 نقر على زر البيع");
        loadView("SaleView.fxml");
    }
    
    @FXML
    private void handleCheckLowStockButton() {
        System.out.println("⚠️ نقر على زر فحص المخزون الناقص");
        loadView("LowStockView.fxml");
    }
    
    @FXML
    private void handleShowTransactionsButton() {
        System.out.println("📊 نقر على زر العمليات");
        loadView("TransactionsView.fxml");
    }

    @FXML
    private void handleBackupButton() {
        System.out.println("💾 نقر على زر النسخ الاحتياطي");
        handleBackup();
    }

    @FXML
    private void handleInventoryReport() {
        System.out.println("📋 نقر على زر تقرير المخزون");
        showInventoryReport();
    }

    private void loadView(String fxmlFile) {
        try {
            System.out.println("🔍 محاولة تحميل: " + fxmlFile);
            
            String resourcePath = "/com/warehouse/view/" + fxmlFile;
            URL url = getClass().getResource(resourcePath);
            
            if (url == null) {
                throw new RuntimeException("لم يتم العثور على الملف: " + resourcePath);
            }
            
            FXMLLoader loader = new FXMLLoader(url);
            Node view = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof ProductsViewController) {
                ((ProductsViewController) controller).setWarehouseManager(warehouseManager);
            } else if (controller instanceof SupplierViewController) {
                ((SupplierViewController) controller).setWarehouseManager(warehouseManager);
            } else if (controller instanceof CustomersViewController) {
                ((CustomersViewController) controller).setWarehouseManager(warehouseManager);
            } else if (controller instanceof SaleViewController) {
                ((SaleViewController) controller).setWarehouseManager(warehouseManager);
            } else if (controller instanceof LowStockViewController) {
                ((LowStockViewController) controller).setWarehouseManager(warehouseManager);
            } else if (controller instanceof TransactionsViewController) {
                ((TransactionsViewController) controller).setWarehouseManager(warehouseManager);
            }
            
            System.out.println("✅ تم تحميل بنجاح: " + fxmlFile);
            
            switchContentView(view);
            
        } catch (Exception e) {
            System.err.println("❌ خطأ في تحميل الملف: " + fxmlFile + " - " + e.getMessage());
            showAlert("خطأ", "فشل تحميل الواجهة", "تعذر تحميل: " + fxmlFile);
        }
    }

    private void switchContentView(Node newView) {
        if (mainContentPane != null && newView != null) {
            mainContentPane.getChildren().clear();
            mainContentPane.getChildren().add(newView);
            AnchorPane.setTopAnchor(newView, 0.0);
            AnchorPane.setBottomAnchor(newView, 0.0);
            AnchorPane.setLeftAnchor(newView, 0.0);
            AnchorPane.setRightAnchor(newView, 0.0);
        }
    }

    private void handleBackup() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("اختر موقع حفظ النسخة الاحتياطية");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("SQL Backup", "*.sql")
        );
        
        File file = fileChooser.showSaveDialog(mainContentPane.getScene().getWindow());
        if (file != null) {
            boolean success = warehouseManager.backupDatabase(file.getAbsolutePath());
            if (success) {
                showAlert("نجاح", "النسخة الاحتياطية", "تم إنشاء النسخة الاحتياطية بنجاح في: " + file.getAbsolutePath());
            } else {
                showAlert("خطأ", "النسخة الاحتياطية", "فشل في إنشاء النسخة الاحتياطية. تأكد من إعدادات قاعدة البيانات.");
            }
        }
    }

    private void showInventoryReport() {
        try {
            String inventoryStats = warehouseManager.getInventoryStatistics();
            String lowStockReport = warehouseManager.getLowStockReport();
            
            StringBuilder report = new StringBuilder();
            report.append("📊 تقرير المخزون الشامل\n");
            report.append("══════════════════════════════\n\n");
            report.append(inventoryStats).append("\n");
            report.append(lowStockReport);
            
            // عرض التقرير في نافذة
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("تقرير المخزون الشامل");
            alert.setHeaderText("📋 تقرير شامل عن حالة المخزون");
            alert.setContentText(report.toString());
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(600, 500);
            alert.showAndWait();
            
        } catch (Exception e) {
            showAlert("خطأ", "تقرير المخزون", "تعذر إنشاء التقرير: " + e.getMessage());
        }
    }

    @FXML
    private void handleExit() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("تأكيد الخروج");
        confirmation.setHeaderText("هل أنت متأكد من الخروج؟");
        confirmation.setContentText("سيتم إغلاق نظام إدارة المستودعات");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                warehouseManager.disconnect();
                System.exit(0);
            }
        });
    }

    public WarehouseManager getWarehouseManager() {
        return warehouseManager;
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}