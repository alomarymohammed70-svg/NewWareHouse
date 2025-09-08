package com.warehouse.view;

import com.warehouse.model.Products;
import com.warehouse.service.WarehouseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.Optional;

public class ProductsViewController {

    @FXML private TableView<Products> productTable;
    @FXML private TableColumn<Products, String> barcodeColumn;
    @FXML private TableColumn<Products, String> nameColumn;
    @FXML private TableColumn<Products, Integer> quantityColumn;
    @FXML private TableColumn<Products, Double> priceColumn;
    @FXML private TableColumn<Products, Double> purchasePriceColumn;
    @FXML private TableColumn<Products, Double> minSellingPriceColumn;
    @FXML private TableColumn<Products, Integer> minQuantityColumn;

    @FXML private TextField barcodeField;
    @FXML private TextField nameField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private TextField purchasePriceField;
    @FXML private TextField minSellingPriceField;
    @FXML private TextField minQuantityField;
    @FXML private TextField searchField;

    private WarehouseManager warehouseManager;
    private ObservableList<Products> productList = FXCollections.observableArrayList();

    public void setWarehouseManager(WarehouseManager manager) {
        this.warehouseManager = manager;
        refreshProductTable();
    }

    @FXML
    public void initialize() {
        System.out.println("🎯 تهيئة واجهة المنتجات...");
        setupTableColumns();
        productTable.setItems(productList);
        setupSelectionListener();
        setupSearchFunctionality();
    }

    private void setupTableColumns() {
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        purchasePriceColumn.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        minSellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("minSellingPrice"));
        minQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("minQuantityThreshold"));
    }

    private void setupSelectionListener() {
        productTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    fillFormWithProductDetails(newSelection);
                }
            });
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 2 || newValue.isEmpty()) {
                handleSearch();
            }
        });
    }

    private void refreshProductTable() {
        try {
            if (warehouseManager != null) {
                productList.clear();
                List<Products> allProducts = warehouseManager.getAllProducts();
                if (allProducts != null && !allProducts.isEmpty()) {
                    productList.addAll(allProducts);
                    System.out.println("✅ تم تحميل " + allProducts.size() + " منتج");
                } else {
                    System.out.println("⚠️ لا توجد منتجات لعرضها");
                }
            }
        } catch (Exception e) {
            showAlert("خطأ", "فشل في التحميل", "تعذر تحميل بيانات المنتجات: " + e.getMessage());
        }
    }

    private void fillFormWithProductDetails(Products product) {
        barcodeField.setText(product.getBarcode());
        nameField.setText(product.getName());
        quantityField.setText(String.valueOf(product.getQuantity()));
        priceField.setText(String.valueOf(product.getPrice()));
        purchasePriceField.setText(String.valueOf(product.getPurchasePrice()));
        minSellingPriceField.setText(String.valueOf(product.getMinSellingPrice()));
        minQuantityField.setText(String.valueOf(product.getMinQuantityThreshold()));
    }

    @FXML
    private void handleAdd() {
        try {
            String barcode = barcodeField.getText().trim();
            String name = nameField.getText().trim();
            
            if (barcode.isEmpty() || name.isEmpty()) {
                showAlert("خطأ في الإدخال", "بيانات ناقصة", "يرجى إدخال الباركود والاسم.");
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
            double minSellingPrice = Double.parseDouble(minSellingPriceField.getText().trim());
            int minQuantity = Integer.parseInt(minQuantityField.getText().trim());

            if (minSellingPrice <= purchasePrice) {
                showAlert("خطأ في الإدخال", "سعر غير صالح", "سعر البيع الأدنى يجب أن يكون أكبر من سعر الشراء.");
                return;
            }

            Products newProduct = new Products(barcode, name, quantity, price, purchasePrice, minSellingPrice, minQuantity);
            warehouseManager.addProduct(newProduct);
            refreshProductTable();
            clearFields();
            showAlert("نجاح", "تمت الإضافة بنجاح", "تم إضافة المنتج '" + name + "'.");
        } catch (NumberFormatException e) {
            showAlert("خطأ في الإدخال", "تنسيق غير صالح", "يرجى إدخال أرقام صحيحة في الحقول الرقمية.");
        } catch (Exception e) {
            showAlert("خطأ", "فشل في الإضافة", "تعذر إضافة المنتج: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Products selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                String barcode = barcodeField.getText().trim();
                String name = nameField.getText().trim();
                
                if (barcode.isEmpty() || name.isEmpty()) {
                    showAlert("خطأ في الإدخال", "بيانات ناقصة", "يرجى إدخال الباركود والاسم.");
                    return;
                }

                int quantity = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
                double minSellingPrice = Double.parseDouble(minSellingPriceField.getText().trim());
                int minQuantity = Integer.parseInt(minQuantityField.getText().trim());

                if (minSellingPrice <= purchasePrice) {
                    showAlert("خطأ في الإدخال", "سعر غير صالح", "سعر البيع الأدنى يجب أن يكون أكبر من سعر الشراء.");
                    return;
                }

                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("تأكيد التحديث");
                confirmation.setHeaderText("هل تريد تحديث بيانات المنتج؟");
                confirmation.setContentText("سيتم تطبيق التغييرات على المنتج: " + name);
                
                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    selectedProduct.setBarcode(barcode);
                    selectedProduct.setName(name);
                    selectedProduct.setQuantity(quantity);
                    selectedProduct.setPrice(price);
                    selectedProduct.setPurchasePrice(purchasePrice);
                    selectedProduct.setMinSellingPrice(minSellingPrice);
                    selectedProduct.setMinQuantityThreshold(minQuantity);
                    
                    warehouseManager.updateProduct(selectedProduct);
                    refreshProductTable();
                    clearFields();
                    showAlert("نجاح", "تم التحديث بنجاح", "تم تحديث بيانات المنتج '" + name + "'.");
                }
            } catch (NumberFormatException e) {
                showAlert("خطأ في الإدخال", "تنسيق غير صالح", "يرجى إدخال أرقام صحيحة في الحقول الرقمية.");
            } catch (Exception e) {
                showAlert("خطأ", "فشل في التحديث", "تعذر تحديث بيانات المنتج: " + e.getMessage());
            }
        } else {
            showAlert("خطأ في التحديد", "لم يتم تحديد منتج", "الرجاء تحديد منتج من الجدول لتحديثه.");
        }
    }

    @FXML
    private void handleDelete() {
        Products selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("تأكيد الحذف");
            confirmation.setHeaderText("هل أنت متأكد من حذف هذا المنتج؟");
            confirmation.setContentText("سيتم حذف المنتج: " + selectedProduct.getName());
            
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    warehouseManager.removeProduct(selectedProduct.getBarcode());
                    refreshProductTable();
                    clearFields();
                    showAlert("نجاح", "تم الحذف بنجاح", "تم حذف المنتج '" + selectedProduct.getName() + "'.");
                } catch (Exception e) {
                    showAlert("خطأ", "فشل في الحذف", "تعذر حذف المنتج: " + e.getMessage());
                }
            }
        } else {
            showAlert("خطأ في التحديد", "لم يتم تحديد منتج", "الرجاء تحديد منتج من الجدول لحذفه.");
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            refreshProductTable();
        } else {
            List<Products> searchResults = warehouseManager.searchProductsByName(searchTerm);
            productList.setAll(searchResults);
        }
    }

    private void clearFields() {
        barcodeField.clear();
        nameField.clear();
        quantityField.clear();
        priceField.clear();
        purchasePriceField.clear();
        minSellingPriceField.clear();
        minQuantityField.clear();
        productTable.getSelectionModel().clearSelection();
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}