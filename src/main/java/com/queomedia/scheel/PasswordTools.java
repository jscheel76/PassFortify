package com.queomedia.scheel;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Helper class used by main controller class to generate passwords and evaluate password strength.
 */
public class PasswordTools {

    /**
     * Location of the master password file.
     */
    private static final String M_PASS_LOCATION = "MPass.txt";

    /**
     * Location of the password file.
     */
    private static final String PASSWORD_LOCATION = "Passwords.txt";

    /**
     * Location of the service file.
     */
    private static final String SERVICE_LOCATION = "Services.txt";

    /**
     * Location of the username file.
     */
    private static final String USERNAME_LOCATION = "Usernames.txt";

    /**
     * Location of the username file.
     */
    private static final String SETTINGS_LOCATION = "Settings.txt";

    /**
     * Minimum size of generated passwords.
     */
    private static final int MINIMUM_PASSWORD_SIZE = 12;

    /**
     * Generates a random password with varying length and character types.
     * This method utilizes a SecureRandom object to determine the length of the password within a specified range.
     * It then generates random characters based on different character types (special characters, numbers,
     * uppercase letters, and lowercase letters) using the generateRandomChar method. The generated characters are
     * concatenated to form the random password.
     *
     * @return A randomly generated password with varying length and character types.
     */
    public static String passwordGenerator() {
        SecureRandom randomizer = new SecureRandom();
        StringBuilder randomPassword = new StringBuilder();

        //Using SecureRandom to decide length of the password
        int passwordLength = randomizer.nextInt(MINIMUM_PASSWORD_SIZE, 24);

        for (int i = 0; i < passwordLength; i++) {
            char randomChar = generateRandomChar(randomizer);
            randomPassword.append(randomChar);
        }
        return randomPassword.toString();
    }

    /**
     * Generates a random character based on different character types.
     * This method takes a SecureRandom object as a parameter and generates a random character based on four character types:
     * special characters, numbers, uppercase letters, and lowercase letters. It uses ASCII values to determine the range
     * for each character type.
     *
     * @param randomizer The SecureRandom object used for generating random values.
     * @return A randomly generated character based on the specified character types.
     * @throws IllegalStateException If an unexpected character type is encountered during the switch statement.
     */
    private static char generateRandomChar(final SecureRandom randomizer) {
        int charType = randomizer.nextInt(4);

        return switch (charType) {
        case 0 -> (char) randomizer.nextInt(33, 48); // ASCII values for special characters
        case 1 -> (char) randomizer.nextInt(48, 58); // ASCII values for numbers
        case 2 -> (char) randomizer.nextInt(65, 91); // ASCII values for uppercase letters
        case 3 -> (char) randomizer.nextInt(97, 123); // ASCII values for lowercase letters
        default -> throw new IllegalStateException("Unexpected value: " + charType);
        };
    }

    /**
     * Evaluates the strength of a password based on various criteria, including length, character types, and complexity.
     *
     * @param passwordToCheck The password to be evaluated for strength.
     * @return An integer representing the password strength, where higher values indicate stronger passwords.
     */
    public static int passwordStrength(final String passwordToCheck) {

        final int lowLength = 8; //Minimum length for an okay password
        final int mediumLength = 10; //Minimum length for a secure password
        int passwordStrength = 0; //Integer used to add up points to check password strength

        //Checks whether the password is equal or longer than 8 characters
        if (passwordToCheck.length() <= lowLength) {
            passwordStrength -= 2;
        }

        //Checks whether the password is equal or longer than 10 characters
        if (passwordToCheck.length() >= mediumLength) {
            passwordStrength++;
        } else {
            passwordStrength--;
        }

        //Checks whether the password contains lowercase letters
        if (passwordToCheck.matches(".*[a-z].*")) {
            passwordStrength++;
        }

        //Checks whether the password contains uppercase letters
        if (passwordToCheck.matches(".*[A-Z].*")) {
            passwordStrength++;
        }

        //Checks whether the password contains numbers
        if (passwordToCheck.matches(".*[0-9].*")) {
            passwordStrength++;
        }

        //Checks whether password contains special characters
        if (passwordToCheck.matches(".*[§!$%&/?+*#~].*")) {
            passwordStrength += 2;
        }
        return passwordStrength;
    }

    /**
     * Evaluates the strength of a given password and returns a corresponding strength label.
     * This method calculates the strength of the provided password using a scoring system and assigns a strength label
     * based on predefined thresholds. The password strength labels include "weak," "medium," "strong," and "very strong."
     *
     * @param passwordToCheck The password to evaluate for strength.
     * @return The strength label indicating the assessed strength of the password.
     */
    public static String passwordStrengthOutput(final String passwordToCheck) {
        final int threeRules = 3; //Used to indicate password strength
        final int fiveRules = 5; //Used to indicate password strength
        int passwordStrength = passwordStrength(passwordToCheck);
        String savedPasswordStrength;
        if (passwordStrength < 2) {
            savedPasswordStrength = "weak";
        } else if (passwordStrength < threeRules) {
            savedPasswordStrength = "medium";
        } else if (passwordStrength <= fiveRules) {
            savedPasswordStrength = "strong";
        } else {
            savedPasswordStrength = "very strong";
        }
        return savedPasswordStrength;
    }

    /**
     * Checks for duplicate passwords within an array of password content lines.
     * This method iterates through the array of password content lines, comparing each password
     * with every other password. If a duplicate password is found (excluding self-comparisons), the
     * method returns true. Otherwise, it returns false.
     *
     * @param passwordContentLines An array of strings representing password content lines.
     * @return True if a duplicate password is found; false otherwise.
     */
    public static boolean duplicatePasswordCheck(final String[] passwordContentLines) {
        boolean hasSamePassword = false; //boolean used to save whether a password is found multiple times

        //Loop that goes through the passwords and compares them with each other
        for (int i = 0; i < passwordContentLines.length; i++) {
            for (int j = 0; j < passwordContentLines.length; j++) {
                if (passwordContentLines[i].equals(passwordContentLines[j]) && i != j) {
                    hasSamePassword = true; //match found, boolean set to true
                    break; // Exit the inner loop once a match is found
                }
            }
        }
        return hasSamePassword; //returning whether a password was found to be used multiple times
    }

    /**
     * Checks if the provided master password is correct by decrypting the stored master password.
     * This method verifies the correctness of the provided master password by attempting to decrypt
     * the stored master password from the designated location. The comparison result is returned as a
     * boolean, indicating whether the entered password matches the stored one.
     *
     * @param mPassword The master password to be checked for correctness.
     * @return True if the entered password matches the stored master password; false otherwise.
     *
     * @throws Exception If an error occurs during the decryption process.
     */
    public static boolean checkMasterpassword(final String mPassword) throws Exception {
        //Security function to check if the master password is correct. Useful as otherwise the application would double encrypt files
        byte[] decryptedText = Cryptography.decryptFile(M_PASS_LOCATION, mPassword);
        String decryptedMPass = new String(decryptedText, UTF_8);
        return mPassword.equals(decryptedMPass); //Returning whether the entered password equals the saved one
    }

    /**
     * Displays a file chooser dialog for selecting a folder to save a backup and returns the chosen path.
     * This method opens a file chooser dialog prompting the user to select a folder for saving a backup.
     * The initial directory is set to the user's home directory, and the suggested file name is "PassFortify Backup".
     * The user's chosen path is then returned as a Path object.
     *
     * @return The Path object representing the selected folder path for saving a backup.
     */
    public static Path getFilePath() {
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Select a directory where to store the backup"); //Title
        filechooser.setInitialDirectory(new File(System.getProperty("user.home"))); //Set initial directory to users home directory
        filechooser.setInitialFileName("PassFortify Backup"); //Set automatic name for the file to "Backup"
        filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Folders", "*."));

        return filechooser.showSaveDialog(null).toPath();
    }

    /**
     * Decrypts the content of a file using the provided master password, and then encrypts the
     * decrypted content with a new master password, overwriting the original file.
     * This method decrypts the specified file using the provided master password, retrieves the
     * decrypted content, and encrypts the content using a new master password. The resulting
     * encrypted content is then written back to the original file, overwriting its previous content.
     *
     * @param location    The location of the file to be decrypted and re-encrypted.
     * @param mPassword   The current master password used to decrypt the file.
     * @param newpass     The new master password used to encrypt the file.
     *
     * @throws Exception  If an error occurs during the decryption, encryption, or file writing process.
     *                    The specific exception type may vary based on the underlying operations.
     *
     * @see Cryptography#decryptFile(String, String)
     * @see #addDataWithoutAppend(String, String, String)
     */
    public static void decryptAndEncrypt(final String location, final String mPassword, final String newpass)
            throws Exception {
        byte[] decryptedPass = Cryptography.decryptFile(location, mPassword); //Decrypting the contents of the file
        String pText = new String(decryptedPass, UTF_8);
        addDataWithoutAppend(location, pText, newpass);
    }

    /**
     * Creates a backup of essential files in a specified destination directory.
     * This method creates a backup by copying critical files, including passwords, usernames,
     * services, settings, and master password information, from their original locations to the
     * specified destination directory. The method also waits for a short duration to ensure that
     * the backup creation message is displayed after the backup is actually created.
     *
     * @param destinationDirectory The path to the directory where the backup files will be stored.*
     * @throws IOException         If an I/O error occurs during file copying or directory creation.
     * @throws InterruptedException If the thread sleep is interrupted.
     */
    public static void createBackup(final Path destinationDirectory) throws IOException, InterruptedException {
        final int sleepASecond = 1000;
        Files.createDirectories(destinationDirectory);

        // Define destination paths for each file
        Path destinationPasswordPath = destinationDirectory.resolve("Passwords.txt");
        Path destinationUsernamePath = destinationDirectory.resolve("Usernames.txt");
        Path destinationServicePath = destinationDirectory.resolve("Services.txt");
        Path destinationSettingsPath = destinationDirectory.resolve("settings.txt");
        Path destinationMpassPath = destinationDirectory.resolve("MPass.txt");

        // Copy each file to the corresponding destination
        Files.copy(Path.of(PASSWORD_LOCATION), destinationPasswordPath);
        Files.copy(Path.of(USERNAME_LOCATION), destinationUsernamePath);
        Files.copy(Path.of(SERVICE_LOCATION), destinationServicePath);
        Files.copy(Path.of(SETTINGS_LOCATION), destinationSettingsPath);
        Files.copy(Path.of(M_PASS_LOCATION), destinationMpassPath);

        //Waiting one second, to display the message once the backup is actually created
        Thread.sleep(sleepASecond);
    }

    /**
     * Adds new account data to the specified location in a file after decrypting and encrypting its contents.
     * If the file exists, it reads the existing data, decrypts it, appends the new data, and then encrypts and saves
     * the modified content back to the file. If the file does not exist, it creates a new file and adds the new data.
     *
     * @param location     The location of the file where the account data will be added.
     * @param accountToAdd The account data to be added.
     * @param mPassword    The master password used for decryption and encryption.
     * @throws Exception If there is an issue with reading, decrypting, appending, encrypting, or writing the file data.
     */
    public static void addData(final String location, final String accountToAdd, final String mPassword)
            throws Exception {
        String existingData;
        final Path path = Path.of(location);
        if (Files.exists(path)) {
            // Read existing data from the file and decrypt
            byte[] encryptedData = Files.readAllBytes(path);
            byte[] decryptedData = Cryptography.decrypt(encryptedData, mPassword);
            existingData = new String(decryptedData, StandardCharsets.UTF_8);
        } else {
            existingData = "";
        }
        existingData += accountToAdd + System.lineSeparator();

        // Encrypt and save only the modified part of the data
        byte[] newData = Cryptography.encrypt(existingData.getBytes(StandardCharsets.UTF_8), mPassword);
        Files.write(path, newData);
    }

    /**
     * Adds content to a file without appending, encrypts the file with the provided master password,
     * and overwrites the existing content.
     * This method creates a FileWriter for the specified file, writes the given content to the file,
     * and then encrypts the entire file using the provided master password. The existing content in
     * the file is overwritten with the new content.
     *
     * @param location      The location of the file to which content is added and encrypted.
     * @param contentToAdd  The content to be added to the file.
     * @param mPassword     The master password used to encrypt the file.
     *
     * @throws Exception    If an error occurs during the file writing or encryption process.
     *                      The specific exception type may vary based on the underlying operations.
     */
    public static void addDataWithoutAppend(final String location, final String contentToAdd, final String mPassword)
            throws Exception {
        byte[] newData = Cryptography.encrypt(contentToAdd.getBytes(StandardCharsets.UTF_8), mPassword);
        Files.write(Paths.get(location), newData);
    }

    /**
     * Retrieves the content of a decrypted file and returns it as an array of strings, where each
     * element corresponds to a line in the file.
     * This method decrypts the specified file using the provided master password and converts the
     * decrypted data into a string. The string is then split into lines using the system-dependent
     * line separator, and the resulting array of lines is returned.
     *
     * @param location   The location of the file to be decrypted and read.
     * @param mPassword  The master password used to decrypt the file.
     * @return           An array of strings representing the lines of the decrypted file.
     *
     * @throws Exception If an error occurs during the decryption process or string conversion.
     *                   The specific exception type may vary based on the underlying operations.
     */
    public static String[] getContentLines(final String location, final String mPassword) throws Exception {
        //The files with the data are decrypted and added to a byte
        byte[] decrypted = Cryptography.decryptFile(location, mPassword);
        String content = new String(decrypted, UTF_8);
        return content.split(System.lineSeparator());
    }

    /**
     * Retrieves the password at the specified position in the content of the password file,
     * after decrypting the file using the provided master password.
     *
     * @param mPassword The master password used for decryption.
     * @param pos       The position of the password to retrieve in the file's content.
     * @return          The password corresponding to the specified position in the file's content.
     * @throws Exception If any error occurs during reading or decrypting the password file,
     *                   an exception is thrown to handle the issue.
     */
    public static String getPasswordFromFiles(final String mPassword, final int pos) throws Exception {
        String[] passwordContentLines = getContentLines(PASSWORD_LOCATION, mPassword);
        if (pos >= passwordContentLines.length) {
            return null;
        }
        return passwordContentLines[pos];
    }

    public static void changeEntry(final String mPassword, final String firstPath, final String secondPath,
            final String changePath, final String compareFirst, final String compareSecond, final String toChange)
            throws Exception {
        //Retrieving content of the saved accounts using PasswordTools class
        String[] firstContentLines = getContentLines(firstPath, mPassword);
        String[] secondContentLines = getContentLines(secondPath, mPassword);
        String[] changeContentLines = getContentLines(changePath, mPassword);

        int maxLines = Math.max(firstContentLines.length,
                Math.max(secondContentLines.length, changeContentLines.length));

        for (int i = 0; i < maxLines; i++) {
            //If service and username entry match in the same line of their respective files
            if (firstContentLines[i].equals(compareFirst) && secondContentLines[i].equals(compareSecond)) {
                //Replacing the desired password in the same line that service and username were found in
                changeContentLines[i] = toChange;
                break;
            }
        }
        //Initialising a FileWriter to rewrite the password file
        try (FileWriter contentChanger = new FileWriter(changePath)) {
            for (String changeContentLine : changeContentLines) {
                contentChanger.write(changeContentLine);
                contentChanger.write(System.lineSeparator()); //Using lineSeparator to achieve correct formatting of file
            }
        }
        Cryptography.encryptFile(changePath, changePath, mPassword); //Encrypting the password file
    }

    /**
     * Deletes the data at the specified position in the content of the files located at the given location,
     * and then saves the updated content after encrypting it using the provided master password.
     *
     * @param pos The position of the data to be deleted in the file's content. Coming from the position of the tableview entry.
     * @param location The location of the file.
     * @param mPassword The master password used for encryption.
     * @throws Exception If any error occurs during reading, encrypting, or writing the file date,
     *                   an exception is thrown to handle the issue.
     */
    public static void deleteLineInAccountFiles(int pos, String location, String mPassword) throws Exception {
        String[] contentLines = getContentLines(location, mPassword);
        StringBuilder newContent = new StringBuilder();
        for (int i = 0; i < contentLines.length; i++) {
            if (i != pos) {
                newContent.append(contentLines[i]).append(System.lineSeparator());
            }
        }
        addDataWithoutAppend(location, newContent.toString(), mPassword);
    }

    /**
     * Support Method used to transfer a string into the user's clipboard. Used primarily when something is generated-
     *
     * @param clipboardContent String which will be placed in clipboard. In this case either a generated password or passphrase
     */
    public static void toClipboard(final String clipboardContent) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(clipboardContent);
        clipboard.setContent(content);
    }
}
