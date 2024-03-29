package com.queomedia.scheel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.nio.file.Path;
import java.nio.file.Files;

public class PassFortify extends Application {
    @Override
    public void start(final Stage stage) throws Exception {
        String mpassfile = "MPass.txt";
        Path masterpass = Path.of(mpassfile);
        if (Files.exists(masterpass)) {
            FXMLLoader fxmlLoader = new FXMLLoader(PassFortify.class.getResource("passwordFound.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.getIcons().add(new Image("https://i.ibb.co/KhSS1G9/icon.jpg"));
            stage.setTitle("PassFortify v1.0");
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.show();
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(PassFortify.class.getResource("noPasswordFound.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.getIcons().add(new Image("https://i.ibb.co/KhSS1G9/icon.jpg"));
            stage.setTitle("PassFortify v1.0");
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.show();
        }
    }

    /**
     * This method launches the application. It serves as the entry point when the program is executed, especially when
     * running the application from a JAR (Java Archive) file. The launch() method is typically used in JavaFX applications
     * to initialize and start the graphical user interface.
     *
     * @param args Command-line arguments passed to the application (unused in this context).
     */
    public static void main(final String[] args) {
        launch();
    }
}
