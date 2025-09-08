package com.warehouse.view;

import com.warehouse.model.Customers;
import com.warehouse.service.WarehouseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.Optional;

public class CustomersViewController {

    @FXML private TableView<Customers> customerTable;
    @FXML private TableColumn<Customers, Integer> idColumn;
    @FXML private TableColumn<Customers, String> nameColumn;
    @FXML private TableColumn<Customers, String> contactPersonColumn;
    @FXML private TableColumn<Customers, String> contactInfoColumn;
    @FXML private TableColumn<Customers, String> addressColumn;
    
    @FXML private TextField searchField;
    @FXML private TextField nameField;
    @FXML private TextField contactPersonField;
    @FXML private TextField contactInfoField;
    @FXML private TextField addressField;

    private WarehouseManager warehouseManager;
    private ObservableList<Customers> customerList = FXCollections.observableArrayList();

    public void setWarehouseManager(WarehouseManager manager) {
        this.warehouseManager = manager;
        refreshCustomerTable();
    }

    @FXML
    public void initialize() {
        System.out.println("✅ تهيئة واجهة العملاء...");
        setupTableColumns();
        customerTable.setItems(customerList);
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
        customerTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    fillFormWithCustomerDetails(newSelection);
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

    private void refreshCustomerTable() {
        try {
            if (warehouseManager != null) {
                customerList.clear();
                List<Customers> allCustomers = warehouseManager.getAllCustomers();
                if (allCustomers != null && !allCustomers.isEmpty()) {
                    customerList.addAll(allCustomers);
                } else {
                    System.out.println("⚠️ لا توجد عملاء لعرضها");
                }
            }
        } catch (Exception e) {
            showAlert("خطأ", "فشل في التحميل", "تعذر تحميل بيانات العملاء: " + e.getMessage());
        }
    }

    private void fillFormWithCustomerDetails(Customers customer) {
        nameField.setText(customer.getName());
        contactPersonField.setText(customer.getContactPerson());
        contactInfoField.setText(customer.getContactInfo());
        addressField.setText(customer.getAddress());
    }

    @FXML
    private void handleAdd() {
        try {
            String name = nameField.getText().trim();
            String contactPerson = contactPersonField.getText().trim();
            String contactInfo = contactInfoField.getText().trim();
            String address = addressField.getText().trim();
            
            if (name.isEmpty()) {
                showAlert("خطأ في الإدخال", "بيانات ناقصة", "يرجى إدخال اسم العميل.");
                return;
            }
            
            Customers newCustomer = new Customers(0, name, contactPerson, contactInfo, address);
            warehouseManager.addCustomer(newCustomer);
            refreshCustomerTable();
            clearFields();
            showAlert("نجاح", "تمت الإضافة بنجاح", "تم إضافة العميل '" + name + "'.");
        } catch (Exception e) {
            showAlert("خطأ", "فشل في الإضافة", "تعذر إضافة العميل: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate() {
        Customers selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            try {
                String name = nameField.getText().trim();
                String contactPerson = contactPersonField.getText().trim();
                String contactInfo = contactInfoField.getText().trim();
                String address = addressField.getText().trim();
                
                if (name.isEmpty()) {
                    showAlert("خطأ في الإدخال", "بيانات ناقصة", "يرجى إدخال اسم العميل.");
                    return;
                }
                
                warehouseManager.updateCustomer(selectedCustomer.getId(), name, contactPerson, contactInfo, address);
                refreshCustomerTable();
                clearFields();
                showAlert("نجاح", "تم التحديث بنجاح", "تم تحديث بيانات العميل '" + name + "'.");
            } catch (Exception e) {
                showAlert("خطأ", "فشل في التحديث", "تعذر تحديث بيانات العميل: " + e.getMessage());
            }
        } else {
            showAlert("خطأ في التحديد", "لم يتم تحديد عميل", "الرجاء تحديد عميل من الجدول لتحديثه.");
        }
    }

    @FXML
    private void handleDelete() {
        Customers selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("تأكيد الحذف");
            confirmation.setHeaderText("هل أنت متأكد من حذف هذا العميل؟");
            confirmation.setContentText("سيتم حذف العميل: " + selectedCustomer.getName());
            
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    warehouseManager.removeCustomer(selectedCustomer.getId());
                    refreshCustomerTable();
                    clearFields();
                    showAlert("نجاح", "تم الحذف بنجاح", "تم حذف العميل '" + selectedCustomer.getName() + "'.");
                } catch (Exception e) {
                    showAlert("خطأ", "فشل في الحذف", "تعذر حذف العميل: " + e.getMessage());
                }
            }
        } else {
            showAlert("خطأ في التحديد", "لم يتم تحديد عميل", "الرجاء تحديد عميل من الجدول لحذفه.");
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        List<Customers> searchResults = warehouseManager.searchCustomersByName(searchTerm);
        customerList.setAll(searchResults);
    }
    
    private void clearFields() {
        nameField.clear();
        contactPersonField.clear();
        contactInfoField.clear();
        addressField.clear();
        customerTable.getSelectionModel().clearSelection();
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
