package com.queomedia.scheel;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

public class AddAccountController {

    /**
     * TextField used to enter a service that the user would like to add.
     */
    @FXML
    private TextField serviceField;

    /**
     * TextField used to enter a username that the user would like to add.
     */
    @FXML
    private TextField usernameField;

    /**
     * PasswordField used to enter a passwort that the user would like to add.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * PasswordField where user has to enter his current master password.
     */
    @FXML
    private PasswordField mPassField;

    /**
     * This method is called when the "Generate Password" button is clicked. It generates a random password using the
     * PasswordTools class, sets the generated password in a text field, and copies the password to the system clipboard.
     */
    public void onGeneratePasswordClick() {
        String password = PasswordTools.passwordGenerator();
        passwordField.setText(password); //Placing generated password into the passwordField
        final Clipboard clipboard = Clipboard.getSystemClipboard(); //initialising clipboard
        final ClipboardContent content = new ClipboardContent();
        content.putString(password);
        clipboard.setContent(content); //Placing generated password into clipboard
    }

    /**
     * This method is called when the "Save" button is clicked. It retrieves the master password, service, username,
     * and password from the corresponding fields. It checks if the required fields are not empty, and if the master password
     * is correct. If conditions are met, it adds the account information to the respective files using the PasswordTools class.
     * Finally, it closes the current window.
     *
     * @throws Exception If there is an issue with checking the master password or adding the account information.
     */
    public void onSaveClick() throws Exception {
        String masterPassword = mPassField.getText();
        String service = serviceField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        //Checking if one of the inputFields is left empty
        if (!service.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            //Adding account details to the respective files
            if (PasswordTools.checkMasterpassword(masterPassword)) {
                String serviceLocation = "Services.txt";
                addAccount(masterPassword, service, serviceLocation);
                String usernameLocation = "Usernames.txt";
                addAccount(masterPassword, username, usernameLocation);
                String passwordLocation = "Passwords.txt";
                addAccount(masterPassword, password, passwordLocation);
            }
        }
        ((Stage) serviceField.getScene().getWindow()).close(); //Closing the form
    }

    /**
     * Adds an account to a specified file after decrypting and saving its contents.
     * This method takes the master password, account information to be added, and the file path as parameters.
     * It first checks if the file exists. If the file exists, it decrypts and saves its contents using the PasswordTools class.
     * Then, it adds the new account information to the file using the PasswordTools class.
     *
     * @param masterPassword The master password used for decryption and encryption.
     * @param accountToAdd   The account information to be added.
     * @param path           The file path where the account information is stored.
     * @throws Exception If there is an issue with decrypting and saving the file contents or adding the account information.
     */
    private void addAccount(final String masterPassword, final String accountToAdd, final String path)
            throws Exception {
        final Path file = Path.of(path); //Getting a path object
        //Decrypting the file if it exists
        if (Files.exists(file)) {
            PasswordTools.decryptAndSave(path, masterPassword);
        }
        PasswordTools.addData(path, accountToAdd, masterPassword); //Adding data and re-encrypting the file
    }

    /**
     * This method is called when the "Exit" button is clicked. It closes the current application window.
     */
    @FXML
    void onExitButtonClick() {
        ((Stage) serviceField.getScene().getWindow()).close(); //Closing the form.
    }

    /**
     * Handles the pressing of a pane to initiate dragging.
     * This method is typically associated with an event listener for mouse pressing on a pane. It captures
     * the initial coordinates of the mouse press relative to the application window, allowing for proper
     * tracking of the mouse movement during dragging.
     *
     * @param me The MouseEvent representing the mouse press action.
     */
    @FXML
    void panePressed(final MouseEvent me) {
        Stage stage = (Stage) serviceField.getScene().getWindow(); //Getting stage object from the label
        Delta.x = stage.getX() - me.getScreenX(); //Setting the x position of the stage
        Delta.y = stage.getY() - me.getScreenY(); //Setting the y position of the stage
    }

    /**
     * Handles the dragging of the application window when a pane is dragged.
     * This method is typically associated with an event listener for mouse dragging on a pane. It adjusts
     * the position of the application window based on the mouse movement, allowing for window dragging.
     *
     * @param me The MouseEvent representing the dragging action.
     */
    @FXML
    void paneDragged(final MouseEvent me) {
        Stage stage = (Stage) serviceField.getScene().getWindow(); //Getting stage object from the label
        stage.setX(Delta.x + me.getScreenX()); //Setting the x position of the stage
        stage.setY(Delta.y + me.getScreenY()); //Setting the y position of the stage
    }
}
