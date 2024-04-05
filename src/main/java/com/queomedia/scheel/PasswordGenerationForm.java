package com.queomedia.scheel;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordGenerationForm {

    @FXML
    private CheckBox lowercaseBox;

    @FXML
    private CheckBox uppercaseBox;

    @FXML
    private CheckBox digitBox;

    @FXML
    private CheckBox specialBox;

    @FXML
    private TextField lengthField;

    @FXML
    private Label feedbackLabel;

    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[{]};:'\"|,./?";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    public void passwordGenerator() {
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
     * This method is called when the "Exit" button is clicked. It closes the current application window.
     */
    @FXML
    void onExitButtonClick() {
        ((Stage) lengthField.getScene().getWindow()).close(); //Closing the form.
    }
}
