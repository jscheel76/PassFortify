package com.queomedia.scheel;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

public class AddAccountController {

    @FXML
    private TextField serviceField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField mPassField;

    /**
     * Location of the password file.
     */
    private final String passwordLocation = "Passwords.txt";

    /**
     * Location of the service file.
     */
    private final String serviceLocation = "Services.txt";

    /**
     * Location of the username file.
     */
    private final String usernameLocation = "Usernames.txt";

    public void onGeneratePasswordClick() {
        String password = PasswordTools.passwordGenerator();
        passwordField.setText(password);
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(password);
        clipboard.setContent(content);
    }

    public void onSaveClick() throws Exception {
        String masterPassword = mPassField.getText();
        String service = serviceField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (!service.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            if (PasswordTools.checkMasterpassword(masterPassword)) {
                addAccount(masterPassword, service, serviceLocation);
                addAccount(masterPassword, username, usernameLocation);
                addAccount(masterPassword, password, passwordLocation);
            }
        }
        ((Stage) serviceField.getScene().getWindow()).close();
    }

    public void addAccount(final String masterPassword, final String accountToAdd, final String path) throws Exception {
        final Path file = Path.of(path);
        if (Files.exists(file)) {
            PasswordTools.decryptAndSave(path, masterPassword);
        }
        PasswordTools.addData(path, accountToAdd, masterPassword);
    }

    public void onExitButtonClick() {
        ((Stage) serviceField.getScene().getWindow()).close();
    }
}
