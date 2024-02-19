package com.queomedia.scheel;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {

    /**
     * Specifying the algorithm used for encryption.
     */
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";

    /**
     * Length of the tag used.
     */
    private static final int TAG_LENGTH_BIT = 128;

    /**
     * Length of the IV used.
     */
    static final int IV_LENGTH_BYTE = 64;

    /**
     * Length of the salt used.
     */
    static final int SALT_LENGTH_BYTE = 64;

    /**
     * Number of iterations done while generating the password derived secret key.
     */
    static final int ITERATION_NUMBER = 128000;

    /**
     * Length of the secret key.
     */
    static final int KEY_LENGTH = 256;

    /**
     * Generates a random nonce (number used once) of the specified length using a cryptographically secure
     * pseudo-random number generator.
     *
     * @param length The desired length of the nonce in bytes.
     * @return A byte array containing the randomly generated nonce.
     */
    public static byte[] getRandomNonce(final int length) {
        //Create a byte array to store the random nonce
        byte[] nonce = new byte[length];

        //Use SecureRandom to generate random bytes for the nonce
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    /**
     * Derives an AES encryption key from the provided master password and salt using the PBKDF2 key derivation function
     * with HMAC-SHA256 as the pseudo-random function.
     *
     * @param masterPassword The master password from which the key is derived.
     * @param salt           The salt used in the key derivation process.
     * @return               A SecretKey object representing the derived AES encryption key.
     * @throws NoSuchAlgorithmException If the specified cryptographic algorithm is not available.
     * @throws InvalidKeySpecException  If the provided key specification is invalid or unsupported.
     */
    public static SecretKey getAESKeyFromPassword(final String masterPassword, final byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Create a SecretKeyFactory instance using PBKDF2 with HMAC SHA-256
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        // Define the key specification with the master password, salt, iteration count, and key length
        KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt, ITERATION_NUMBER, KEY_LENGTH);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    /**
     * Encrypts the given plaintext using the Advanced Encryption Standard (AES) algorithm
     * in Galois/Counter Mode (GCM) mode with a derived key from the provided master password.
     *
     * @param pText          The plaintext to be encrypted.
     * @param masterPassword The master password used to derive the encryption key.
     * @return               A byte array containing the encrypted data, including the initialization vector (IV)
     *                       and the salt used in key derivation.
     * @throws Exception     If any cryptographic operation fails, or if an invalid key specification is provided.
     *                       Possible exceptions include NoSuchAlgorithmException, NoSuchPaddingException,
     *                       InvalidKeyException, InvalidAlgorithmParameterException, and IllegalBlockSizeException.
     */
    public static byte[] encrypt(final byte[] pText, final String masterPassword) throws Exception {

        //Generate a random nonce as salt
        byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);

        //Generate a random nonce as IV (Initialization Vector)
        byte[] iv = getRandomNonce(IV_LENGTH_BYTE);

        //Derive the AES key from the master password and salt
        SecretKey aesKeyFromPassword = getAESKeyFromPassword(masterPassword, salt);

        //Create a cipher object for AES-GCM encryption
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

        //Initialize the cipher in encryption mode with the derived key and IV
        cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        //Perform the encryption and obtain the cipher text
        byte[] cipherText = cipher.doFinal(pText); //create cipher text out of plain text

        //Concatenate IV, salt, and cipher text into a single byte array
        return ByteBuffer.allocate(iv.length + salt.length + cipherText.length).put(iv).put(salt).put(cipherText)
                .array();

    }

    /**
     * Decrypts the given ciphertext using the Advanced Encryption Standard (AES) algorithm
     * in Galois/Counter Mode (GCM) mode with a derived key from the provided master password.
     *
     * @param cText          The ciphertext to be decrypted, including the initialization vector (IV)
     *                       and the salt used in key derivation.
     * @param masterPassword The master password used to derive the decryption key.
     * @return               A byte array containing the decrypted plaintext.
     * @throws Exception     If any cryptographic operation fails, or if an invalid key specification is provided.
     *                       Possible exceptions include NoSuchAlgorithmException, NoSuchPaddingException,
     *                       InvalidKeyException, InvalidAlgorithmParameterException,
     *                       IllegalBlockSizeException, BadPaddingException, and AEADBadTagException
     *                       (thrown specifically for authentication failure).
     *                       If the master password is incorrect, a warning message is printed,
     *                       and the original ciphertext is returned.
     */
    public static byte[] decrypt(final byte[] cText, final String masterPassword) throws Exception {
        try {
            //Wrap the cipher text in a ByteBuffer for easy extraction
            ByteBuffer bb = ByteBuffer.wrap(cText);

            //Extract the Initialization Vector (IV) from the cipher text
            byte[] iv = new byte[IV_LENGTH_BYTE];
            bb.get(iv);

            //Extract the salt from the cipher text
            byte[] salt = new byte[SALT_LENGTH_BYTE];
            bb.get(salt);

            //Extract the actual cipher text from the remaining bytes
            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);

            //Derive the AES decryption key from the master password and salt
            SecretKey aesKeyFromPassword = getAESKeyFromPassword(masterPassword, salt);

            //Create a cipher object for AES-GCM decryption
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            //Perform the decryption and obtain the plaintext
            return cipher.doFinal(cipherText);
        } catch (javax.crypto.AEADBadTagException e) {
            // Handle the case where decryption fails due to an incorrect master password
            System.out.println("Decryption failed. Make sure your master password is correct.");
        }
        //Return the original cipher text if decryption fails
        return cText;
    }

    /**
     * Encrypts the content of a file using the provided master password and writes the encrypted data to another file.
     *
     * @param fromFile        The path to the file containing the plaintext data to be encrypted.
     * @param toFile          The path to the file where the encrypted data will be written.
     * @param masterPassword  The master password used to derive the encryption key.
     * @throws Exception      If any I/O error occurs while reading or writing the files,
     *                        or if there is an issue with the encryption process (e.g., cryptographic operations fail).
     */
    public static void encryptFile(final String fromFile, final String toFile, final String masterPassword)
            throws Exception {
        //Read the content of the input file into a byte array
        byte[] fileContent = Files.readAllBytes(Paths.get(fromFile));

        //Encrypt the file content using the provided master password
        byte[] encryptedText = encrypt(fileContent, masterPassword);

        //Specify the path for the output file
        Path path = Paths.get(toFile);

        //Write the encrypted data to the specified output file
        Files.write(path, encryptedText);
    }

    /**
     * Decrypts the content of an encrypted file using the provided master password.
     *
     * @param fromEncryptedFile The path to the file containing the encrypted data to be decrypted.
     * @param masterPassword    The master password used to derive the decryption key.
     * @return                  A byte array containing the decrypted plaintext data.
     * @throws Exception        If any I/O error occurs while reading the encrypted file,
     *                          or if there is an issue with the decryption process (e.g., cryptographic operations fail).
     */
    public static byte[] decryptFile(final String fromEncryptedFile, final String masterPassword) throws Exception {
        //Read the content of the encrypted file into a byte array
        byte[] fileContent = Files.readAllBytes(Paths.get(fromEncryptedFile));

        //Decrypt the file content using the provided master password
        return decrypt(fileContent, masterPassword);
    }
}
