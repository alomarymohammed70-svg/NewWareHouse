package com.warehouse.view;

import com.warehouse.model.Products;
import com.warehouse.service.WarehouseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class LowStockViewController {

    @FXML private TableView<Products> lowStockTable;
    @FXML private TableColumn<Products, String> barcodeColumn;
    @FXML private TableColumn<Products, String> nameColumn;
    @FXML private TableColumn<Products, Integer> currentQuantityColumn;
    @FXML private TableColumn<Products, Integer> minQuantityColumn;
    @FXML private TableColumn<Products, Integer> differenceColumn;
    @FXML private TableColumn<Products, String> statusColumn;
    @FXML private Label countLabel;

    private WarehouseManager warehouseManager;
    private ObservableList<Products> lowStockList = FXCollections.observableArrayList();

    public void setWarehouseManager(WarehouseManager manager) {
        this.warehouseManager = manager;
        refreshLowStockTable();
    }

    @FXML
    public void initialize() {
        System.out.println("⚠️ تهيئة واجهة المخزون الناقص...");
        setupTableColumns();
        lowStockTable.setItems(lowStockList);
    }

    private void setupTableColumns() {
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        currentQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        minQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("minQuantityThreshold"));
        
        // إنشاء أعمدة مخصصة للفرق والحالة
        differenceColumn.setCellValueFactory(cellData -> {
            Products product = cellData.getValue();
            int difference = product.getMinQuantityThreshold() - product.getQuantity();
            return new javafx.beans.property.SimpleIntegerProperty(difference).asObject();
        });
        
        statusColumn.setCellValueFactory(cellData -> {
            Products product = cellData.getValue();
            String status = product.isLowStock() ? "⚠️ ناقص" : "✅ كافي";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
    }

    @FXML
    private void handleRefresh() {
        System.out.println("🔄 تحديث المخزون الناقص");
        refreshLowStockTable();
    }

    private void refreshLowStockTable() {
        try {
            if (warehouseManager != null) {
                lowStockList.clear();
                List<Products> lowStockProducts = warehouseManager.getLowStockProducts();
                
                if (lowStockProducts != null && !lowStockProducts.isEmpty()) {
                    lowStockList.addAll(lowStockProducts);
                    countLabel.setText(String.valueOf(lowStockProducts.size()));
                    System.out.println("✅ تم تحميل " + lowStockProducts.size() + " منتج ناقص");
                } else {
                    countLabel.setText("0");
                    System.out.println("✅ لا توجد منتجات ناقصة في المخزون");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ خطأ في تحميل المخزون الناقص: " + e.getMessage());
            showAlert("خطأ", "فشل التحميل", "تعذر تحميل بيانات المخزون الناقص: " + e.getMessage());
        }
    }

    @FXML
    private void handlePrintReport() {
        System.out.println("🖨️ طباعة تقرير المخزون الناقص");
        generateLowStockReport();
    }

    @FXML
    private void handleNotifySuppliers() {
        System.out.println("📧 إرسال إشعار للموردين");
        notifySuppliers();
    }

    @FXML
    private void handlePurchaseOrder() {
        System.out.println("➕ إنشاء طلب شراء");
        createPurchaseOrder();
    }

    @FXML
    private void handleGenerateFullReport() {
        System.out.println("📋 إنشاء تقرير مفصل");
        generateDetailedReport();
    }

    private void generateLowStockReport() {
        try {
            String report = warehouseManager.getLowStockReport();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("تقرير المخزون الناقص");
            alert.setHeaderText("📋 تقرير المنتجات الناقصة في المخزون");
            alert.setContentText(report);
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(600, 400);
            alert.showAndWait();
            
        } catch (Exception e) {
            showAlert("خطأ", "التقرير", "تعذر إنشاء التقرير: " + e.getMessage());
        }
    }

    private void notifySuppliers() {
        if (lowStockList.isEmpty()) {
            showAlert("معلومات", "لا توجد منتجات ناقصة", "لا توجد منتجات ناقصة في المخزون لإرسال إشعارات");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("إرسال إشعارات");
        confirmation.setHeaderText("إرسال إشعارات للموردين");
        confirmation.setContentText("سيتم إرسال إشعارات للموردين حول " + lowStockList.size() + " منتج ناقص");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // محاكاة إرسال الإشعارات
                System.out.println("📧 تم إرسال إشعارات للموردين حول " + lowStockList.size() + " منتج");
                showAlert("نجاح", "الإشعارات", "تم إرسال الإشعارات بنجاح للموردين");
            }
        });
    }

    private void createPurchaseOrder() {
        Products selectedProduct = lowStockTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            int requiredQuantity = selectedProduct.getMinQuantityThreshold() - selectedProduct.getQuantity();
            
            Alert orderAlert = new Alert(Alert.AlertType.CONFIRMATION);
            orderAlert.setTitle("إنشاء طلب شراء");
            orderAlert.setHeaderText("طلب شراء للمنتج: " + selectedProduct.getName());
            orderAlert.setContentText("الكمية المطلوبة: " + requiredQuantity + 
                                    "\nالسعر الشرائي: " + selectedProduct.getPurchasePrice() +
                                    "\nالمجموع: " + (requiredQuantity * selectedProduct.getPurchasePrice()) + " ر.س");
            
            orderAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("✅ تم إنشاء طلب شراء للمنتج: " + selectedProduct.getName());
                    showAlert("نجاح", "طلب شراء", "تم إنشاء طلب الشراء بنجاح");
                }
            });
        } else {
            showAlert("تحذير", "لم يتم تحديد منتج", "يرجى تحديد منتج من القائمة لإنشاء طلب شراء");
        }
    }

    private void generateDetailedReport() {
        try {
            StringBuilder report = new StringBuilder();
            report.append("📊 تقرير مفصل عن المخزون الناقص\n");
            report.append("══════════════════════════════\n\n");
            
            report.append("عدد المنتجات الناقصة: ").append(lowStockList.size()).append("\n\n");
            
            for (Products product : lowStockList) {
                int deficit = product.getMinQuantityThreshold() - product.getQuantity();
                double valueDeficit = deficit * product.getPurchasePrice();
                
                report.append("• ").append(product.getName()).append(" (").append(product.getBarcode()).append(")\n");
                report.append("  - المتوفر: ").append(product.getQuantity()).append("\n");
                report.append("  - المطلوب: ").append(product.getMinQuantityThreshold()).append("\n");
                report.append("  - النقص: ").append(deficit).append("\n");
                report.append("  - قيمة النقص: ").append(String.format("%.2f", valueDeficit)).append(" ر.س\n");
                report.append("  - سعر الشراء: ").append(product.getPurchasePrice()).append(" ر.س\n");
                report.append("  - سعر البيع: ").append(product.getPrice()).append(" ر.س\n");
                report.append("──────────────────────────────\n");
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("تقرير مفصل");
            alert.setHeaderText("📋 التقرير المفصل للمخزون الناقص");
            alert.setContentText(report.toString());
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(700, 500);
            alert.showAndWait();
            
        } catch (Exception e) {
            showAlert("خطأ", "التقرير", "تعذر إنشاء التقرير المفصل: " + e.getMessage());
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