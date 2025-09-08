package warehouseappgui;

import com.warehouse.service.WarehouseManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.net.URL;
import java.util.Optional;

public class MainApp extends Application {
    
    private WarehouseManager warehouseManager = new WarehouseManager();

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("🚀 بدء تشغيل نظام إدارة المخازن...");
            
            // التحقق من وجود ملفات الموارد أولاً
            checkResourceFiles();
            
            // تحميل الواجهة الرئيسية بالطريقة الصحيحة
            URL fxmlUrl = getClass().getResource("/com/warehouse/view/MainView.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("❌ ملف MainView.fxml غير موجود!");
            }
            
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);
            
            // تحميل CSS إذا وجد
            URL cssUrl = getClass().getResource("/com/warehouse/view/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("✅ تم تحميل ملف CSS بنجاح");
            } else {
                System.out.println("⚠️ ملف CSS غير موجود، سيتم استخدام التنسيقات الافتراضية");
            }
            
            // إعداد النافذة
            stage.setScene(scene);
            stage.setTitle("🏪 نظام إدارة المخازن المتكامل");
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            
            // إعداد حدث الإغلاق
            stage.setOnCloseRequest(event -> {
                event.consume();
                confirmExit(stage);
            });
            
            stage.show();
            
            System.out.println("✅ تم تشغيل التطبيق بنجاح!");
            
        } catch (Exception e) {
            System.err.println("❌ فشل في تحميل الواجهة: " + e.getMessage());
            showErrorAlert("فشل في بدء التطبيق", "خطأ: " + e.getMessage());
            Platform.exit();
        }
    }

    private void checkResourceFiles() {
        System.out.println("🔍 التحقق من ملفات الموارد...");
        
        String[] requiredFiles = {
            "/com/warehouse/view/MainView.fxml",
            "/com/warehouse/view/ProductView.fxml", 
            "/com/warehouse/view/CustomersView.fxml",
            "/com/warehouse/view/SupplierView.fxml",
            "/com/warehouse/view/DataView.fxml",
            "/com/warehouse/view/styles.css"
        };
        
        for (String file : requiredFiles) {
            URL url = getClass().getResource(file);
            if (url == null) {
                System.err.println("❌ ملف مفقود: " + file);
            } else {
                System.out.println("✅ ملف موجود: " + file);
            }
        }
    }

    private void confirmExit(Stage stage) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("تأكيد الخروج");
        confirmation.setHeaderText("هل أنت متأكد من الخروج؟");
        confirmation.setContentText("سيتم إغلاق التطبيق وفصل الاتصال بقاعدة البيانات.");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("🔄 جاري إغلاق التطبيق...");
            stop();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        try {
            if (warehouseManager != null) {
                warehouseManager.disconnect();
            }
            System.out.println("✅ تم إغلاق التطبيق وتنظيف الموارد.");
        } catch (Exception e) {
            System.err.println("❌ خطأ أثناء إغلاق التطبيق: " + e.getMessage());
        }
    }
    
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("خطأ في النظام");
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        System.out.println("🌙 بدء تشغيل تطبيق إدارة المخازن...");
        launch(args);
    }
}