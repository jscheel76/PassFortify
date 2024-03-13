package com.queomedia.scheel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

/**
 * The PassphraseGenerator class provides a method for generating random passphrases
 * by selecting words from a wordlist.
 *
 * <p>This class utilizes a wordlist based on the diceware wordlist to construct passphrases of a specified length.
 * The wordlist should contain one word per line. The number of words to be included in
 * the passphrase and the total number of lines in the wordlist file are configurable.
 * The generated passphrase consists of these randomly chosen words, separated by spaces.</p>
 *
 * <p>The method {@code generatePassphrase()} throws an IOException if there is an
 * issue reading the wordlist file. This could occur if the file is not found or if there
 * is a problem reading its contents.</p>
 *
 * @author Jannik Scheel
 */
public class PassphraseGenerator {

    /**
     * Generates a passphrase by randomly selecting words from a wordlist.
     *
     * @return A randomly generated passphrase consisting of words separated by spaces.
     * @throws IOException If an I/O error occurs while reading the wordlist file.
     *                     This could happen if the file is not found or if there is an issue reading its contents.
     */
    public static String generatePassphrase() throws IOException {
        final int passphraseLength = 6; //How many words should be generated for the passphrase
        final int lengthOfWordlist = 7776; //Amount of lines in the wordlist
        StringBuilder passphrase = new StringBuilder();
        int indexToPick; //Integer used to pick new index
        SecureRandom indexPicker = new SecureRandom(); //Using secure random to randomly choose words from the wordlist
        for (int i = passphraseLength; i > 0; i--) {
            indexToPick = indexPicker.nextInt(lengthOfWordlist); //Pick a random index
            passphrase.append(Files.readAllLines(Paths.get("wordlist.txt")).get(indexToPick)); //Add it to the passphrase
            if (i > 1) {
                passphrase.append(" "); //As long as the integer 'i' is still bigger than 1, add a space to make the passphrase more readable
            }
        }
        return passphrase.toString(); //Return the passphrase as a string
    }
}
