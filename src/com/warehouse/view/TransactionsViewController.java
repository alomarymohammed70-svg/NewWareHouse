package com.warehouse.view;

import com.warehouse.model.Transaction;
import com.warehouse.service.WarehouseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class TransactionsViewController {

    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, Integer> idColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> productColumn;
    @FXML private TableColumn<Transaction, Integer> quantityColumn;
    @FXML private TableColumn<Transaction, Double> priceColumn;
    @FXML private TableColumn<Transaction, Double> totalColumn;
    @FXML private TableColumn<Transaction, String> userColumn;
    
    @FXML private Label totalSalesLabel;
    @FXML private Label countLabel;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private TextField searchField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private WarehouseManager warehouseManager;
    private ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();

    public void setWarehouseManager(WarehouseManager manager) {
        this.warehouseManager = manager;
        refreshTransactionsTable();
    }

    @FXML
    public void initialize() {
        System.out.println("📊 تهيئة واجهة العمليات...");
        setupTableColumns();
        setupTypeFilter();
        setupDatePickers();
        transactionsTable.setItems(transactionsList);
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        dateColumn.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            String timestamp = transaction.getTimestamp() != null ? 
                transaction.getTimestamp().toString() : "غير محدد";
            return new javafx.beans.property.SimpleStringProperty(timestamp);
        });
        
        typeColumn.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            String type = transaction.getType();
            String typeArabic = "";
            switch (type) {
                case "Sell": typeArabic = "بيع"; break;
                case "Buy": typeArabic = "شراء"; break;
                case "Adjust": typeArabic = "تعديل"; break;
                default: typeArabic = type;
            }
            return new javafx.beans.property.SimpleStringProperty(typeArabic);
        });
        
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productBarcode"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        totalColumn.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            double total = transaction.getQuantity() * transaction.getPrice();
            return new javafx.beans.property.SimpleDoubleProperty(total).asObject();
        });
        
        userColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("النظام"));
    }

    private void setupTypeFilter() {
        typeFilterCombo.getItems().addAll("جميع العمليات", "مبيعات", "شراء", "تعديل مخزون");
        typeFilterCombo.setValue("جميع العمليات");
    }

    private void setupDatePickers() {
        startDatePicker.setValue(LocalDate.now().minusDays(7));
        endDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleRefresh() {
        System.out.println("🔄 تحديث العمليات");
        refreshTransactionsTable();
    }

    private void refreshTransactionsTable() {
        try {
            if (warehouseManager != null) {
                transactionsList.clear();
                List<Transaction> allTransactions = warehouseManager.getAllTransactions();
                
                if (allTransactions != null && !allTransactions.isEmpty()) {
                    transactionsList.addAll(allTransactions);
                    updateStatistics(allTransactions);
                    System.out.println("✅ تم تحميل " + allTransactions.size() + " عملية");
                } else {
                    countLabel.setText("0");
                    totalSalesLabel.setText("0.00 ر.س");
                    System.out.println("📝 لا توجد عمليات مسجلة");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ خطأ في تحميل العمليات: " + e.getMessage());
            showAlert("خطأ", "فشل التحميل", "تعذر تحميل بيانات العمليات: " + e.getMessage());
        }
    }

    private void updateStatistics(List<Transaction> transactions) {
        int count = transactions.size();
        double totalSales = transactions.stream()
                .filter(t -> "Sell".equals(t.getType()))
                .mapToDouble(t -> t.getQuantity() * t.getPrice())
                .sum();
        
        countLabel.setText(String.valueOf(count));
        totalSalesLabel.setText(String.format("%.2f ر.س", totalSales));
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            refreshTransactionsTable();
            return;
        }
        
        ObservableList<Transaction> filteredList = FXCollections.observableArrayList();
        for (Transaction transaction : transactionsList) {
            if (transaction.getProductBarcode().toLowerCase().contains(searchTerm.toLowerCase())) {
                filteredList.add(transaction);
            }
        }
        
        transactionsTable.setItems(filteredList);
        updateStatistics(filteredList);
        System.out.println("🔍 تم العثور على " + filteredList.size() + " عملية للبحث: " + searchTerm);
    }

    @FXML
    private void handleFilter() {
        String selectedType = typeFilterCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if ("جميع العمليات".equals(selectedType) && startDate == null && endDate == null) {
            refreshTransactionsTable();
            return;
        }
        
        try {
            List<Transaction> filteredTransactions = null;
            
            // تطبيق الفلتر حسب النوع
            String filterType = "";
            if (!"جميع العمليات".equals(selectedType)) {
                switch (selectedType) {
                    case "مبيعات": filterType = "Sell"; break;
                    case "شراء": filterType = "Buy"; break;
                    case "تعديل مخزون": filterType = "Adjust"; break;
                }
                filteredTransactions = warehouseManager.getTransactionsByType(filterType);
            } else {
                filteredTransactions = warehouseManager.getAllTransactions();
            }
            
            // تطبيق الفلتر حسب التاريخ
            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                
                filteredTransactions.removeIf(transaction -> 
                    transaction.getTimestamp() == null ||
                    transaction.getTimestamp().isBefore(startDateTime) ||
                    transaction.getTimestamp().isAfter(endDateTime)
                );
            }
            
            transactionsTable.setItems(FXCollections.observableArrayList(filteredTransactions));
            updateStatistics(filteredTransactions);
            System.out.println("🗑️ تم تطبيق الفلتر: " + selectedType + " - " + filteredTransactions.size() + " عملية");
            
        } catch (Exception e) {
            System.err.println("❌ خطأ في التصفية: " + e.getMessage());
            showAlert("خطأ", "فشل التصفية", "تعذر تطبيق الفلتر: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFilter() {
        typeFilterCombo.setValue("جميع العمليات");
        searchField.clear();
        startDatePicker.setValue(LocalDate.now().minusDays(7));
        endDatePicker.setValue(LocalDate.now());
        refreshTransactionsTable();
        System.out.println("✅ تم مسح جميع الفلاتر");
    }

    @FXML
    private void handlePrint() {
        System.out.println("🖨️ طباعة تقرير العمليات");
        printTransactionsReport();
    }

    @FXML
    private void handleShowStatistics() {
        System.out.println("📊 عرض إحصائيات المخزون");
        showInventoryStatistics();
    }

    @FXML
    private void handleSalesReport() {
        System.out.println("💰 عرض إحصائيات المبيعات");
        showSalesStatistics();
    }

    @FXML
    private void handleExport() {
        System.out.println("📤 تصدير البيانات");
        exportToCSV();
    }

    private void printTransactionsReport() {
        try {
            StringBuilder report = new StringBuilder();
            report.append("📊 تقرير العمليات\n");
            report.append("══════════════════════════════\n\n");
            report.append("الفترة: ").append(startDatePicker.getValue()).append(" إلى ").append(endDatePicker.getValue()).append("\n");
            report.append("عدد العمليات: ").append(transactionsList.size()).append("\n");
            report.append("إجمالي المبيعات: ").append(totalSalesLabel.getText()).append("\n\n");
            
            for (Transaction transaction : transactionsList) {
                report.append("• ").append(transaction.getTimestamp()).append(" - ");
                report.append(transaction.getType()).append(" - ");
                report.append(transaction.getProductBarcode()).append(" - ");
                report.append(transaction.getQuantity()).append(" × ");
                report.append(transaction.getPrice()).append(" = ");
                report.append(transaction.getQuantity() * transaction.getPrice()).append(" ر.س\n");
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("تقرير العمليات");
            alert.setHeaderText("📋 تقرير شامل للعمليات");
            alert.setContentText(report.toString());
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(600, 400);
            alert.showAndWait();
            
        } catch (Exception e) {
            showAlert("خطأ", "التقرير", "تعذر إنشاء التقرير: " + e.getMessage());
        }
    }

    private void showInventoryStatistics() {
        if (warehouseManager != null) {
            String stats = warehouseManager.getInventoryStatistics();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("إحصائيات المخزون");
            alert.setHeaderText("📊 إحصائيات شاملة للمخزون");
            alert.setContentText(stats);
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(500, 300);
            alert.showAndWait();
        }
    }

    private void showSalesStatistics() {
        if (warehouseManager != null) {
            Map<String, Object> salesStats = warehouseManager.getSalesStatistics();
            
            StringBuilder stats = new StringBuilder();
            stats.append("💰 إحصائيات المبيعات\n");
            stats.append("══════════════════════════════\n\n");
            stats.append("• إجمالي المبيعات: ").append(salesStats.get("total_sales")).append("\n");
            stats.append("• إجمالي الإيرادات: ").append(String.format("%.2f", salesStats.get("total_revenue"))).append(" ر.س\n");
            stats.append("• متوسط سعر البيع: ").append(String.format("%.2f", salesStats.get("average_sale_price"))).append(" ر.س\n");
            stats.append("• أعلى قيمة بيع: ").append(String.format("%.2f", salesStats.get("max_sale_value"))).append(" ر.س\n");
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("إحصائيات المبيعات");
            alert.setHeaderText("💰 تقرير إحصائيات المبيعات");
            alert.setContentText(stats.toString());
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(500, 300);
            alert.showAndWait();
        }
    }

    private void exportToCSV() {
        try {
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("رقم العملية,التاريخ,النوع,المنتج,الكمية,السعر,الإجمالي\n");
            
            for (Transaction transaction : transactionsList) {
                csvContent.append(transaction.getId()).append(",");
                csvContent.append(transaction.getTimestamp()).append(",");
                csvContent.append(transaction.getType()).append(",");
                csvContent.append(transaction.getProductBarcode()).append(",");
                csvContent.append(transaction.getQuantity()).append(",");
                csvContent.append(transaction.getPrice()).append(",");
                csvContent.append(transaction.getQuantity() * transaction.getPrice()).append("\n");
            }
            
            // محاكاة عملية التصدير
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("تصدير البيانات");
            alert.setHeaderText("تم تصدير البيانات بنجاح");
            alert.setContentText("تم تصدير " + transactionsList.size() + " عملية إلى ملف CSV\n\n" + csvContent.toString());
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(600, 400);
            alert.showAndWait();
            
        } catch (Exception e) {
            showAlert("خطأ", "التصدير", "تعذر تصدير البيانات: " + e.getMessage());
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