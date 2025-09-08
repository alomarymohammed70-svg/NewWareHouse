package com.warehouse.model;

import java.time.LocalDateTime;
import java.util.List;

public class Invoice {
    private int invoiceId;
    private double totalAmount;
    private LocalDateTime saleDateTime;
    private List<SaleItem> saleItems;
    private Customers customer;

    public Invoice(int invoiceId, double totalAmount, List<SaleItem> saleItems, LocalDateTime saleDateTime, Customers customer) {
        this.invoiceId = invoiceId;
        this.totalAmount = totalAmount;
        this.saleItems = saleItems;
        this.saleDateTime = saleDateTime;
        this.customer = customer;
    }

    public Invoice(int invoiceId, double totalAmount, List<SaleItem> saleItems, Customers customer) {
        this(invoiceId, totalAmount, saleItems, LocalDateTime.now(), customer);
    }

    // Getters
    public int getInvoiceId() { return invoiceId; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getSaleDateTime() { return saleDateTime; }
    public List<SaleItem> getSaleItems() { return saleItems; }
    public Customers getCustomer() { return customer; }
}