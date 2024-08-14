package com.queomedia.scheel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Controller class for the password manager and login windows.
 * This class contains all the methods used by the password manager.
 * The main methods are direct interaction with the GUI.
 */
public class PassFortifyController {

    /**
     * Column where services are displayed.
     */
    @FXML
    private TableColumn<String, String> services;

    /**
     * Column where usernames are displayed.
     */
    @FXML
    private TableColumn<String, String> usernames;

    /**
     * Column where passwords are displayed.
     */
    @FXML
    private TableColumn<String, String> passwords;

    /**
     * The tableview, in which the columns are displayed, using the DataEntry class.
     */
    @FXML
    private TableView<DataEntry> accountTable;

    /**
     * Label used to display feedback after actions are performed.
     */
    @FXML
    private Label feedbackLabel;

    /**
     * Label used to warn the user about using the same password twice, if enabled in settings.
     */
    @FXML
    private Label warningLabel;

    /**
     * TextField used to extract master password.
     */
    @FXML
    private TextField mPasswordField2;

    /**
     * PasswordField used to extract master password in the main application.
     */
    @FXML
    private PasswordField passField;

    /**
     * TextField used for input of Username, Password, Service, New Master Password.
     */
    @FXML
    private TextField inputField;

    /**
     * Button used to confirm master password change.
     */
    @FXML
    private Button yesButton;

    /**
     * Button used to cancel master password change.
     */
    @FXML
    private Button noButton;

    /**
     * CheckMenuItem to enable setting that warns user about using multiple passwords.
     */
    @FXML
    private CheckMenuItem passwordMatch;

    /**
     * CheckMenuItem to enable the setting that hides passwords.
     */
    @FXML
    private CheckMenuItem hidePassword;

    /**
     * CheckMenuItem to enable the setting that clears the master password field after each use.
     */
    @FXML
    private CheckMenuItem clearPassword;

    /**
     * CheckMenuItem to enable the setting that allows to check if the password has been leaked on right click.
     */
    @FXML
    private CheckMenuItem checkLeak;

    /**
     * Label that warns user that he entered the wrong password.
     */
    @FXML
    private Label tryLabel;

    /**
     * Label used to display the amount of accounts the user has saved.
     */
    @FXML
    private Label accountLabel;

    /**
     * CheckBox used to allow users to hide their input, for example during a password change
     */
    @FXML
    private CheckBox inputCheckBox;

    /**
     * PasswordField used in place of the inputField, when inputBox is selected
     */
    @FXML
    private PasswordField inputPasswordField;

    /**
     * Integer determining how many tries a user has left.
     * Using 2+1 as to not use magic numbers.
     */
    private int triesLeft = 2 + 1;

    /**
     * Filtered list used to filter the tableview entries.
     */
    private FilteredList<DataEntry> filteredData;

    /**
     * Global String used to save the password strength evaluation.
     */
    private String savedPasswordStrength = "";

    /**
     * String used to save the master password.
     */
    private String mPassword;

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

    /**
     * Location of the master password file.
     */
    private final String mPassLocation = "MPass.txt";

    /**
     * Opens a new window based on the provided FXML scene file.
     * Closes the previous window before opening the new one.
     *
     * @param sceneToOpen The FXML scene file to open.
     * @param closePreviousWindow boolean used to decide whether the previous window will be closed or not.
     * @throws IOException If an I/O error occurs during the window opening process.
     */
    @FXML
    protected void openWindow(final String sceneToOpen, final boolean closePreviousWindow) throws IOException {
        //Getting position of the stage to open the new stage in the same place
        Stage currentStage = ((Stage) feedbackLabel.getScene().getWindow());
        double x = currentStage.getX();
        double y = currentStage.getY();

        if (closePreviousWindow) {
            ((Stage) feedbackLabel.getScene().getWindow()).close(); //Using UI element present in all three fxml files
        }

        FXMLLoader fxmlLoader = new FXMLLoader(PassFortify.class.getResource(sceneToOpen));
        Parent root1 = fxmlLoader.load();
        Stage stage = new Stage();
        stage.getIcons().add(new Image("https://i.ibb.co/KhSS1G9/icon.jpg"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("PassFortify v1.0");
        stage.setScene(new Scene(root1));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.show();
        stage.setY(y);
        stage.setX(x);
    }

    /**
     * Generates a secure master password using the PasswordTools class,
     * sets it in the password field, and copies it to the system clipboard.
     * Provides feedback indicating that the master password has been generated and copied.
     */
    public void onGeneratePasswordLoginWindow() {
        String newMasterKey = PasswordTools.passwordGenerator();
        passField.setText(newMasterKey); //Depositing generated password in the passField
        PasswordTools.toClipboard(newMasterKey);
        feedbackLabel.setText("Generated secure master password and copied it to clipboard");
    }

    /**
     * Generates a passphrase using the PassphraseGenerator class, sets it in the password field,
     * and copies it to the system clipboard.
     * Provides feedback indicating that the passphrase has been generated and copied.
     *
     * @throws IOException If an I/O error occurs during the generation or clipboard copying process.
     */
    public void onGeneratePassphraseLoginWindow() throws IOException {
        String passphrase = PassphraseGenerator.generatePassphrase(); //Using PassphraseGenerator class to generate Passphrase
        passField.setText(passphrase); //Depositing passphrase in the pass field
        PasswordTools.toClipboard(passphrase);
        feedbackLabel.setText("Generated passphrase and copied it to clipboard");
        feedbackLabel.setAlignment(Pos.CENTER); //Centering text
    }

    /**
     * Generates a passphrase using the PassphraseGenerator class, sets it in the input field.
     * Provides feedback indicating that the passphrase has been generated.
     *
     * @throws IOException If an I/O error occurs during the generation or clipboard copying process.
     */
    public void onGeneratePassphraseClick() throws IOException {
        String passphrase = PassphraseGenerator.generatePassphrase(); //Using PassphraseGenerator class to generate Passphrase
        if (inputCheckBox.isSelected()) {
            inputPasswordField.setText(passphrase);
        } else {
            inputField.setText(passphrase); //Depositing passphrase in the input field
        }
        PasswordTools.toClipboard(passphrase);
        feedbackLabel.setText("Generated passphrase");
        feedbackLabel.setStyle("-fx-text-fill: #03c203");
    }

    /**
     * Handles the change event of the input box.
     * This method is invoked when the state of the input box (checkbox) changes.
     * If the input box is checked, it hides the input field and displays the hidden input field,
     * copying the content of the input field to the hidden input field.
     * If the input box is unchecked, it hides the hidden input field and displays the input field,
     * copying the content of the hidden input field to the input field.
     */
    public void onInputBoxChange() {
        if (inputCheckBox.isSelected()) {
            inputField.setVisible(false);
            inputPasswordField.setVisible(true);
            inputPasswordField.setText(inputField.getText());
        } else {
            inputPasswordField.setVisible(false);
            inputField.setVisible(true);
            inputField.setText(inputPasswordField.getText());
        }
    }

    /**
     * Handles the action triggered when the "Set Master Password" button is clicked.
     * Retrieves the entered master password from the password field, writes it to the master password file,
     * encrypts the master password file, and opens the login window.
     *
     * @throws Exception If any unexpected error occurs during the process, such as file writing, encryption, or window opening issues.
     */
    @FXML
    protected void onSetMasterPassClick() throws Exception {
        String passwordFound = "passwordFound.fxml"; //fxml file of the login window
        mPassword = passField.getText();
        if (passField.getText().isEmpty()) {
            feedbackLabel.setText("Please enter password");
            feedbackLabel.setStyle("-fx-text-fill: red");
        } else {
            PasswordTools.addDataWithoutAppend(mPassLocation, mPassword, mPassword);
            openWindow(passwordFound, true); //opens login window
        }
    }

    /**
     * Logs out the user and opens the passwordFound.fxml window.
     * This method initiates the logout process by opening the passwordFound.fxml window.
     *
     * @throws IOException If an error occurs during the window opening process.
     */
    public void onLogoutClick() throws IOException {
        String passwordFound = "passwordFound.fxml";
        openWindow(passwordFound, true); //opens login window
    }

    /**
     * Handles the event when the Enter key is pressed.
     * It invokes the onPasswordButtonClick method, thus acting like a button press.
     *
     * @see #onLoginClick()
     * @param event The KeyEvent representing the key press event.
     * @throws Exception If any error occurs during the execution of the associated action,
     *                   an exception is thrown to handle the issue.
     */
    @FXML
    void enterKeyPressed(KeyEvent event) throws Exception {
        if (event.getCode() == KeyCode.ENTER) {
            onLoginClick();
        }
    }

    /**
     * Handles the action triggered when the "Login" button is clicked.
     * Retrieves the entered master password from the password field,
     * decrypts the stored master password, and opens the internal window if the passwords match.
     * If the password is incorrect, decreases the remaining attempts and provides feedback.
     * Exits the application after too many incorrect attempts.
     *
     * @throws Exception If any unexpected error occurs during the process, such as decryption or window opening issues.
     */
    public void onLoginClick() throws Exception {
        mPassword = passField.getText(); //Retrieves entered masterpassword from passField
        if (PasswordTools.checkMasterpassword(mPassword)) {
            openWindow("internal.fxml", true);
        } else {
            //if the password was incorrect 3 times, the program closes
            triesLeft--;
            passField.setText("");
            if (triesLeft > 1) {
                tryLabel.setText("Wrong password, " + triesLeft + " attempts remaining");
            } else if (triesLeft > 0) {
                tryLabel.setText("Wrong password, " + triesLeft + " attempt remaining");
            } else {
                System.exit(0);
            }
        }
    }

    /**
     * Checks and sets the application settings based on the content of the settings file.
     * If the file exists, reads the settings and updates the corresponding toggles.
     * If the file does not exist, creates a new settings file with default values.
     *
     * @throws IOException If an I/O error occurs while reading or creating the settings file.
     */
    public void applySettings() throws IOException {
        BufferedReader reader;
        String settingLocation = "settings.txt";
        Path settingPath = Path.of(settingLocation);

        //Checking whether the setting file exits
        if (Files.exists(settingPath)) {
            final int positionOfCheckLeaks = 3; //Position of the check for leaks setting
            final int amountOfSettings = 4; //4, because there is four settings.
            String[] settingContentLines = new String[amountOfSettings]; //Array containing the settings
            reader = new BufferedReader(new FileReader(settingLocation));
            String line = reader.readLine();
            int i = 0;
            while (line != null) {
                settingContentLines[i] = line; //Setting line is assigned to a position in the array
                line = reader.readLine();
                i++;
            }
            reader.close();

            //Looking which settings are selected
            hidePassword.setSelected(settingContentLines[0].equals("HidePassword1"));
            passwordMatch.setSelected(settingContentLines[1].equals("PasswordWarning1"));
            if (settingContentLines[2].equals("ClearPassword1")) {
                clearPassword.setSelected(true);
                clearMasterPassSettingActivated();
            } else {
                clearPassword.setSelected(false);
            }
            checkLeak.setSelected(settingContentLines[positionOfCheckLeaks].equals("CheckLeak1"));
        } else { //Creating settings file, if it doesn't exist
            try {
                FileWriter settingWriter = new FileWriter(settingLocation);
                settingWriter.write("HidePassword1\nPasswordWarning0\nClearPassword0\nCheckLeak0");
                settingWriter.close();

                //Manually selecting and unselecting the checkMenuItems, as otherwise it would bug out.
                checkLeak.setSelected(false);
                hidePassword.setSelected(true);
                passwordMatch.setSelected(false);
                clearPassword.setSelected(false);
            } catch (IOException e) {
                feedbackLabel.setText("Error creating settings file.");
                feedbackLabel.setStyle("-fx-text-fill: red");
            }
        }
    }

    public void updateSettingsFile() {
        String settingLocation = "settings.txt";
        try (FileWriter settingWriter = new FileWriter(settingLocation)) {
            StringBuilder sb = new StringBuilder();

            sb.append(hidePassword.isSelected() ? "HidePassword1\n" : "HidePassword0\n");
            sb.append(passwordMatch.isSelected() ? "PasswordWarning1\n" : "PasswordWarning0\n");
            sb.append(clearPassword.isSelected() ? "ClearPassword1\n" : "ClearPassword0\n");
            sb.append(checkLeak.isSelected() ? "CheckLeak1\n" : "CheckLeak0\n");

            settingWriter.write(sb.toString());
        } catch (IOException e) {
            feedbackLabel.setText("Error updating settings file.");
            feedbackLabel.setStyle("-fx-text-fill: red");
        }
    }

    /**
     * Handles the action triggered when the "Clear Master Password" setting is toggled.
     * If the setting is selected, clears the content of the master password field.
     * Regardless of the setting state, updates other relevant settings.
     */
    public void clearMasterPassSettingActivated() {
        if (clearPassword.isSelected()) {
            mPasswordField2.setText("");
        }
        updateSettingsFile();
    }

    /**
     * Handles the action triggered when the "Minimize" button is clicked.
     * Minimizes the current window using the JavaFX Stage object associated with a common label in the FXML.
     */
    public void onMinimizeClick() {
        Stage obj = (Stage) feedbackLabel.getScene().getWindow(); //Using a label existing in all relevant FXMLs to locate the Stage
        obj.setIconified(true);
    }

    /**
     * Handles the action triggered when the "Change Master Password" button is clicked.
     * Retrieves the new master password and the current master password from input fields,
     * checks the strength of the new master password, and prompts the user to confirm the change.
     * If the input field is empty or if the current master password is not entered,
     * appropriate feedback messages are displayed, and the process is cancelled.
     * Otherwise, the user will be prompted to confirm the master password change with "yes" and "no" buttons.
     */
    public void onChangeMasterPassClick() {
        String newMasterPass = getInputFromTextField();
        String currentMasterPass = mPasswordField2.getText();

        //Checks whether the input field is empty
        if (newMasterPass.isEmpty()) {
            feedbackLabel.setText("Input field is empty, please enter a new master password");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } else if (currentMasterPass.isEmpty()) {
            feedbackLabel.setText("You have to enter your current master password to proceed");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } else {
            //If both a new and current master password is entered, the user is asked to confirm
            savedPasswordStrength = PasswordTools.passwordStrengthOutput(newMasterPass);
            accountLabel.setText("");
            feedbackLabel.setText("Are you sure you want to change the master password to '" + newMasterPass + "' ("
                    + savedPasswordStrength + ")?"); //Prompt asking the user if he wants to change it to the new password
            feedbackLabel.setStyle("-fx-text-fill: #03c203;");
            yesButton.setVisible(true);
            noButton.setVisible(true);
        }
    }

    /**
     * Handles the action triggered when the "yes" button is clicked.
     * Sets the "yes" and "no" buttons to invisible and calls the changeMasterPass method.
     */
    public void yesClick() {
        yesButton.setVisible(false);
        noButton.setVisible(false);
        try {
            changeMasterPassword();
        } catch (Exception e) {
            feedbackLabel.setText("Master password could not be changed");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Handles the action triggered when the user decides not to proceed with the master password change.
     * Hides the confirmation buttons, refreshes the table view data, and provides user feedback.
     *
     * @throws Exception If any unexpected error occurs during the cancellation process,
     *                   such as issues with updating the UI or file operations.
     */
    public void noClick() throws Exception {
        yesButton.setVisible(false);
        noButton.setVisible(false);
        populateTableData();
        feedbackLabel.setText("Master password change cancelled");
        feedbackLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Handles the pressing of a pane to initiate dragging.
     * This method is typically associated with an event listener for mouse pressing on a pane. It captures
     * the initial coordinates of the mouse press relative to the application window, allowing for proper
     * tracking of the mouse movement during dragging.
     *
     * @param me The MouseEvent representing the mouse press action.
     */
    public void panePressed(final MouseEvent me) {
        Stage stage = (Stage) feedbackLabel.getScene().getWindow(); //Getting stage object from the label
        Delta.x = stage.getX() - me.getScreenX();
        Delta.y = stage.getY() - me.getScreenY();
    }

    /**
     * Handles the dragging of the application window when a pane is dragged.
     * This method is typically associated with an event listener for mouse dragging on a pane. It adjusts
     * the position of the application window based on the mouse movement, allowing for window dragging.
     *
     * @param me The MouseEvent representing the dragging action.
     */
    public void paneDragged(final MouseEvent me) {
        Stage stage = (Stage) feedbackLabel.getScene().getWindow(); //Getting stage object from the label
        stage.setX(Delta.x + me.getScreenX());
        stage.setY(Delta.y + me.getScreenY());
    }

    /**
     * Handles the action triggered when changing the master password.
     * Decrypts existing password, service, and username files using the old master password,
     * then encrypts them with the new master password, replaces the master password file,
     * and updates the UI accordingly.
     *
     * @throws Exception If any unexpected error occurs during the master password change process,
     *                   such as issues with decryption, encryption, file operations, or password validation.
     */
    @FXML
    public void changeMasterPassword() throws Exception {
        String newpass = getInputFromTextField();
        mPassword = mPasswordField2.getText();

        if (PasswordTools.checkMasterpassword(mPassword) && newpass != null) {
            PasswordTools.changeMPass(mPassword, newpass);
            mPassword = newpass;
            feedbackLabel.setText("Master password changed");
            feedbackLabel.setStyle("-fx-text-fill: #03c203;");
        } else {
            feedbackLabel.setText("Master password incorrect");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Method called by clicking the "Add Account" button. Opens a second window using the internal.fxml file.
     * Allows user to enter full account details in a form.
     *
     * @throws IOException if anything unexpected happens during the loading of the fxml.
     */
    public void onAddAccountButton() throws IOException {
        openWindow("addAccount.fxml", false);
    }

    /**
     * Handles the action triggered when the "Reveal Accounts" button is clicked.
     * Retrieves the master password from the password field and populates the table with decrypted data.
     *
     * @throws Exception If an unexpected error occurs during the process, such as incorrect master password or table population issues.
     */
    @FXML
    void onRevealAccountsClick() throws Exception {
        mPassword = mPasswordField2.getText();
        if (!PasswordTools.checkMasterpassword(mPassword)) {
            feedbackLabel.setText("Master password incorrect");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        if (!Files.exists(Path.of(serviceLocation)) && Files.exists(Path.of(usernameLocation))
                && Files.exists(Path.of(passwordLocation))) {
            feedbackLabel.setText("Missing account files.");
            feedbackLabel.setStyle("-fx-text-fill: red");
            return;
        }
        populateTableData();
        feedbackLabel.setText("");
    }

    /**
     * Populates the table with data retrieved from password, username, and service files.
     * Applies settings, checks for duplicate passwords, and updates UI elements accordingly.
     *
     * @throws Exception If an unexpected error occurs during the process, such as file reading or setting updates.
     */
    private void populateTableData() throws Exception {
        mPassword = mPasswordField2.getText();
        applySettings(); //Updates and applies settings
        applySettings(); //Settings need to be checked twice. if settings file was just created all settings would be turned on by default

        //Using PasswordTools class to get arrays filled with account information.
        String[] serviceContentLines = PasswordTools.getContentLines(serviceLocation, mPassword);
        String[] usernameContentLines = PasswordTools.getContentLines(usernameLocation, mPassword);
        String[] passwordContentLines = PasswordTools.getContentLines(passwordLocation, mPassword);

        //Checks whether the setting for warning about duplicate passwords is selected
        if (passwordMatch.isSelected() && !hidePassword.isSelected()) {
            samePasswordCheck(passwordContentLines); //enters the samePasswordCheck to check if passwords are reused
        } else {
            warningLabel.setText("");
        }

        //ObservableList is created for the data
        ObservableList<DataEntry> data = FXCollections.observableArrayList();

        //calculates the maximum number of lines
        int maxLines = Math.max(serviceContentLines.length,
                Math.max(usernameContentLines.length, passwordContentLines.length));

        //Checking whether to fill tableView with passwords shown or passwords hidden
        for (int i = 0; i < maxLines; i++) {
            String services = (i < serviceContentLines.length) ? serviceContentLines[i] : "";
            String usernames = (i < usernameContentLines.length) ? usernameContentLines[i] : "";
            String passwords;
            if (hidePassword.isSelected()) {
                passwords = (i < passwordContentLines.length) ? "******" : "";
            } else {
                passwords = (i < passwordContentLines.length) ? passwordContentLines[i] : "";
            }
            data.add(new DataEntry(services, usernames, passwords)); // Adds the information as a DataEntry object
        }

        //The data is added to the CellValueFactories
        services.setCellValueFactory(new PropertyValueFactory<>("service"));
        usernames.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwords.setCellValueFactory(new PropertyValueFactory<>("password"));

        filteredData = new FilteredList<>(data, p -> true);
        accountTable.setItems(filteredData); //Displaying the filtered list through the tableview
        accountLabel.setText("Accounts saved: " + maxLines); //Using maxLines to determine the amount of accounts saved

        accountTable.getSelectionModel().setCellSelectionEnabled(true);
    }

    /**
     * Handles the action triggered when the search button is clicked.
     * Filters the table data based on the search term entered.
     *
     * @throws Exception If an unexpected error occurs during the process, such as file reading or setting updates.
     */
    @FXML
    private void handleSearchButtonClick() throws Exception {
        if (PasswordTools.checkMasterpassword(mPasswordField2.getText())) {
            populateTableData();
            String filterThis = getInputFromTextField();
            filteredData.setPredicate(entry -> { //setting the predicate of the filtered list
                if (filterThis == null || filterThis.isEmpty()) {
                    return true; // Show all entries when the search field is empty
                }

                String lowerCaseFilter = filterThis.toLowerCase();

                // Check if any of the columns contain the search term
                return entry.getService().toLowerCase().contains(lowerCaseFilter)
                        || entry.getUsername().toLowerCase().contains(lowerCaseFilter);
            });
        } else {
            feedbackLabel.setText("Wrong master password");
            feedbackLabel.setStyle("-fx-text-fill: red");
        }
    }

    /**
     * Handles the action when the "Delete Account" button is clicked.
     * Deletes the selected account from the data files if the master password is correct.
     *
     * @throws Exception If an unexpected error occurs, such as an incorrect master password or issues with account deletion.
     */
    public void onDeleteAccountButtonClicked() throws Exception {
        //Assigns selected item to selectedItem
        DataEntry selectedItem = accountTable.getSelectionModel().getSelectedItem();
        String mPass = mPasswordField2.getText(); //Takes master password from password field

        //If item is selected, password gets taken from password field and deletion is executed
        if (selectedItem != null && PasswordTools.checkMasterpassword(mPass)) {
            String service = selectedItem.getService(); //Service from selected column
            String mPassword = mPasswordField2.getText();

            try {
                //table position used to delete account
                int pos = accountTable.getSelectionModel().getSelectedIndex();

                PasswordTools.deleteLineInAccountFiles(pos, serviceLocation, mPassword);
                PasswordTools.deleteLineInAccountFiles(pos, usernameLocation, mPassword);
                PasswordTools.deleteLineInAccountFiles(pos, passwordLocation, mPassword);

                feedbackLabel.setText("Account '" + service + "' deleted");
                feedbackLabel.setStyle("-fx-text-fill: #03c203;");
            } catch (IOException e) {
                feedbackLabel.setText("Account could not be deleted");
                feedbackLabel.setStyle("-fx-text-fill: red;");
                throw new RuntimeException(e);
            }
        } else {
            feedbackLabel.setText("Please select an account to delete");
            feedbackLabel.setStyle("-fx-text-fill: red");
        }
        if (!PasswordTools.checkMasterpassword(mPass)) {
            feedbackLabel.setText("Master password incorrect");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } else {
            populateTableData();
        }
    }

    /**
     * Copies the selected account information (service, username, and password) to the system clipboard.
     * Shows a success message if the passwords are visible; otherwise, displays an error message.
     *
     *  @throws Exception If an error occurs during the decryption process in getPasswordFromFile()
     */
    @FXML
    public void copyPasswordFromTableview() throws Exception {
        mPassword = mPasswordField2.getText();
        if (accountTable.getSelectionModel().getSelectedItem() != null) {
            int pos = accountTable.getSelectionModel().getSelectedIndex();
            String password = PasswordTools.getPasswordFromFiles(mPassword, pos);

            if (password != null) {
                PasswordTools.toClipboard(password);
                feedbackLabel.setText("Password copied");
                feedbackLabel.setStyle("-fx-text-fill: #03c203;");
            } else {
                feedbackLabel.setText("Error! Either master password is incorrect, or no password in selection");
                feedbackLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            feedbackLabel.setText("Please initialise the table and select a password");
            feedbackLabel.setStyle("-fx-text-fill: red");
        }
    }

    /**
     * Replaces the password for a selected service and username.
     * This method retrieves the master password, new password, service, and username from UI components.
     * It then loads the existing content lines for the service, username, and password files.
     * If a match is found for the selected service and username, the corresponding password is replaced
     * with the new password. The modified password content is then written back to the password file,
     * encrypted with the master password, and the table data is updated.
     *
     * @throws Exception If an error occurs during the password replacement, file writing, or encryption process.
     *
     * @see #populateTableData()
     */
    public void onChangeEntryClick() throws Exception {
        if (accountTable.getSelectionModel().getSelectedItem() != null) {
            mPassword = mPasswordField2.getText();
            String newEntry = getInputFromTextField();

            TableColumn selectedColumn = accountTable.getSelectionModel().getSelectedCells().get(0).getTableColumn();

            String service = accountTable.getSelectionModel().getSelectedItem().getService();
            String username = accountTable.getSelectionModel().getSelectedItem().getUsername();
            String password = accountTable.getSelectionModel().getSelectedItem().getPassword();

            if (selectedColumn.getText().equals("Service")) {
                PasswordTools.changeEntry(mPassword,
                        passwordLocation,
                        usernameLocation,
                        serviceLocation,
                        password,
                        username,
                        newEntry);
            } else if (selectedColumn.getText().equals("Username")) {
                PasswordTools.changeEntry(mPassword,
                        serviceLocation,
                        passwordLocation,
                        usernameLocation,
                        service,
                        password,
                        newEntry);
            } else if (selectedColumn.getText().equals("Password")) {
                PasswordTools.changeEntry(mPassword,
                        serviceLocation,
                        usernameLocation,
                        passwordLocation,
                        service,
                        username,
                        newEntry);
            }

            feedbackLabel.setText(selectedColumn.getText() + " changed successfully");
            feedbackLabel.setStyle("-fx-text-fill: #03c203");

            populateTableData();
        } else {
            feedbackLabel.setText("Please select an entry to change");
            feedbackLabel.setStyle("-fx-text-fill: red");
        }
    }

    /**
     * Checks for potential data breaches or leaks using the "Have I Been Pwned" service.
     * If the "Check for leaks" setting is enabled and the selected item in the account table is an email address,
     * opens a web browser to the corresponding "Have I Been Pwned" page for the email address.
     *
     * @throws IOException If an error occurs while attempting to access the "Have I Been Pwned" service.
     */
    @FXML
    public void checkForLeaks() throws IOException {
        if (checkLeak.isSelected()) { //Checking if setting to check for leaks is enabled
            String elementTableSelected = accountTable.getSelectionModel().getSelectedItem().getUsername();
            if (elementTableSelected.contains(".") && elementTableSelected.contains("@")) { //Checking for e-mail
                String url = "https://haveibeenpwned.com/account/" + elementTableSelected; //Link used to check for leaks
                //Accessing "haveIBeenPWNED" for the selected e-mail
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            }
        }
    }

    /**
     * This method is invoked when the user clicks the "Generate" button.
     * It opens the password generation form, allowing the user to select character sets
     * and specify the length of the generated password.
     *
     * @throws IOException If an error occurs while attempting to open the password generation form.
     */
    public void onGenerateClick() throws IOException {
        openWindow("passwordGenerationForm.fxml", false);
    }

    /**
     * Method that gets called when the "Create Backup" button is clicked.
     * the Path destinationDirectory gets assigned the output of the getFilePath method.
     * If the destinationDirectory is not null, the destinationDirectory gets created if it does not exist.
     * The password, username, service and mpass files get copied to the destinationDirectory.
     * The user gets informed whether the backup has been created.
     */
    public void onCreateBackUpClick() {
        try {
            Path destinationDirectory = PasswordTools.getFilePath(); //Assigns the file path through the getFilePath method
            PasswordTools.createBackup(destinationDirectory);
            feedbackLabel.setText("Backup created.");
            feedbackLabel.setStyle("-fx-text-fill: #03c203;");
        } catch (IOException | InterruptedException | RuntimeException e) {
            feedbackLabel.setText("Backup creation was interrupted.");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public void onCloseClick() {
        Platform.exit();
    }

    /**
     * Checks whether the same password is used multiple times and displays a warning if detected.
     * This method uses the samePasswordCheck method from the PasswordTools class to check whether the same password
     * is used multiple times within the provided array of password content lines. If duplicates are found, a warning
     * message is displayed on the warningLabel with a red text color. Otherwise, the warningLabel is set to empty.
     *
     * @param passwordContentLines An array of strings containing the saved passwords in different entries
     *
     * @see PasswordTools#duplicatePasswordCheck(String[])
     */
    public void samePasswordCheck(final String[] passwordContentLines) {
        //Checks whether same password is used multiple times using the samePasswordCheck method from the PasswordTools class
        boolean hasSamePassword = PasswordTools.duplicatePasswordCheck(passwordContentLines);
        if (hasSamePassword) { //Checks whether same password is used multiple times
            warningLabel.setText("WARNING: Same password used for different accounts");
            warningLabel.setStyle("-fx-text-fill: red;");
        } else {
            warningLabel.setText("");
        }
    }

    /**
     * Method that gets called whenever the menu option to display the warning gets updated
     * Depending on whether the CheckMenuItem is selected or not, the warning will get removed, or the table refreshed to see if passwords matched
     *
     * @throws Exception unused exception
     */
    public void dupeUpdater() throws Exception {
        String masterPassword = mPasswordField2.getText(); //Master password taken from password field
        updateSettingsFile();
        if (PasswordTools.checkMasterpassword(masterPassword)) {
            if (passwordMatch.isSelected()) { //Checking whether the setting to alert for duplicate passwords is enabled
                populateTableData(); //If enabled, tableview will be repopulated and warning appears on bottom
            } else {
                warningLabel.setText("");
            }
        } else {
            feedbackLabel.setText("Master password incorrect");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Handles the action when checking the strength of a password is clicked.
     * This method retrieves the password to check from the input field. If the input field is empty, a
     * warning message is displayed on the feedbackLabel. Otherwise, the password strength is evaluated using
     * the PasswordTools.passwordStrengthOutput method, and the result is displayed on the feedbackLabel.
     */
    public void onCheckPasswordStrengthClick() {
        String passwordToCheck = getInputFromTextField();
        //Checks whether the input field is empty
        if (Objects.equals(passwordToCheck, "")) {
            feedbackLabel.setText("Please enter a password to check");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } else {
            savedPasswordStrength = PasswordTools.passwordStrengthOutput(passwordToCheck); //Evaluating the strength of the password
            feedbackLabel.setText("Password is " + savedPasswordStrength);
            feedbackLabel.setStyle("-fx-text-fill: yellow");
        }
    }

    /**
     * Retrieves the input from either the visible input field or the hidden input field, based on the state of the input box.
     * If the input box is selected, it retrieves the text from the hidden input field.
     * If the input box is not selected, it retrieves the text from the visible input field.
     *
     * @return The input text retrieved from either the visible or hidden input field.
     */
    public String getInputFromTextField() {
        String input;
        if (inputCheckBox.isSelected()) {
            input = inputPasswordField.getText();
        } else {
            input = inputField.getText();
        }
        return input;
    }

    /**
     * Method that gets called when the "About" button is clicked
     * Opens the GitHub repository of the password manager in the default browser
     * @throws IOException not used
     */
    public void onAboutButtonClick() throws IOException {
        java.awt.Desktop.getDesktop().browse(URI.create("https://github.com/jscheel76/Passwordmanager-Java-Test"));
    }

    /**
     * Method that gets called when the "Contact" button is clicked
     * Opens the default mail client with the email address of the developer
     * @throws IOException not used
     */
    public void onContactButtonClick() throws IOException {
        java.awt.Desktop.getDesktop().browse(URI.create("mailto:passfortify@proton.me"));
    }

    /**
     * Method that gets called when the "Hide Passwords" checkMenuItem is selected.
     * onRevealAccountsClick gets called to refresh the table
     * @throws Exception unused exception
     */
    @FXML
    public void hidePasswordsUpdater() throws Exception {
        updateSettingsFile();
        onRevealAccountsClick();
    }
}
