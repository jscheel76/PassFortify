module com.example.pwmanager_test {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.pwmanager_test to javafx.fxml;
    exports com.example.pwmanager_test;
}