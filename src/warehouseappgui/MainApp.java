package warehouseappgui;

import com.warehouse.service.WarehouseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    private WarehouseManager warehouseManager = new WarehouseManager();

    @Override
    public void start(Stage stage) throws Exception {
        // تحميل واجهة الرئيسية من المسار الصحيح
        Parent root = FXMLLoader.load(getClass().getResource("/warehouseappgui/MainView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("نظام إدارة المخازن");
        stage.show();
    }
    
    @Override
    public void stop() throws Exception {
        warehouseManager.disconnect();
        System.out.println("✅ تم إغلاق التطبيق وقطع الاتصال بقاعدة البيانات.");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}