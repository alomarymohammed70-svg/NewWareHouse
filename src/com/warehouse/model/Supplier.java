package com.warehouse.model;

public class Supplier {
    private int id;
    private String name;
    private String contactPerson;
    private String contactInfo;
    private String address;

    public Supplier(int id, String name, String contactPerson, String contactInfo, String address) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.contactInfo = contactInfo;
        this.address = address;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return name + " - " + contactPerson + " - " + contactInfo;
    }
}