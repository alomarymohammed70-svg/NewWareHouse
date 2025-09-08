package com.warehouse.model;

public class SaleItem {
    private String barcode;
    private String productName;
    private int quantity;
    private double salePrice;
    private double totalItemPrice;

    public SaleItem(String barcode, String productName, int quantity, double salePrice) {
        this.barcode = barcode;
        this.productName = productName;
        this.quantity = quantity;
        this.salePrice = salePrice;
        this.totalItemPrice = quantity * salePrice;
    }

    // Getters
    public String getBarcode() {
        return barcode;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public double getTotalItemPrice() {
        return totalItemPrice;
    }
}