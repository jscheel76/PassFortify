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
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Controller class for the password manager.
 * This class contains all the methods used by the password manager.
 * The main methods are direct interaction with the GUI.
 * Other methods are used as helper methods.
 *
 * @author Jannik Scheel
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
     * CheckMenuItem used to switch between experimental and legacy UI.
     */
    @FXML
    private CheckMenuItem experimentalMode;

    /**
     * Boolean to check if the table is initialised and ready to be copied from.
     */
    private boolean arePasswordsShown = false;

    /**
     * Integer determining how many tries a user has left.
     * Using 2+1 as to not use magic numbers.
     */
    private int triesLeft = 2 + 1;

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
     * Filtered list used to filter the tableview entries.
     */
    private FilteredList<DataEntry> filteredData;

    /**
     * Global String used to save the password strength evaluation.
     */
    private String savedPasswordStrength = "";

    /**
     * Opens a new window based on the provided FXML scene file.
     * Closes the previous window before opening the new one.
     *
     * @param sceneToOpen The FXML scene file to open.
     * @param close boolean used to decide whether the previous window will be closed or not.
     * @throws IOException If an I/O error occurs during the window opening process.
     */
    @FXML
    protected void openWindow(final String sceneToOpen, final boolean close) throws IOException {
       //Getting position of the stage to open the new stage in the same place
        Stage currentStage = ((Stage) feedbackLabel.getScene().getWindow());
        double x = currentStage.getX();
        double y = currentStage.getY();

        //Closes the previous window
        if (close) {
            ((Stage) feedbackLabel.getScene().getWindow()).close(); //Using UI element present in all three fxml files
        }

        //tries to open internals of the password manager, if the password was correct
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
        final Clipboard clipboard = Clipboard.getSystemClipboard(); //Initialize clipboard
        final ClipboardContent content = new ClipboardContent(); //String to store copied password
        content.putString(newMasterKey); //Assign the generated password to the content string
        clipboard.setContent(content); //Place password in users clipboard
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
        final Clipboard clipboard = Clipboard.getSystemClipboard(); //Initialize clipboard
        final ClipboardContent content = new ClipboardContent(); //String to store copied password
        content.putString(passphrase); //Assign the generated password to the content string
        clipboard.setContent(content); //Place password in users clipboard
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
        inputField.setText(passphrase); //Depositing passphrase in the input field
        feedbackLabel.setText("Generated passphrase");
        feedbackLabel.setStyle("-fx-text-fill: #03c203");
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
        mPassword = passField.getText(); //Entered password retrieved from passField
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
    public void logout() throws IOException {
        String passwordFound = "passwordFound.fxml";
        openWindow(passwordFound, true); //opens login window
    }

    /**
     * Handles the action triggered when the "Password" button is clicked.
     * Retrieves the entered master password from the password field,
     * decrypts the stored master password, and opens the internal window if the passwords match.
     * If the password is incorrect, decreases the remaining attempts and provides feedback.
     * Exits the application after too many incorrect attempts.
     *
     * @throws Exception If any unexpected error occurs during the process, such as decryption or window opening issues.
     */
    public void onPasswordButtonClick() throws Exception {
        String internal = "internal.fxml"; //fxml file for the internal window
        mPassword = passField.getText(); //Retrieves entered masterpassword from passField
        byte[] decryptedText = Cryptography.decryptFile(mPassLocation, mPassword);
        String decryptedMPass = new String(decryptedText, UTF_8);

        //Compares entered password with decrypted master password
        if ((decryptedMPass).equals(mPassword)) {
            if (experimentalMode.isSelected()) {
                openWindow("internal2.fxml", true);
            } else {
                openWindow(internal, true); //Opens internal.fxml window
            }
        } else {
            //if the password was incorrect 3 times, the program closes
            triesLeft--;
            passField.setText("");
            if (triesLeft > 1) {
                tryLabel.setText("Wrong password, " + triesLeft + " attempts remaining");
            } else if (triesLeft > 0) {
                tryLabel.setText("Wrong password, " + triesLeft + " attempt remaining");
            } else {
                System.exit(0); //Exits the application after too many attempts
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
    public void settingCheck() throws IOException {
        BufferedReader reader; //BufferedReader to read lines
        String settingLocation = "settings.txt"; //Location of the settings file
        Path settingPath = Path.of(settingLocation);

        //Checking whether the setting file exits
        if (Files.exists(settingPath)) {
            final int positionOfCheckLeaks = 3; //Position of the check for leaks setting
            final int amountOfSettings = 4; //4, because there is four settings.
            String[] settingContentLines = new String[amountOfSettings]; //Array containing the settings
            reader = new BufferedReader(new FileReader(settingLocation));
            String line = reader.readLine(); //reads first line and assigns it to the string
            int i = 0; //Integer used to go through all positions in the array
            while (line != null) {
                settingContentLines[i] = line; //Setting line is assigned to a position in the array
                line = reader.readLine(); //Next line gets read
                i++; //incrementing to read the next line
            }
            reader.close();

            //Looking which settings are selected
            hidePassword.setSelected(settingContentLines[0].equals("HidePassword1"));
            passwordMatch.setSelected(settingContentLines[1].equals("PasswordWarning1"));
            if (settingContentLines[2].equals("ClearPassword1")) {
                clearPassword.setSelected(true);
                onClearMPass(); //Clearing password field if setting is selected
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
                hidePassword.setSelected(true);
                passwordMatch.setSelected(false);
                clearPassword.setSelected(false);
                checkLeak.setSelected(false);
            } catch (IOException e) {
                feedbackLabel.setText("Error creating settings file.");
                feedbackLabel.setStyle("-fx-text-fill: red");
            }
        }
    }

    /**
     * Updates the application settings based on the selected states of various settings toggles.
     * Writes the updated settings to the settings file.
     * Provides feedback in case of an error while updating the settings file.
     */
    public void settingUpdater() {
        String settingLocation = "settings.txt";
        try {
            FileWriter settingWriter = new FileWriter(settingLocation);
            if (hidePassword.isSelected()) {
                settingWriter.write("HidePassword1\n"); //1 to enable
            } else {
                settingWriter.write("HidePassword0\n"); //0 to disable
            }
            if (passwordMatch.isSelected()) {
                settingWriter.write("PasswordWarning1\n"); //1 to enable
            } else {
                settingWriter.write("PasswordWarning0\n"); //0 to disable
            }
            if (clearPassword.isSelected()) {
                settingWriter.write("ClearPassword1\n"); //1 to enable
            } else {
                settingWriter.write("ClearPassword0\n"); //0 to disable
            }
            if (checkLeak.isSelected()) {
                settingWriter.write("CheckLeak1\n"); //1 to enable
            } else {
                settingWriter.write("CheckLeak0\n"); //0 to disable
            }
            settingWriter.close();
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
    public void onClearMPass() {
        if (clearPassword.isSelected()) { //Checking whether the clear password setting is selected
            mPasswordField2.setText(""); //clearing password field
        }
        settingUpdater(); //Updating settings
    }

    /**
     * Handles the action triggered when the "Minimize" button is clicked.
     * Minimizes the current window using the JavaFX Stage object associated with a common label in the FXML.
     */
    public void onMinimizeClick() {
        Stage obj = (Stage) feedbackLabel.getScene().getWindow(); //Using a label existing in all FXMLs to locate the Stage
        obj.setIconified(true); //Minimizing window
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
        String newMasterPass = inputField.getText(); //Takes the new master password from the input field
        String currentMasterPass = mPasswordField2.getText(); //Takes the current master password from the password fieldZ
        onCheckPasswordStrengthClick(); //Checking the strength of the new master password

        //Checks whether the input field is empty
        if (newMasterPass.isEmpty()) {
            //If no new master password is entered in the input field, a notice is displayed to enter one and the process is cancelled
            feedbackLabel.setText("Input field is empty, please enter a new master password");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } else if (currentMasterPass.isEmpty()) {
            //If master password is entered in the password field, a notice is displayed to enter it and the process is cancelled
            feedbackLabel.setText("You have to enter your current master password to proceed");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } else {
            //If both a new and current master password is entered, the user is asked to confirm
            accountLabel.setText("");
            feedbackLabel.setText("Are you sure you want to change the master password to '" + newMasterPass + "' ("
                    + savedPasswordStrength + ")?"); //Prompt asking the user if he wants to change it to the new password
            feedbackLabel.setStyle("-fx-text-fill: #03c203;"); //Green
            yesButton.setVisible(true); //Button to confirm is displayed
            noButton.setVisible(true); //Button to cancel is displayed
        }
    }

    /**
     * Handles the action triggered when the "yes" button is clicked.
     * Sets the "yes" and "no" buttons to invisible and calls the changeMasterPass method.
     */
    public void yesClick() {
        yesButton.setVisible(false); //Hide yesButton
        noButton.setVisible(false); //Hide noButton
        try {
            changeMasterPass(); //Start password change process
        } catch (Exception e) {
            feedbackLabel.setText("Master password could not be changed"); //User feedback
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
        yesButton.setVisible(false); //Hide yesButton
        noButton.setVisible(false); //Hide noButton
        populateTableData(); //Reset tableview

        feedbackLabel.setText("Master password change cancelled"); //User feedback
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
    public void paneDragged(final MouseEvent me) {
        Stage stage = (Stage) feedbackLabel.getScene().getWindow(); //Getting stage object from the label
        stage.setX(Delta.x + me.getScreenX()); //Setting the x position of the stage
        stage.setY(Delta.y + me.getScreenY()); //Setting the y position of the stage
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
    public void changeMasterPass() throws Exception {
        //New master password is taken from input field, old master password from the password field
        String newpass = inputField.getText();
        mPassword = mPasswordField2.getText();

        if (PasswordTools.checkMasterpassword(mPassword) && newpass != null) {
            PasswordTools.decryptAndEncrypt(passwordLocation, mPassword, newpass); //decrypting passwords and encrypting them with new master password
            PasswordTools.decryptAndEncrypt(serviceLocation, mPassword, newpass); //decrypting services and encrypting them with new master password
            PasswordTools.decryptAndEncrypt(usernameLocation, mPassword, newpass); //decrypting usernames and encrypting them with new master password
            PasswordTools.addDataWithoutAppend(mPassLocation, newpass, newpass); //replacing master password file
            mPassword = newpass; //Reassigning mPassword to the new password

            //Shows user that the master password has been changed
            feedbackLabel.setText("Master password changed"); //User feedback
            feedbackLabel.setStyle("-fx-text-fill: #03c203;"); //Green
        } else {
            feedbackLabel.setText("Master password incorrect"); //User feedback
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Checks if there is any input in the specified input field.
     *
     * @return true if the input field is not empty, false otherwise.
     */
    public boolean isInput() {
        String userInput = inputField.getText();
        return !userInput.isEmpty(); //returns true if inputField is empty, returns false if inputField contains anything
    }

    /**
     * Method called by clicking the "Add Account" button. Opens a second window using the internal2.fxml file.
     * Allows user to enter full account details in a form.
     *
     * @throws IOException if anything unexpected happens during the loading of the fxml.
     */
    public void onAddAccountButton() throws IOException {
        openWindow("addAccount.fxml", false);
    }

    @FXML
    public void addInput(String location, String feedback) throws Exception {
        //Master password and new account are taken from the password and input field
        mPassword = mPasswordField2.getText();
        String inputToAdd = inputField.getText();
        if (isInput()) { //Checking whether input even exists
            if (PasswordTools.checkMasterpassword(mPassword)) { //Comparing master password
                if (Files.exists(Path.of(location))) {
                    PasswordTools.decryptAndSave(location, mPassword); //Decrypting file
                }
                try {
                    PasswordTools.addData(location, inputToAdd, mPassword); //Adding new input
                    feedbackLabel.setText(feedback + " added");
                    feedbackLabel.setStyle("-fx-text-fill: #03c203;"); // Green
                } catch (Exception e) {
                    //Prints error message if account could not be added
                    feedbackLabel.setText(feedback + " could not be added");
                    feedbackLabel.setStyle("-fx-text-fill: red;");
                }
                if (Files.exists(Path.of(passwordLocation)) && Files.exists(Path.of(usernameLocation))
                        && Files.exists(Path.of(serviceLocation))) {
                    populateTableData(); //Updating tableview only if all files exist
                }
            } else {
                feedbackLabel.setText("Master password incorrect");
                feedbackLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            feedbackLabel.setText("Enter the " + feedback + " you want to add in the input field");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public void onServiceButtonClick() throws Exception {
        addInput(serviceLocation, "Service");
    }

    public void onAddUsernameButton() throws Exception {
        addInput(usernameLocation, "Username");
    }

    public void onAddPasswordButtonClick() throws Exception {
        addInput(passwordLocation, "Password");
    }

    /**
     * Handles the action triggered when the "Reveal Accounts" button is clicked.
     * Retrieves the master password from the password field and populates the table with decrypted data.
     *
     * @throws Exception If an unexpected error occurs during the process, such as incorrect master password or table population issues.
     */
    @FXML
    public void onRevealAccountsClick() throws Exception {
        //Master password is taken from the password field, the table is populated with the decrypted data
        mPassword = mPasswordField2.getText();
        if (PasswordTools.checkMasterpassword(mPassword)) { //Checks whether the master password is correct
            if (Files.exists(Path.of(serviceLocation)) && Files.exists(Path.of(usernameLocation))
                    && Files.exists(Path.of(passwordLocation))) {
                populateTableData(); //Updating tableView
                feedbackLabel.setText(""); //Hiding previous feedback
            } else {
                feedbackLabel.setText("Missing account files.");
                feedbackLabel.setStyle("-fx-text-fill: red");
            }
        } else {
            feedbackLabel.setText("Master password incorrect");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Populates the table with data retrieved from password, username, and service files.
     * Applies settings, checks for duplicate passwords, and updates UI elements accordingly.
     *
     * @throws Exception If an unexpected error occurs during the process, such as file reading or setting updates.
     */
    private void populateTableData() throws Exception {
        mPassword = mPasswordField2.getText();
        settingCheck(); //Updates and applies settings
        settingCheck(); //Settings need to be checked twice, if settings were just created, otherwise all settings are turned on by default
        arePasswordsShown = true; //Set to true, unless hidePassword is selected (checked later on)

        //Using PasswordTools class to get arrays filled with account information.
        String[] serviceContentLines = PasswordTools.getContentLines(serviceLocation, mPassword);
        String[] usernameContentLines = PasswordTools.getContentLines(usernameLocation, mPassword);
        String[] passwordContentLines = PasswordTools.getContentLines(passwordLocation, mPassword);

        //Checks whether the setting for warning about duplicate passwords is selected
        if (passwordMatch.isSelected() && !hidePassword.isSelected()) {
            samePasswordCheck(passwordContentLines); //enters the samePasswordCheck to check if passwords are reused
        } else {
            warningLabel.setText(""); //Resetting the warning label
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
                passwords = "********"; //Filler, so passwords are not shown
                arePasswordsShown = false; //Setting to false as to help with deletion logic, as passwords are hidden
            } else {
                passwords = (i < passwordContentLines.length) ? passwordContentLines[i] : "";
            }
            data.add(new DataEntry(services, usernames, passwords)); //Adds the information as a DataEntry object
        }

        //The data is added to the CellValueFactories
        services.setCellValueFactory(new PropertyValueFactory<>("service"));
        usernames.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwords.setCellValueFactory(new PropertyValueFactory<>("password"));

        filteredData = new FilteredList<>(data, p -> true);
        accountTable.setItems(filteredData); //Displaying the filtered list through the tableview
        accountLabel.setText("Accounts saved: " + maxLines); //Using maxLines to determine the amount of accounts saved

        //Allows for single cell selection
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
        populateTableData();
        String newValue = inputField.getText();
        filteredData.setPredicate(entry -> { //setting the predicate of the filtered list
            if (newValue == null || newValue.isEmpty()) {
                return true; // Show all entries when the search field is empty
            }

            // Convert to lowercase for case-insensitive search
            String lowerCaseFilter = newValue.toLowerCase();

            // Check if any of the columns contain the search term
            return entry.getService().toLowerCase().contains(lowerCaseFilter)
                    || entry.getUsername().toLowerCase().contains(lowerCaseFilter)
                    || entry.getPassword().toLowerCase().contains(lowerCaseFilter);
        });
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
            String username = selectedItem.getUsername(); //Username from selected column
            String password = selectedItem.getPassword(); //Password from selected column
            String mPassword = mPasswordField2.getText(); //Master password from mPasswordField

            try {
                //Using PasswordTools class to get arrays filled with account information.
                String[] serviceContentLines = PasswordTools.getContentLines(serviceLocation, mPassword);
                String[] usernameContentLines = PasswordTools.getContentLines(usernameLocation, mPassword);
                String[] passwordContentLines = PasswordTools.getContentLines(passwordLocation, mPassword);

                //Creates new string builders
                StringBuilder newServiceContent = new StringBuilder();
                StringBuilder newUsernameContent = new StringBuilder();
                StringBuilder newPasswordContent = new StringBuilder();

                boolean deleted = false; //Flag to track whether the item has been deleted
                if (!arePasswordsShown) {
                    for (int i = 0; i < serviceContentLines.length; i++) {
                        if (!deleted && serviceContentLines[i].equals(service)
                                && usernameContentLines[i].equals(username)) {
                            deleted = true; //Flagged
                        } else {
                            newServiceContent.append(serviceContentLines[i]).append(System.lineSeparator());
                            newUsernameContent.append(usernameContentLines[i]).append(System.lineSeparator());
                            newPasswordContent.append(passwordContentLines[i]).append(System.lineSeparator());
                        }
                    }
                } else {
                    //Deletes the selected item from the string builders
                    for (int i = 0; i < serviceContentLines.length; i++) {
                        if (!deleted && serviceContentLines[i].equals(service)
                                && passwordContentLines[i].equals(password)
                                && usernameContentLines[i].equals(username)) {
                            deleted = true; //Flagged
                        } else {
                            newServiceContent.append(serviceContentLines[i]).append(System.lineSeparator());
                            newUsernameContent.append(usernameContentLines[i]).append(System.lineSeparator());
                            newPasswordContent.append(passwordContentLines[i]).append(System.lineSeparator());
                        }
                    }
                }

                //Adding the data using the PasswordTools class
                PasswordTools.addDataWithoutAppend(serviceLocation, newServiceContent.toString(), mPassword);
                PasswordTools.addDataWithoutAppend(usernameLocation, newUsernameContent.toString(), mPassword);
                PasswordTools.addDataWithoutAppend(passwordLocation, newPasswordContent.toString(), mPassword);

                feedbackLabel.setText("Account '" + service + "' deleted"); //User feedback
                feedbackLabel.setStyle("-fx-text-fill: #03c203;"); //Green color

                if (passwordMatch.isSelected()) {
                    samePasswordCheck(passwordContentLines); //Checking for duplicate passwords
                }
            } catch (IOException e) {
                feedbackLabel.setText("Account could not be deleted"); //User feedback
                feedbackLabel.setStyle("-fx-text-fill: red;");
                throw new RuntimeException(e);
            }
        } else {
            feedbackLabel.setText("Please select an account to delete");
            feedbackLabel.setStyle("-fx-text-fill: red");
        }
        if (!PasswordTools.checkMasterpassword(mPass)) {
            feedbackLabel.setText("Master password incorrect"); //User feedback
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } else {
            populateTableData(); //Refreshes the table
        }
    }

    /**
     * Copies the selected account information (service, username, and password) to the system clipboard.
     * Shows a success message if the passwords are visible; otherwise, displays an error message.
     *
     *  @throws Exception If an error occurs during the decryption process in getPasswordFromFile()
     */
    @FXML
    public void copyPassword() throws Exception {
        mPassword = mPasswordField2.getText();
        if (accountTable.getSelectionModel().getSelectedItem() != null) {
            String username = accountTable.getSelectionModel().getSelectedItem().getUsername();
            String service = accountTable.getSelectionModel().getSelectedItem().getService();

            String password = PasswordTools.getPasswordFromFiles(mPassword, username, service);

            if (password != null) {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(password);
                clipboard.setContent(content);
                feedbackLabel.setText("Password copied");
                feedbackLabel.setStyle("-fx-text-fill: #03c203;"); // Green
            } else {
                feedbackLabel.setText("Password not found. Make sure master password is correct");
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
     * @see PasswordTools#getContentLines(String, String)
     * @see Cryptography#encryptFile(String, String, String)
     * @see #populateTableData()
     */
    public void onChangePasswordClick() throws Exception {
        if (accountTable.getSelectionModel().getSelectedItem() != null) {
            mPassword = mPasswordField2.getText();
            String newPass = inputField.getText(); //Retrieving new password from input field

            //Getting service and username from user selection to be used for search
            String service = accountTable.getSelectionModel().getSelectedItem().getService();
            String username = accountTable.getSelectionModel().getSelectedItem().getUsername();

            //Using PasswordTools class to perform the change of the password
            PasswordTools.changePassword(mPassword, service, username, newPass);
            feedbackLabel.setText("Password changed"); //User feedback
            feedbackLabel.setStyle("-fx-text-fill: #03c203"); //Green color

            populateTableData(); //Updating tableview
        } else {
            feedbackLabel.setText("Please select a password to change");
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
     * Method that gets called when the "Generate Password" button is clicked
     * String generatedPassword gets assigned the output of the passwordGenerator method
     * The input field gets set to the generated password
     * The user gets informed that the password has been generated
     */
    public void onGenerateClick() {
        String generatedPassword = PasswordTools.passwordGenerator(); //Generates password
        inputField.setText(generatedPassword); //Enters the generated password into the inputField
        feedbackLabel.setText("Password generated"); //User feedback
        feedbackLabel.setStyle("-fx-text-fill: #03c203;"); //Green
    }

    /**
     * Method that gets called when the "Create Backup" button is clicked.
     * the Path destinationDirectory gets assigned the output of the getFilePath method.
     * If the destinationDirectory is not null, the destinationDirectory gets created if it does not exist.
     * The password, username, service and mpass files get copied to the destinationDirectory.
     * The user gets informed whether the backup has been created.
     */
    public void onCreateBackUpClick() {
        String settingsLocation = "settings.txt"; //Location of settings file
        try {
            Path destinationDirectory = PasswordTools.getFilePath(); //Assigns the file path through the getFilePath method
            PasswordTools.createBackup(destinationDirectory, settingsLocation); //using passwordTools class to generate backup
            feedbackLabel.setText("Backup created"); //User feedback
            feedbackLabel.setStyle("-fx-text-fill: #03c203;"); //Green
        } catch (IOException | InterruptedException | RuntimeException e) {
            feedbackLabel.setText("Backup could not be created"); //User feedback after fail
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * The mesmerizing onCloseClick method unfurls its narrative, reminiscent of a delightful odyssey that traces its roots
     * back to a quaint cafe nestled in the heart of Zanzibar. Picture this: a sun-drenched afternoon, a gentle breeze carrying
     * the intoxicating aroma of freshly brewed coffee, and the rhythmic symphony of waves caressing the shores nearby. It was
     * in this idyllic setting that inspiration struck like a bolt of lightning, giving birth to the genesis of a method that
     * would come to be known as the architect of graceful application closure.
     * <p>
     * In the midst of sipping a cup of aromatic Swahili coffee, the idea crystallized: the need for a method that not only
     * gracefully handles the closure of a graphical window but also orchestrates the cessation of the entire application,
     * ushering in a denouement akin to the final act of a captivating play. Thus, the onCloseClick method was conceptualized,
     * a digital maestro orchestrating the symphony of application termination.
     * <p>
     * As the sun dipped below the horizon, casting a warm glow over the azure sea, the choice of nomenclature became
     * evident. 'onCloseClick' encapsulated the essence of the method with a poetic finesse, signaling its purpose – to
     * respond to the user's decisive click on the close button, much like turning the page of an enchanting novel towards
     * its conclusion.
     * <p>
     * It's noteworthy that the execution of this method involves the invocation of the ethereal Platform.exit() function,
     * a metaphysical portal leading to the realm of application termination. This code, akin to the closing of a well-woven
     * narrative, seamlessly brings about the cessation of our digital creation, leaving the user with a sense of completion.
     * <p>
     * However, as any seasoned storyteller would attest, the tale doesn't end with the first draft. The onCloseClick method,
     * in its resolute functionality, beckons developers to embark on a quest for improvement. Akin to a traveler refining
     * the route for future journeys, consider fortifying this method with error-handling mechanisms, enhancing its
     * resilience against unforeseen twists in the narrative of application termination.
     * <p>
     * So, as the curtain falls on this narrative, envision the onCloseClick method as a cherished companion in the
     * developer's toolkit, guiding them through the intricate landscapes of application closure. In the spirit of this
     * Zanzibarian revelation, may the method stand not just as a piece of code but as a testament to the artistry of
     * crafting digital experiences.
     *
     * @implNote It is advised to test this method thoroughly within the specific context of your application,
     *           ensuring that it aligns harmoniously with the overall design and functionality requirements.
     * @author Jannik Scheel
     * @since 2024-01-11
     */
    public void onCloseClick() {
        Platform.exit(); //Closes the window and stops the application
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
            warningLabel.setText("WARNING: Same password used for different accounts"); //Warning message displayed if that's the case
            warningLabel.setStyle("-fx-text-fill: red;");
        } else {
            warningLabel.setText(""); //Warning label set to empty, if no passwords are duplicates
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
        settingUpdater();
        if (PasswordTools.checkMasterpassword(masterPassword)) {
            if (passwordMatch.isSelected()) { //Checking whether the setting to alert for duplicate passwords is enabled
                populateTableData(); //If enabled, tableview will be repopulated and warning appears on bottom
            } else {
                warningLabel.setText(""); //If not enabled, the warning text will be set to empty, to make it disappear
            }
        } else {
            feedbackLabel.setText("Master password incorrect"); //User feedback
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
        String passwordToCheck = inputField.getText();
        //Checks whether the input field is empty
        if (Objects.equals(passwordToCheck, "")) {
            feedbackLabel.setText("Please enter a password to check"); //User feedback
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } else {
            savedPasswordStrength = PasswordTools.passwordStrengthOutput(passwordToCheck); //Evaluating the strength of the password
            feedbackLabel.setText("Password is " + savedPasswordStrength); //User feedback
            feedbackLabel.setStyle("-fx-text-fill: yellow");
        }
    }

    /**
     * Method that gets called when the "About" button is clicked
     * Opens the GitHub repository of the password manager in the default browser
     * @throws IOException not used
     */
    public void onAboutButtonClick() throws IOException {
        String url = "https://github.com/jscheel76/Passwordmanager-Java-Test"; //Link to projects GitHub
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url)); //Open browser with specified url
    }

    /**
     * Method that gets called when the "Contact" button is clicked
     * Opens the default mail client with the email address of the developer
     * @throws IOException not used
     */
    public void onContactButtonClick() throws IOException {
        String url = "mailto:passfortify@proton.me"; //Using a mailto: address, to open users e-mail client
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url)); //Open browser with specified url
    }

    /**
     * Method that gets called when the "Hide Passwords" checkMenuItem is selected.
     * onRevealAccountsClick gets called to refresh the table
     * @throws Exception unused exception
     */
    @FXML
    public void hidePasswordsUpdater() throws Exception {
        settingUpdater(); //Updating settings
        onRevealAccountsClick(); //Repopulating tableview
    }
}
