module com.example.oops_app {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.oops_app to javafx.fxml;
    exports com.example.oops_app;
}