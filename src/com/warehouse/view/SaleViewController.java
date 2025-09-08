package com.warehouse.view;

import com.warehouse.model.Products;
import com.warehouse.service.WarehouseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleViewController {

    @FXML private ComboBox<Products> productComboBox;
    @FXML private TextField searchField;
    @FXML private TextField barcodeField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private Label totalLabel;
    
    @FXML private TableView<Map<String, Object>> cartTable;
    @FXML private TableColumn<Map<String, Object>, String> cartNameColumn;
    @FXML private TableColumn<Map<String, Object>, Integer> cartQuantityColumn;
    @FXML private TableColumn<Map<String, Object>, Double> cartPriceColumn;
    @FXML private TableColumn<Map<String, Object>, Double> cartTotalColumn;

    private WarehouseManager warehouseManager;
    private ObservableList<Map<String, Object>> cartItems = FXCollections.observableArrayList();
    private double totalAmount = 0.0;

    public void setWarehouseManager(WarehouseManager manager) {
        this.warehouseManager = manager;
        setupProductComboBox();
    }

    @FXML
    public void initialize() {
        System.out.println("💰 تهيئة واجهة البيع...");
        setupCartTable();
        setupSearchFunctionality();
        setupBarcodeScanner();
        setupQuantityField();
        
        cartTable.setItems(cartItems);
        quantityField.setText("1");
    }

    private void setupProductComboBox() {
        // تعبئة ComboBox بجميع المنتجات
        List<Products> allProducts = warehouseManager.getAllProducts();
        ObservableList<Products> products = FXCollections.observableArrayList(allProducts);
        productComboBox.setItems(products);
        
        // لعرض اسم المنتج في الComboBox
        productComboBox.setCellFactory(param -> new ListCell<Products>() {
            @Override
            protected void updateItem(Products product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    setText(product.getName() + " - " + product.getBarcode() + " - " + product.getPrice() + " ريال");
                }
            }
        });
        
        productComboBox.setButtonCell(new ListCell<Products>() {
            @Override
            protected void updateItem(Products product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText("اختر المنتج");
                } else {
                    setText(product.getName());
                }
            }
        });
    }

    private void setupCartTable() {
        cartNameColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue().get("name")));
        cartQuantityColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty((Integer) data.getValue().get("quantity")).asObject());
        cartPriceColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleDoubleProperty((Double) data.getValue().get("price")).asObject());
        cartTotalColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleDoubleProperty((Double) data.getValue().get("total")).asObject());
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1) {
                handleSearch();
            } else if (newValue.isEmpty()) {
                productComboBox.getSelectionModel().clearSelection();
            }
        });
    }

    private void setupBarcodeScanner() {
        barcodeField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleBarcodeScan();
            }
        });
    }

    private void setupQuantityField() {
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.length() > 1) {
            updateProductSuggestions(searchTerm);
        }
    }

    private void updateProductSuggestions(String searchTerm) {
        ObservableList<Products> suggestions = FXCollections.observableArrayList();
        
        // البحث بالاسم
        List<Products> byName = warehouseManager.searchProductsByName(searchTerm);
        suggestions.addAll(byName);
        
        // البحث بالباركود
        Products byBarcode = warehouseManager.findProductByBarcode(searchTerm);
        if (byBarcode != null && !suggestions.contains(byBarcode)) {
            suggestions.add(byBarcode);
        }
        
        productComboBox.setItems(suggestions);
        
        if (!suggestions.isEmpty()) {
            productComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleBarcodeScan() {
        String barcode = barcodeField.getText().trim();
        if (!barcode.isEmpty()) {
            Products product = warehouseManager.findProductByBarcode(barcode);
            if (product != null) {
                addProductToCart(product);
                barcodeField.clear();
            } else {
                showAlert("خطأ", "المنتج غير موجود", "الباركود المدخل غير صحيح: " + barcode);
            }
        }
    }

    @FXML
    private void handleProductSelection() {
        Products selectedProduct = productComboBox.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            addProductToCart(selectedProduct);
        }
    }

    private void addProductToCart(Products product) {
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                showAlert("خطأ", "كمية غير صالحة", "الكمية يجب أن تكون أكبر من الصفر");
                return;
            }

            if (product.getQuantity() < quantity) {
                showAlert("خطأ", "كمية غير متوفرة", "المخزون المتاح: " + product.getQuantity());
                return;
            }

            double price = product.getPrice();
            addToCart(product, quantity, price);
            clearProductFields();

        } catch (NumberFormatException e) {
            showAlert("خطأ", "كمية غير صالحة", "يرجى إدخال رقم صحيح للكمية");
        }
    }

    private void addToCart(Products product, int quantity, double price) {
        // التحقق إذا كان المنتج موجوداً بالفعل في السلة
        for (Map<String, Object> item : cartItems) {
            if (item.get("barcode").equals(product.getBarcode())) {
                int currentQty = (Integer) item.get("quantity");
                item.put("quantity", currentQty + quantity);
                item.put("total", (currentQty + quantity) * price);
                updateCartTotal();
                cartTable.refresh();
                return;
            }
        }

        // إضافة منتج جديد إلى السلة
        Map<String, Object> item = new HashMap<>();
        item.put("barcode", product.getBarcode());
        item.put("name", product.getName());
        item.put("quantity", quantity);
        item.put("price", price);
        item.put("total", quantity * price);

        cartItems.add(item);
        totalAmount += quantity * price;
        totalLabel.setText(String.format("%.2f", totalAmount));
    }

    @FXML
    private void increaseQuantity() {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            quantityField.setText(String.valueOf(quantity + 1));
        } catch (NumberFormatException e) {
            quantityField.setText("1");
        }
    }

    @FXML
    private void decreaseQuantity() {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity > 1) {
                quantityField.setText(String.valueOf(quantity - 1));
            }
        } catch (NumberFormatException e) {
            quantityField.setText("1");
        }
    }

    @FXML
    private void removeFromCart() {
        Map<String, Object> selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            double itemTotal = (Double) selectedItem.get("total");
            totalAmount -= itemTotal;
            cartItems.remove(selectedItem);
            totalLabel.setText(String.format("%.2f", totalAmount));
        } else {
            showAlert("تحذير", "لم يتم تحديد منتج", "يرجى تحديد منتج من السلة للحذف");
        }
    }

    @FXML
    private void handleCompleteSale() {
        if (cartItems.isEmpty()) {
            showAlert("خطأ", "سلة فارغة", "لم يتم إضافة أي منتجات إلى السلة");
            return;
        }

        // تأكيد عملية البيع
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("تأكيد البيع");
        confirmation.setHeaderText("إتمام عملية البيع");
        confirmation.setContentText("المجموع الكلي: " + totalAmount + " ريال\nعدد المنتجات: " + cartItems.size());
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                processSale();
            }
        });
    }

    private void processSale() {
        boolean allSuccessful = true;
        StringBuilder receipt = new StringBuilder();
        receipt.append("🧾 إيصال البيع\n");
        receipt.append("══════════════════════════════\n\n");

        for (Map<String, Object> item : cartItems) {
            String barcode = (String) item.get("barcode");
            int quantity = (Integer) item.get("quantity");
            double price = (Double) item.get("price");
            
            boolean success = warehouseManager.sellProduct(barcode, quantity, price, 0);
            
            if (success) {
                receipt.append("✅ ").append(item.get("name"))
                      .append(" - الكمية: ").append(quantity)
                      .append(" - السعر: ").append(price)
                      .append(" - المجموع: ").append(item.get("total"))
                      .append("\n");
            } else {
                receipt.append("❌ فشل بيع: ").append(item.get("name")).append("\n");
                allSuccessful = false;
            }
        }

        receipt.append("\n💰 المجموع الكلي: ").append(totalAmount).append(" ريال");
        
        if (allSuccessful) {
            receipt.append("\n\n🎉 تمت عملية البيع بنجاح!");
            clearCart();
        } else {
            receipt.append("\n\n⚠️ حدثت بعض الأخطاء أثناء عملية البيع");
        }

        showAlert("نتيجة البيع", "إيصال البيع", receipt.toString());
    }

    private void updateCartTotal() {
        totalAmount = 0.0;
        for (Map<String, Object> item : cartItems) {
            totalAmount += (Double) item.get("total");
        }
        totalLabel.setText(String.format("%.2f", totalAmount));
    }

private void clearProductFields() {
    if (searchField != null) searchField.clear();
    if (barcodeField != null) barcodeField.clear();
    if (quantityField != null) quantityField.setText("1");
    if (priceField != null) priceField.clear();
    if (productComboBox != null) productComboBox.getSelectionModel().clearSelection();
}

    private void clearCart() {
        cartItems.clear();
        totalAmount = 0.0;
        totalLabel.setText("0.00");
        clearProductFields();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}