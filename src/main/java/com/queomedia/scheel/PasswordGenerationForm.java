package com.queomedia.scheel;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordGenerationForm {

    /**
     * CheckBox used to allow user to select lowercase characters in password generation.
     */
    @FXML
    private CheckBox lowercaseBox;

    /**
     * CheckBox used to allow user to select uppercase characters in password generation.
     */
    @FXML
    private CheckBox uppercaseBox;

    /**
     * CheckBox used to allow user to select digits in password password generation.
     */
    @FXML
    private CheckBox digitBox;

    /**
     * CheckBox to allow user to select special characters in password generation.
     */
    @FXML
    private CheckBox specialBox;

    /**
     * TextField to allow user to input custom password length.
     */
    @FXML
    private TextField lengthField;

    /**
     * Label used for user feedback.
     */
    @FXML
    private Label feedbackLabel;

    @FXML
    private Slider lengthSlider;

    /**
     * Set of all possible Special characters. Used in password generation.
     */
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[{]};:'\"|,./?";

    /**
     * Set of all lowercase characters. Used in password generation.
     */
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Set of all uppercase characters. Used in password generation.
     */
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Set of all digits 0-9.
     */
    private static final String DIGITS = "0123456789";

    /**
     * Generates a random password based on the selected character sets and length.
     * This method is invoked when the user clicks the password generation button.
     * It constructs a random password by selecting characters from the chosen character sets
     * (lowercase letters, uppercase letters, digits, and special characters) and of the specified length.
     * The generated password is copied to the system clipboard and displayed in the lengthField.
     * Additionally, it updates the feedbackLabel to indicate that the password has been generated and copied.
     */
    public void passwordGenerator() {
        try {
            SecureRandom randomizer = new SecureRandom();
            StringBuilder randomPassword = new StringBuilder();

            List<String> characterSets = new ArrayList<>();

            if (lowercaseBox.isSelected()) {
                characterSets.add(LOWERCASE_LETTERS);
            }
            if (uppercaseBox.isSelected()) {
                characterSets.add(UPPERCASE_LETTERS);
            }
            if (digitBox.isSelected()) {
                characterSets.add(DIGITS);
            }
            if (specialBox.isSelected()) {
                characterSets.add(SPECIAL_CHARACTERS);
            }

            int passwordLength = Integer.parseInt(lengthField.getText());

            int numSets = characterSets.size();

            for (int i = 0; i < passwordLength; i++) {
                String selectedCharacterSet = characterSets.get(randomizer.nextInt(numSets));
                char randomChar = selectedCharacterSet.charAt(randomizer.nextInt(selectedCharacterSet.length()));
                randomPassword.append(randomChar);
            }

            PasswordTools.toClipboard(String.valueOf(randomPassword));
            lengthField.setText(String.valueOf(randomPassword));
            feedbackLabel.setText("Password generated and copied");
            feedbackLabel.setAlignment(Pos.CENTER);
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter numbers only.");
            feedbackLabel.setAlignment(Pos.CENTER);
        } catch (IllegalArgumentException e) {
            feedbackLabel.setText("Please select at least one set.");
            feedbackLabel.setAlignment(Pos.CENTER);
        }
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
        Stage stage = (Stage) lengthField.getScene().getWindow(); //Getting stage object from the label
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
        Stage stage = (Stage) lengthField.getScene().getWindow(); //Getting stage object from the label
        stage.setX(Delta.x + me.getScreenX()); //Setting the x position of the stage
        stage.setY(Delta.y + me.getScreenY()); //Setting the y position of the stage
    }

    /**
     * This method is invoked when the "Exit" button is clicked. It closes the current application window.
     */
    @FXML
    void onExitButtonClick() {
        ((Stage) lengthField.getScene().getWindow()).close(); //Closing the form.
    }

    @FXML
    void slide() {
        lengthSlider.setMin(8);
        lengthSlider.setMax(50);
        int passwordLength = (int) lengthSlider.getValue();
        lengthField.setText(String.valueOf(passwordLength));
    }
}
