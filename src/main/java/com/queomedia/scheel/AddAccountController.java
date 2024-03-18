package com.queomedia.scheel;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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

    @FXML
    private TextField passwordPlaintext;

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
     * Label used to return feedback to the user.
     */
    @FXML
    private Label passwordFeedback;

    @FXML
    private CheckBox passwordCheckBox;

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
        passwordFeedback.setText("Password generated and copied");
        passwordFeedback.setStyle("-fx-text-fill: #03c203;"); //Green
        passwordFeedback.setAlignment(Pos.CENTER);
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
        String password;

        if (passwordCheckBox.isSelected()) {
            password = passwordPlaintext.getText();
        } else {
            password = passwordField.getText();
        }

        //Checking if one of the inputFields is left empty
        if (!service.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            //Adding account details to the respective files
            if (PasswordTools.checkMasterpassword(masterPassword)) {
                String serviceLocation = "Services.txt";
                PasswordTools.addData(serviceLocation, service, masterPassword);
                String usernameLocation = "Usernames.txt";
                PasswordTools.addData(usernameLocation, username, masterPassword);
                String passwordLocation = "Passwords.txt";
                PasswordTools.addData(passwordLocation, password, masterPassword);

                //User feedback
                passwordFeedback.setText("Account added");
                passwordFeedback.setStyle("-fx-text-fill: #03c203;"); //Green
                passwordFeedback.setAlignment(Pos.CENTER);

                //Emptying the textFields
                serviceField.setText("");
                usernameField.setText("");
                passwordField.setText("");
                passwordPlaintext.setText("");
            } else {
                passwordFeedback.setText("Master password incorrect");
                passwordFeedback.setStyle("-fx-text-fill: red");
                passwordFeedback.setAlignment(Pos.CENTER);
            }
        } else {
            passwordFeedback.setText("At least one field left empty");
            passwordFeedback.setStyle("-fx-text-fill: red");
            passwordFeedback.setAlignment(Pos.CENTER);
        }
    }

    @FXML
    void onPasswordCheckBoxClick() {
        String fieldContent;
        if (passwordCheckBox.isSelected()) {
            passwordField.setVisible(false);
            passwordPlaintext.setVisible(true);
            fieldContent = passwordField.getText();
            passwordPlaintext.setText(fieldContent);
        } else {
            passwordPlaintext.setVisible(false);
            passwordField.setVisible(true);
            fieldContent = passwordPlaintext.getText();
            passwordField.setText(fieldContent);
        }
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
