package com.warehouse.view;

import com.warehouse.model.Supplier;
import com.warehouse.service.WarehouseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.Optional;

public class SupplierViewController {

    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, Integer> idColumn;
    @FXML private TableColumn<Supplier, String> nameColumn;
    @FXML private TableColumn<Supplier, String> contactPersonColumn;
    @FXML private TableColumn<Supplier, String> contactInfoColumn;
    @FXML private TableColumn<Supplier, String> addressColumn;
    
    @FXML private TextField searchField;
    @FXML private TextField nameField;
    @FXML private TextField contactPersonField;
    @FXML private TextField contactInfoField;
    @FXML private TextField addressField;

    private WarehouseManager warehouseManager;
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    public void setWarehouseManager(WarehouseManager manager) {
        this.warehouseManager = manager;
        refreshSupplierTable();
    }

    @FXML
    public void initialize() {
        System.out.println("✅ تهيئة واجهة الموردين...");
        setupTableColumns();
        supplierTable.setItems(supplierList);
        setupSelectionListener();
        setupSearchFunctionality();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactPersonColumn.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        contactInfoColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    }
    
    private void setupSelectionListener() {
        supplierTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    fillFormWithSupplierDetails(newSelection);
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
    
    private void refreshSupplierTable() {
        try {
            if (warehouseManager != null) {
                supplierList.clear();
                List<Supplier> allSuppliers = warehouseManager.getAllSuppliers();
                if (allSuppliers != null && !allSuppliers.isEmpty()) {
                    supplierList.addAll(allSuppliers);
                    System.out.println("✅ تم تحميل " + allSuppliers.size() + " مورد في الجدول");
                } else {
                    System.out.println("⚠️ لا توجد موردون لعرضهم");
                }
            }
        } catch (Exception e) {
            showAlert("خطأ", "فشل في التحميل", "تعذر تحميل بيانات الموردين: " + e.getMessage());
        }
    }
    
    private void fillFormWithSupplierDetails(Supplier supplier) {
        nameField.setText(supplier.getName());
        contactPersonField.setText(supplier.getContactPerson());
        contactInfoField.setText(supplier.getContactInfo());
        addressField.setText(supplier.getAddress());
    }

    @FXML
    private void handleAdd() {
        try {
            String name = nameField.getText().trim();
            String contactPerson = contactPersonField.getText().trim();
            String contactInfo = contactInfoField.getText().trim();
            String address = addressField.getText().trim();
            
            if (name.isEmpty()) {
                showAlert("خطأ في الإدخال", "بيانات ناقصة", "يرجى إدخال اسم المورد.");
                return;
            }
            
            Supplier newSupplier = new Supplier(0, name, contactPerson, contactInfo, address);
            warehouseManager.addSupplier(newSupplier);
            refreshSupplierTable();
            clearFields();
            showAlert("نجاح", "تمت الإضافة بنجاح", "تم إضافة المورد '" + name + "'.");
        } catch (Exception e) {
            showAlert("خطأ", "فشل في الإضافة", "تعذر إضافة المورد: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate() {
        Supplier selectedSupplier = supplierTable.getSelectionModel().getSelectedItem();
        if (selectedSupplier != null) {
            try {
                String name = nameField.getText().trim();
                String contactPerson = contactPersonField.getText().trim();
                String contactInfo = contactInfoField.getText().trim();
                String address = addressField.getText().trim();
                
                if (name.isEmpty()) {
                    showAlert("خطأ في الإدخال", "بيانات ناقصة", "يرجى إدخال اسم المورد.");
                    return;
                }
                
                warehouseManager.updateSupplier(selectedSupplier.getId(), name, contactPerson, contactInfo, address);
                refreshSupplierTable();
                clearFields();
                showAlert("نجاح", "تم التحديث بنجاح", "تم تحديث بيانات المورد '" + name + "'.");
            } catch (Exception e) {
                showAlert("خطأ", "فشل في التحديث", "تعذر تحديث بيانات المورد: " + e.getMessage());
            }
        } else {
            showAlert("خطأ في التحديد", "لم يتم تحديد مورد", "الرجاء تحديد مورد من الجدول لتحديثه.");
        }
    }
    
    @FXML
    private void handleDelete() {
        Supplier selectedSupplier = supplierTable.getSelectionModel().getSelectedItem();
        if (selectedSupplier != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("تأكيد الحذف");
            confirmation.setHeaderText("هل أنت متأكد من حذف هذا المورد؟");
            confirmation.setContentText("سيتم حذف المورد: " + selectedSupplier.getName());
            
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    warehouseManager.removeSupplier(selectedSupplier.getId());
                    refreshSupplierTable();
                    clearFields();
                    showAlert("نجاح", "تم الحذف بنجاح", "تم حذف المورد '" + selectedSupplier.getName() + "'.");
                } catch (Exception e) {
                    showAlert("خطأ", "فشل في الحذف", "تعذر حذف المورد: " + e.getMessage());
                }
            }
        } else {
            showAlert("خطأ في التحديد", "لم يتم تحديد مورد", "الرجاء تحديد مورد من الجدول لحذفه.");
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        List<Supplier> searchResults = warehouseManager.searchSuppliersByName(searchTerm);
        supplierList.setAll(searchResults);
    }
    
    private void clearFields() {
        nameField.clear();
        contactPersonField.clear();
        contactInfoField.clear();
        addressField.clear();
        supplierTable.getSelectionModel().clearSelection();
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
