package com.warehouse.model;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private String productBarcode;
    private int quantity;
    private String type;
    private double price;
    private LocalDateTime timestamp;
    private Integer customerId;
    private Integer supplierId;
    private String productName;
    private String customerName;
    private String supplierName;

    public Transaction(int id, String productBarcode, int quantity, String type, double price) {
        this.id = id;
        this.productBarcode = productBarcode;
        this.quantity = quantity;
        this.type = type;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public int getId() { return id; }
    public String getProductBarcode() { return productBarcode; }
    public int getQuantity() { return quantity; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Integer getCustomerId() { return customerId; }
    public Integer getSupplierId() { return supplierId; }
    public String getProductName() { return productName; }
    public String getCustomerName() { return customerName; }
    public String getSupplierName() { return supplierName; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProductBarcode(String productBarcode) { this.productBarcode = productBarcode; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setType(String type) { this.type = type; }
    public void setPrice(double price) { this.price = price; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}