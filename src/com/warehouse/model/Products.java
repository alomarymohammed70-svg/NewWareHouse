package com.warehouse.model;

/**
 * يمثل منتج في المخزن.
 */
public class Products {
    private String barcode;
    private String name;
    private int quantity;
    private double price;
    private double purchasePrice;
    private double minSellingPrice;
    private int minQuantityThreshold;

    public Products(String barcode, String name, int quantity, double price, 
                   double purchasePrice, double minSellingPrice, int minQuantityThreshold) {
        this.barcode = barcode;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.purchasePrice = purchasePrice;
        this.minSellingPrice = minSellingPrice;
        this.minQuantityThreshold = minQuantityThreshold;
    }

    // Getters
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getPurchasePrice() { return purchasePrice; }
    public double getMinSellingPrice() { return minSellingPrice; }
    public int getMinQuantityThreshold() { return minQuantityThreshold; }

    // Setters
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setMinSellingPrice(double minSellingPrice) { this.minSellingPrice = minSellingPrice; }
    public void setMinQuantityThreshold(int minQuantityThreshold) { this.minQuantityThreshold = minQuantityThreshold; }

    public boolean isLowStock() {
        return this.quantity < this.minQuantityThreshold;
    }

    public double calculateProfitPerItem() {
        return this.price - this.purchasePrice;
    }

    public boolean isPriceValid() {
        return this.price >= this.minSellingPrice;
    }
}