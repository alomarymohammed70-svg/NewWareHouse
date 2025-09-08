package com.warehouse.service;

import com.warehouse.model.Customers;
import com.warehouse.model.Products;
import com.warehouse.model.Supplier;
import com.warehouse.model.Transaction;
import com.warehouse.model.Invoice;
import com.warehouse.model.SaleItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class WarehouseManager {

    private Connection connection;

    public WarehouseManager() {
        connect();
    }

    private void connect() {
        try {
            System.out.println("✅ محاولة الاتصال بقاعدة البيانات...");
            
            // تحميل درايفر MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String url = "jdbc:mysql://localhost:3306/warehouse_db?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = ""; // ضع كلمة المرور هنا إذا كانت موجودة
            
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ تم الاتصال بقاعدة بيانات MySQL بنجاح.");
            
            initializeDatabase();
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ خطأ: لم يتم العثور على درايفر MySQL");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ فشل الاتصال بقاعدة البيانات: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            System.out.println("✅ جاري تهيئة قاعدة البيانات...");
            
            // إنشاء قاعدة البيانات إذا لم تكن موجودة
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS warehouse_db");
            stmt.executeUpdate("USE warehouse_db");
            
            // إنشاء جدول المنتجات
            String createProductsTable = "CREATE TABLE IF NOT EXISTS products ("
                    + "barcode VARCHAR(255) PRIMARY KEY,"
                    + "name VARCHAR(255) NOT NULL,"
                    + "quantity INT NOT NULL,"
                    + "price DOUBLE NOT NULL,"
                    + "purchase_price DOUBLE NOT NULL,"
                    + "min_selling_price DOUBLE NOT NULL,"
                    + "min_quantity_threshold INT NOT NULL"
                    + ")";
            stmt.executeUpdate(createProductsTable);
            
            // إنشاء جدول العملاء
            String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(255) NOT NULL,"
                    + "contact_person VARCHAR(255),"
                    + "contact_info VARCHAR(255),"
                    + "address VARCHAR(255)"
                    + ")";
            stmt.executeUpdate(createCustomersTable);
            
            // إنشاء جدول الموردين
            String createSuppliersTable = "CREATE TABLE IF NOT EXISTS suppliers ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(255) NOT NULL,"
                    + "contact_person VARCHAR(255),"
                    + "contact_info VARCHAR(255),"
                    + "address VARCHAR(255)"
                    + ")";
            stmt.executeUpdate(createSuppliersTable);
            
            // إنشاء جدول المعاملات
            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "product_barcode VARCHAR(255) NOT NULL,"
                    + "quantity INT NOT NULL,"
                    + "type VARCHAR(50) NOT NULL,"
                    + "price DOUBLE NOT NULL,"
                    + "customer_id INT NULL,"
                    + "supplier_id INT NULL,"
                    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,"
                    + "FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL"
                    + ")";
            stmt.executeUpdate(createTransactionsTable);
            
            // إنشاء جدول الفواتير
            String createInvoicesTable = "CREATE TABLE IF NOT EXISTS invoices ("
                    + "invoice_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "total_amount DOUBLE NOT NULL,"
                    + "sale_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "customer_id INT NULL,"
                    + "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL"
                    + ")";
            stmt.executeUpdate(createInvoicesTable);
            
            // إنشاء جدول عناصر الفاتورة
            String createInvoiceItemsTable = "CREATE TABLE IF NOT EXISTS invoice_items ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "invoice_id INT NOT NULL,"
                    + "product_barcode VARCHAR(255) NOT NULL,"
                    + "product_name VARCHAR(255) NOT NULL,"
                    + "quantity INT NOT NULL,"
                    + "sale_price DOUBLE NOT NULL,"
                    + "total_item_price DOUBLE NOT NULL,"
                    + "FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE,"
                    + "FOREIGN KEY (product_barcode) REFERENCES products(barcode) ON DELETE CASCADE"
                    + ")";
            stmt.executeUpdate(createInvoiceItemsTable);
            
            System.out.println("✅ تم إنشاء/التأكد من الجداول بنجاح.");
            
        } catch (SQLException e) {
            System.err.println("❌ خطأ في تهيئة قاعدة البيانات: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ تم قطع الاتصال بقاعدة البيانات.");
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ عند قطع الاتصال بقاعدة البيانات: " + e.getMessage());
        }
    }

    // Product Operations
    public void addProduct(Products product) {
        String sql = "INSERT INTO products (barcode, name, quantity, price, purchase_price, min_selling_price, min_quantity_threshold) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getBarcode());
            pstmt.setString(2, product.getName());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setDouble(5, product.getPurchasePrice());
            pstmt.setDouble(6, product.getMinSellingPrice());
            pstmt.setInt(7, product.getMinQuantityThreshold());
            pstmt.executeUpdate();
            System.out.println("✅ تم إضافة المنتج: " + product.getName());
        } catch (SQLException e) {
            System.err.println("❌ خطأ في إضافة المنتج: " + e.getMessage());
            throw new RuntimeException("فشل في إضافة المنتج: " + e.getMessage());
        }
    }

    public void updateProduct(Products product) {
        String sql = "UPDATE products SET name = ?, quantity = ?, price = ?, purchase_price = ?, min_selling_price = ?, min_quantity_threshold = ? WHERE barcode = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setInt(2, product.getQuantity());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setDouble(4, product.getPurchasePrice());
            pstmt.setDouble(5, product.getMinSellingPrice());
            pstmt.setInt(6, product.getMinQuantityThreshold());
            pstmt.setString(7, product.getBarcode());
            pstmt.executeUpdate();
            System.out.println("✅ تم تحديث المنتج: " + product.getName());
        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث المنتج: " + e.getMessage());
            throw new RuntimeException("فشل في تحديث المنتج: " + e.getMessage());
        }
    }

    public void removeProduct(String barcode) {
        String sql = "DELETE FROM products WHERE barcode = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ تم حذف المنتج بالباركود: " + barcode);
            } else {
                System.out.println("❌ المنتج غير موجود: " + barcode);
                throw new RuntimeException("المنتج غير موجود: " + barcode);
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في حذف المنتج: " + e.getMessage());
            throw new RuntimeException("فشل في حذف المنتج: " + e.getMessage());
        }
    }
    
    public boolean sellProduct(String barcode, int quantity, double sellingPrice, int customerId) {
    String selectSql = "SELECT * FROM products WHERE barcode = ?";
    String updateSql = "UPDATE products SET quantity = quantity - ? WHERE barcode = ?";
    String transactionSql = "INSERT INTO transactions (product_barcode, quantity, type, price) VALUES (?, ?, 'Sell', ?)";
    
    try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
         PreparedStatement updateStmt = connection.prepareStatement(updateSql);
         PreparedStatement transactionStmt = connection.prepareStatement(transactionSql)) {
        
        // التحقق من المنتج
        selectStmt.setString(1, barcode);
        ResultSet rs = selectStmt.executeQuery();
        
        if (rs.next()) {
            int currentQuantity = rs.getInt("quantity");
            double minSellingPrice = rs.getDouble("min_selling_price");
            
            // التحقق من الكمية
            if (currentQuantity < quantity) {
                System.out.println("❌ خطأ: الكمية المطلوبة (" + quantity + ") غير متوفرة في المخزون (" + currentQuantity + ")");
                return false;
            }
            
            // التحقق من السعر
            if (sellingPrice < minSellingPrice) {
                System.out.println("❌ خطأ: سعر البيع (" + sellingPrice + ") أقل من الحد الأدنى المسموح (" + minSellingPrice + ")");
                return false;
            }
            
            // تحديث الكمية
            updateStmt.setInt(1, quantity);
            updateStmt.setString(2, barcode);
            updateStmt.executeUpdate();
            
            // تسجيل المعاملة (بدون customer_id)
            transactionStmt.setString(1, barcode);
            transactionStmt.setInt(2, quantity);
            transactionStmt.setDouble(3, sellingPrice);
            transactionStmt.executeUpdate();
            
            System.out.println("✅ تم بيع " + quantity + " من المنتج: " + rs.getString("name"));
            return true;
        } else {
            System.out.println("❌ خطأ: المنتج غير موجود: " + barcode);
            return false;
        }
    } catch (SQLException e) {
        System.err.println("❌ خطأ في عملية البيع: " + e.getMessage());
        return false;
    }
}
    
    public int createInvoice(double totalAmount, int customerId, List<SaleItem> saleItems) {
        String invoiceSql = "INSERT INTO invoices (total_amount, customer_id) VALUES (?, ?)";
        String itemsSql = "INSERT INTO invoice_items (invoice_id, product_barcode, product_name, quantity, sale_price, total_item_price) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement invoiceStmt = connection.prepareStatement(invoiceSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement itemsStmt = connection.prepareStatement(itemsSql)) {
            
            // إنشاء الفاتورة
            invoiceStmt.setDouble(1, totalAmount);
            invoiceStmt.setInt(2, customerId);
            invoiceStmt.executeUpdate();
            
            // الحصول على ID الفاتورة المولدة
            int invoiceId = 0;
            try (ResultSet generatedKeys = invoiceStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invoiceId = generatedKeys.getInt(1);
                }
            }
            
            // إضافة عناصر الفاتورة
            for (SaleItem item : saleItems) {
                itemsStmt.setInt(1, invoiceId);
                itemsStmt.setString(2, item.getBarcode());
                itemsStmt.setString(3, item.getProductName());
                itemsStmt.setInt(4, item.getQuantity());
                itemsStmt.setDouble(5, item.getSalePrice());
                itemsStmt.setDouble(6, item.getTotalItemPrice());
                itemsStmt.addBatch();
            }
            
            itemsStmt.executeBatch();
            System.out.println("✅ تم إنشاء فاتورة رقم: " + invoiceId);
            return invoiceId;
            
        } catch (SQLException e) {
            System.err.println("❌ خطأ في إنشاء الفاتورة: " + e.getMessage());
            return -1;
        }
    }
    
    public Invoice getInvoiceById(int invoiceId) {
        String sql = "SELECT i.*, c.name as customer_name FROM invoices i LEFT JOIN customers c ON i.customer_id = c.id WHERE i.invoice_id = ?";
        String itemsSql = "SELECT * FROM invoice_items WHERE invoice_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             PreparedStatement itemsStmt = connection.prepareStatement(itemsSql)) {
            
            pstmt.setInt(1, invoiceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double totalAmount = rs.getDouble("total_amount");
                LocalDateTime saleDateTime = rs.getTimestamp("sale_date_time").toLocalDateTime();
                int customerId = rs.getInt("customer_id");
                String customerName = rs.getString("customer_name");
                
                // الحصول على عناصر الفاتورة
                itemsStmt.setInt(1, invoiceId);
                ResultSet itemsRs = itemsStmt.executeQuery();
                
                List<SaleItem> saleItems = new ArrayList<>();
                while (itemsRs.next()) {
                    SaleItem item = new SaleItem(
                        itemsRs.getString("product_barcode"),
                        itemsRs.getString("product_name"),
                        itemsRs.getInt("quantity"),
                        itemsRs.getDouble("sale_price")
                    );
                    saleItems.add(item);
                }
                
                return new Invoice(invoiceId, totalAmount, saleItems, saleDateTime, null);
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الفاتورة: " + e.getMessage());
        }
        return null;
    }
    
    public void buyProduct(String barcode, int quantity, int supplierId) {
        String updateSql = "UPDATE products SET quantity = quantity + ? WHERE barcode = ?";
        String transactionSql = "INSERT INTO transactions (product_barcode, quantity, type, price, supplier_id) VALUES (?, ?, 'Buy', 0, ?)";
        
        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql);
             PreparedStatement transactionStmt = connection.prepareStatement(transactionSql)) {
            
            // تحديث الكمية
            updateStmt.setInt(1, quantity);
            updateStmt.setString(2, barcode);
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // تسجيل المعاملة
                transactionStmt.setString(1, barcode);
                transactionStmt.setInt(2, quantity);
                transactionStmt.setInt(3, supplierId);
                transactionStmt.executeUpdate();
                
                System.out.println("✅ تم شراء " + quantity + " من المنتج بالباركود: " + barcode);
            } else {
                System.out.println("❌ خطأ: المنتج غير موجود: " + barcode);
                throw new RuntimeException("المنتج غير موجود: " + barcode);
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في عملية الشراء: " + e.getMessage());
            throw new RuntimeException("فشل في عملية الشراء: " + e.getMessage());
        }
    }

    public void adjustInventory(String barcode, int quantity, String reason) {
        String updateSql = "UPDATE products SET quantity = quantity + ? WHERE barcode = ?";
        String transactionSql = "INSERT INTO transactions (product_barcode, quantity, type, price) VALUES (?, ?, 'Adjust', 0)";
        
        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql);
             PreparedStatement transactionStmt = connection.prepareStatement(transactionSql)) {
            
            // تحديث الكمية
            updateStmt.setInt(1, quantity);
            updateStmt.setString(2, barcode);
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // تسجيل المعاملة
                transactionStmt.setString(1, barcode);
                transactionStmt.setInt(2, Math.abs(quantity));
                transactionStmt.executeUpdate();
                
                String action = quantity > 0 ? "زيادة" : "نقص";
                System.out.println("✅ تم " + action + " المخزون للمنتج: " + barcode + " - السبب: " + reason);
            } else {
                System.out.println("❌ خطأ: المنتج غير موجود: " + barcode);
                throw new RuntimeException("المنتج غير موجود: " + barcode);
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في تعديل المخزون: " + e.getMessage());
            throw new RuntimeException("فشل في تعديل المخزون: " + e.getMessage());
        }
    }

    public List<Products> getAllProducts() {
        List<Products> productsList = new ArrayList<>();
        String sql = "SELECT * FROM products";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                productsList.add(new Products(
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("purchase_price"),
                    rs.getDouble("min_selling_price"),
                    rs.getInt("min_quantity_threshold")
                ));
            }
            System.out.println("✅ تم تحميل " + productsList.size() + " منتج من قاعدة البيانات");
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب المنتجات: " + e.getMessage());
        }
        return productsList;
    }

    public List<Products> searchProductsByName(String name) {
        List<Products> productsList = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productsList.add(new Products(
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("purchase_price"),
                    rs.getDouble("min_selling_price"),
                    rs.getInt("min_quantity_threshold")
                ));
            }
            System.out.println("✅ تم العثور على " + productsList.size() + " منتج للبحث: " + name);
        } catch (SQLException e) {
            System.err.println("❌ خطأ في البحث عن المنتجات: " + e.getMessage());
        }
        return productsList;
    }

    public List<Products> getBestSellingProducts() {
        List<Products> bestSellers = new ArrayList<>();
        String sql = "SELECT p.*, SUM(t.quantity) as total_sold " +
                     "FROM products p " +
                     "JOIN transactions t ON p.barcode = t.product_barcode " +
                     "WHERE t.type = 'Sell' " +
                     "GROUP BY p.barcode " +
                     "ORDER BY total_sold DESC " +
                     "LIMIT 5";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bestSellers.add(new Products(
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("purchase_price"),
                    rs.getDouble("min_selling_price"),
                    rs.getInt("min_quantity_threshold")
                ));
            }
            System.out.println("✅ تم تحميل " + bestSellers.size() + " منتج الأكثر مبيعاً");
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب المنتجات الأكثر مبيعاً: " + e.getMessage());
        }
        return bestSellers;
    }

    // Customer Operations
    public void addCustomer(Customers customer) {
        String sql = "INSERT INTO customers (name, contact_person, contact_info, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getContactPerson());
            pstmt.setString(3, customer.getContactInfo());
            pstmt.setString(4, customer.getAddress());
            pstmt.executeUpdate();
            
            // الحصول على الـ ID المولد
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    System.out.println("✅ تم إضافة العميل: " + customer.getName() + " (ID: " + newId + ")");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في إضافة العميل: " + e.getMessage());
            throw new RuntimeException("فشل في إضافة العميل: " + e.getMessage());
        }
    }

    public void updateCustomer(int id, String name, String contactPerson, String contactInfo, String address) {
        String sql = "UPDATE customers SET name = ?, contact_person = ?, contact_info = ?, address = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contactPerson);
            pstmt.setString(3, contactInfo);
            pstmt.setString(4, address);
            pstmt.setInt(5, id);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ تم تحديث العميل: " + name);
            } else {
                System.out.println("❌ العميل غير موجود: ID " + id);
                throw new RuntimeException("العميل غير موجود: ID " + id);
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث العميل: " + e.getMessage());
            throw new RuntimeException("فشل في تحديث العميل: " + e.getMessage());
        }
    }

    public void removeCustomer(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ تم حذف العميل بالـ ID: " + id);
            } else {
                System.out.println("❌ العميل غير موجود: ID " + id);
                throw new RuntimeException("العميل غير موجود: ID " + id);
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في حذف العميل: " + e.getMessage());
            throw new RuntimeException("فشل في حذف العميل: " + e.getMessage());
        }
    }

    public List<Customers> getAllCustomers() {
        List<Customers> customersList = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customersList.add(new Customers(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact_person"),
                    rs.getString("contact_info"),
                    rs.getString("address")
                ));
            }
            System.out.println("✅ تم تحميل " + customersList.size() + " عميل من قاعدة البيانات");
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب العملاء: " + e.getMessage());
        }
        return customersList;
    }
    
    public List<Customers> searchCustomersByName(String name) {
        List<Customers> customersList = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE name LIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customersList.add(new Customers(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact_person"),
                    rs.getString("contact_info"),
                    rs.getString("address")
                ));
            }
            System.out.println("✅ تم العثور على " + customersList.size() + " عميل للبحث: " + name);
        } catch (SQLException e) {
            System.err.println("❌ خطأ في البحث عن العملاء: " + e.getMessage());
        }
        return customersList;
    }

    // Supplier Operations
    public void addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, contact_person, contact_info, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getContactPerson());
            pstmt.setString(3, supplier.getContactInfo());
            pstmt.setString(4, supplier.getAddress());
            pstmt.executeUpdate();
            
            // الحصول على الـ ID المولد
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    System.out.println("✅ تم إضافة المورد: " + supplier.getName() + " (ID: " + newId + ")");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في إضافة المورد: " + e.getMessage());
            throw new RuntimeException("فشل في إضافة المورد: " + e.getMessage());
        }
    }

    public void updateSupplier(int id, String name, String contactPerson, String contactInfo, String address) {
        String sql = "UPDATE suppliers SET name = ?, contact_person = ?, contact_info = ?, address = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contactPerson);
            pstmt.setString(3, contactInfo);
            pstmt.setString(4, address);
            pstmt.setInt(5, id);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ تم تحديث المورد: " + name);
            } else {
                System.out.println("❌ المورد غير موجود: ID " + id);
                throw new RuntimeException("المورد غير موجود: ID " + id);
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث المورد: " + e.getMessage());
            throw new RuntimeException("فشل في تحديث المورد: " + e.getMessage());
        }
    }

    public void removeSupplier(int id) {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ تم حذف المورد بالـ ID: " + id);
            } else {
                System.out.println("❌ المورد غير موجود: ID " + id);
                throw new RuntimeException("المورد غير موجود: ID " + id);
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في حذف المورد: " + e.getMessage());
            throw new RuntimeException("فشل في حذف المورد: " + e.getMessage());
        }
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliersList = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliersList.add(new Supplier(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact_person"),
                    rs.getString("contact_info"),
                    rs.getString("address")
                ));
            }
            System.out.println("✅ تم تحميل " + suppliersList.size() + " مورد من قاعدة البيانات");
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الموردين: " + e.getMessage());
        }
        return suppliersList;
    }

    public List<Supplier> searchSuppliersByName(String name) {
        List<Supplier> suppliersList = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE name LIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                suppliersList.add(new Supplier(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact_person"),
                    rs.getString("contact_info"),
                    rs.getString("address")
                ));
            }
            System.out.println("✅ تم العثور على " + suppliersList.size() + " مورد للبحث: " + name);
        } catch (SQLException e) {
            System.err.println("❌ خطأ في البحث عن الموردين: " + e.getMessage());
        }
        return suppliersList;
    }

    // Transaction Operations
 public List<Transaction> getAllTransactions() {
    List<Transaction> transactionsList = new ArrayList<>();
    // استعلام مبسط بدون الأعمدة المفقودة
    String sql = "SELECT t.id, t.product_barcode, t.quantity, t.type, t.price, t.timestamp, p.name as product_name " +
                 "FROM transactions t " +
                 "LEFT JOIN products p ON t.product_barcode = p.barcode " +
                 "ORDER BY t.timestamp DESC";
    
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            Transaction transaction = new Transaction(
                rs.getInt("id"),
                rs.getString("product_barcode"),
                rs.getInt("quantity"),
                rs.getString("type"),
                rs.getDouble("price")
            );
            transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            transaction.setProductName(rs.getString("product_name"));
            transactionsList.add(transaction);
        }
        System.out.println("✅ تم تحميل " + transactionsList.size() + " معاملة");
    } catch (SQLException e) {
        System.err.println("❌ خطأ في جلب المعاملات: " + e.getMessage());
    }
    return transactionsList;
}
    public List<Transaction> getTransactionsByType(String type) {
    List<Transaction> transactionsList = new ArrayList<>();
    String sql = "SELECT t.id, t.product_barcode, t.quantity, t.type, t.price, t.timestamp, p.name as product_name " +
                 "FROM transactions t " +
                 "LEFT JOIN products p ON t.product_barcode = p.barcode " +
                 "WHERE t.type = ? " +
                 "ORDER BY t.timestamp DESC";
    
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, type);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Transaction transaction = new Transaction(
                rs.getInt("id"),
                rs.getString("product_barcode"),
                rs.getInt("quantity"),
                rs.getString("type"),
                rs.getDouble("price")
            );
            transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            transaction.setProductName(rs.getString("product_name"));
            transactionsList.add(transaction);
        }
        System.out.println("✅ تم تحميل " + transactionsList.size() + " معاملة من نوع: " + type);
    } catch (SQLException e) {
        System.err.println("❌ خطأ في جلب المعاملات: " + e.getMessage());
    }
    return transactionsList;
}

    public List<Products> getLowStockProducts() {
        List<Products> lowStockProducts = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity < min_quantity_threshold";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lowStockProducts.add(new Products(
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("purchase_price"),
                    rs.getDouble("min_selling_price"),
                    rs.getInt("min_quantity_threshold")
                ));
            }
            System.out.println("⚠️ المنتجات ذات المخزون المنخفض: " + lowStockProducts.size() + " منتج");
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب المنتجات ذات المخزون المنخفض: " + e.getMessage());
        }
        return lowStockProducts;
    }

    public void refreshSuppliers() {
        System.out.println("🔄 تحديث بيانات الموردين");
    }
    
    public void refreshProducts() {
        System.out.println("🔄 تحديث بيانات المنتجات");
    }
    
    public void refreshCustomers() {
        System.out.println("🔄 تحديث بيانات العملاء");
    }
    
    // دالة مساعدة للبحث عن منتج بالباركود
    public Products findProductByBarcode(String barcode) {
        String sql = "SELECT * FROM products WHERE barcode = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Products(
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("purchase_price"),
                    rs.getDouble("min_selling_price"),
                    rs.getInt("min_quantity_threshold")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في البحث عن المنتج: " + e.getMessage());
        }
        return null;
    }
    
    // دالة للحصول على تقرير المخزون المنخفض
    public String getLowStockReport() {
        StringBuilder report = new StringBuilder();
        List<Products> lowStockProducts = getLowStockProducts();
        
        report.append("📋 تقرير المخزون المنخفض\n");
        report.append("══════════════════════════════\n\n");
        
        if (lowStockProducts.isEmpty()) {
            report.append("✅ لا توجد منتجات ناقصة في المخزون\n");
        } else {
            report.append(String.format("⚠️ عدد المنتجات الناقصة: %d\n\n", lowStockProducts.size()));
            report.append("المنتجات الناقصة:\n");
            report.append("──────────────────────────────\n");
            
            for (Products product : lowStockProducts) {
                int deficit = product.getMinQuantityThreshold() - product.getQuantity();
                report.append(String.format("• %s (%s) - المتوفر: %d - المطلوب: %d - النقص: %d\n",
                    product.getName(), product.getBarcode(), product.getQuantity(),
                    product.getMinQuantityThreshold(), deficit));
            }
        }
        
        return report.toString();
    }

    // دالة للحصول على إحصائيات المخزون
    public String getInventoryStatistics() {
        StringBuilder stats = new StringBuilder();
        String sql = "SELECT " +
                     "COUNT(*) as total_products, " +
                     "SUM(quantity) as total_quantity, " +
                     "SUM(quantity * price) as total_value, " +
                     "SUM(CASE WHEN quantity < min_quantity_threshold THEN 1 ELSE 0 END) as low_stock_count " +
                     "FROM products";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                stats.append("📊 إحصائيات المخزون:\n");
                stats.append("══════════════════════════════\n");
                stats.append("• إجمالي المنتجات: ").append(rs.getInt("total_products")).append("\n");
                stats.append("• إجمالي الكمية: ").append(rs.getInt("total_quantity")).append("\n");
                stats.append("• القيمة الإجمالية: ").append(String.format("%.2f", rs.getDouble("total_value"))).append(" ر.س\n");
                stats.append("• المنتجات منخفضة المخزون: ").append(rs.getInt("low_stock_count")).append("\n");
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب إحصائيات المخزون: " + e.getMessage());
            stats.append("❌ تعذر تحميل إحصائيات المخزون: ").append(e.getMessage());
        }
        
        return stats.toString();
    }

    // دالة للتحقق من اتصال قاعدة البيانات
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // دالة لإعادة الاتصال بقاعدة البيانات
    public void reconnect() {
        disconnect();
        connect();
    }

    // دالة لتنفيذ استعلام SQL مباشر (لأغراض التصحيح)
    public void executeSQL(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ تم تنفيذ الاستعلام بنجاح");
        } catch (SQLException e) {
            System.err.println("❌ خطأ في تنفيذ الاستعلام: " + e.getMessage());
        }
    }

    // دالة للحصول على تاريخ آخر معاملة
    public String getLastTransactionDate() {
        String sql = "SELECT MAX(timestamp) as last_transaction FROM transactions";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getString("last_transaction");
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب تاريخ آخر معاملة: " + e.getMessage());
        }
        return "لا توجد معاملات";
    }

    // دالة للحصول على إجمالي المبيعات اليوم
    public double getTodaySales() {
        String sql = "SELECT SUM(price * quantity) as total_sales " +
                     "FROM transactions " +
                     "WHERE type = 'Sell' AND DATE(timestamp) = CURDATE()";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total_sales");
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب إجمالي المبيعات: " + e.getMessage());
        }
        return 0.0;
    }

    // دالة للحصول على المنتجات التي لم يتم بيعها أبداً
    public List<Products> getNeverSoldProducts() {
        List<Products> neverSoldProducts = new ArrayList<>();
        String sql = "SELECT p.* FROM products p " +
                     "LEFT JOIN transactions t ON p.barcode = t.product_barcode AND t.type = 'Sell' " +
                     "WHERE t.id IS NULL";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                neverSoldProducts.add(new Products(
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("purchase_price"),
                    rs.getDouble("min_selling_price"),
                    rs.getInt("min_quantity_threshold")
                ));
            }
            System.out.println("📦 المنتجات التي لم يتم بيعها: " + neverSoldProducts.size() + " منتج");
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب المنتجات التي لم يتم بيعها: " + e.getMessage());
        }
        return neverSoldProducts;
    }

    // دالة للتحقق من وجود منتج بالباركود
    public boolean productExists(String barcode) {
        String sql = "SELECT COUNT(*) as count FROM products WHERE barcode = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في التحقق من وجود المنتج: " + e.getMessage());
        }
        return false;
    }

    // دالة للحصول على إجمالي عدد المعاملات
    public int getTotalTransactions() {
        String sql = "SELECT COUNT(*) as total FROM transactions";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب إجمالي المعاملات: " + e.getMessage());
        }
        return 0;
    }

    // دالة للحصول على نسبة المنتجات منخفضة المخزون
    public double getLowStockPercentage() {
        String sql = "SELECT " +
                     "COUNT(*) as total_products, " +
                     "SUM(CASE WHEN quantity < min_quantity_threshold THEN 1 ELSE 0 END) as low_stock_count " +
                     "FROM products";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int total = rs.getInt("total_products");
                int lowStock = rs.getInt("low_stock_count");
                if (total > 0) {
                    return (lowStock * 100.0) / total;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في حساب نسبة المخزون المنخفض: " + e.getMessage());
        }
        return 0.0;
    }
    
    // دالة للحصول على إحصائيات المبيعات
    public Map<String, Object> getSalesStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        String sql = "SELECT " +
                     "COUNT(*) as total_sales, " +
                     "SUM(quantity * price) as total_revenue, " +
                     "AVG(price) as average_sale_price, " +
                     "MAX(price * quantity) as max_sale_value " +
                     "FROM transactions WHERE type = 'Sell'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                statistics.put("total_sales", rs.getInt("total_sales"));
                statistics.put("total_revenue", rs.getDouble("total_revenue"));
                statistics.put("average_sale_price", rs.getDouble("average_sale_price"));
                statistics.put("max_sale_value", rs.getDouble("max_sale_value"));
            }
        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب إحصائيات المبيعات: " + e.getMessage());
        }
        return statistics;
    }
    
    // دالة لإنشاء نسخة احتياطية من قاعدة البيانات
    public boolean backupDatabase(String backupPath) {
        try {
            String executeCmd = "mysqldump -u root -p warehouse_db -r " + backupPath;
            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();
            
            if (processComplete == 0) {
                System.out.println("✅ تم إنشاء نسخة احتياطية بنجاح: " + backupPath);
                return true;
            } else {
                System.out.println("❌ فشل في إنشاء النسخة الاحتياطية");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ خطأ في إنشاء النسخة الاحتياطية: " + e.getMessage());
            return false;
        }
    }
}