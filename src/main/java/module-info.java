module com.example.hospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.hospital to javafx.fxml;
    exports com.example.hospital;
}