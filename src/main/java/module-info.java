module com.queomedia.scheel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires org.apache.commons.io;
    requires java.logging;

    opens com.queomedia.scheel to javafx.fxml;
    exports com.queomedia.scheel;
}