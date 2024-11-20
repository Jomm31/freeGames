module com.example.paidg {
    requires javafx.controls; // Ensure you have added JavaFX SDK to your module path
    // Ensure you have the JavaFX SDK added to your module path
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires jdk.compiler;
    requires java.sql;


    opens com.example.paidg to javafx.fxml;
    exports com.example.paidg;
}