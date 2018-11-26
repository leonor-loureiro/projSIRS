package authserver.security;

import authserver.exception.CryptoException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;

public class Crypto {

    // Salt generator
    private static final DigestRandomGenerator generator = new DigestRandomGenerator(new SHA3Digest(512));

    /**
     * Generates salt
     *
     * @param size # bytes of the salt
     * @return salt value
     */
    private static byte[] salt(int size) {
        byte[] salt = new byte[size];
        // Fills bytes with random values
        generator.nextBytes(salt);
        return salt;
    }


    private static String toString(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    /**
     * Converts a byte array to a string
     *
     * @param input byte array
     * @return converted string
     */
    private static byte[] toByteArray(String input) {
        return Base64.getDecoder().decode(input);
    }

    /**
     * Computes the salted password with random salt
     *
     * @param plainPassword
     * @return salt|salted password
     */
    public static String hash(String plainPassword) throws CryptoException {
        return hash(plainPassword, salt(128), 512, 101501);
    }

    /**
     * Computes the salted password with given salt
     *
     * @param plainPassword
     * @param salt
     * @return salt|salted password
     */
    public static String hash(String plainPassword, byte[] salt) throws CryptoException {
        return hash(plainPassword, salt, 512, 101501);
    }

    /**
     * Computes the salted hash of the password using PBKDF2
     *
     * @param password
     * @param salt
     * @param keyLength
     * @param iterations
     * @return salt|salted password
     */
    public static String hash(String password, byte[] salt, int keyLength, int iterations) throws CryptoException {
        if (password == null || password.isEmpty())
            throw new CryptoException("Password cannot be null nor empty");

        if (keyLength <= 0)
            throw new CryptoException("Key Length must be a positive integer");

        if (iterations < 0)
            throw new CryptoException("Iterations must be a positive integer");


        // PKCS 5 - Password-based Encryption Standard
        // PBE - Password-based Encryption

        //Create the generator for the PBE derived keys and ivs as defined by PKCS 5 V2.0 Scheme 2
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator();

        // Convert the password to a byte array
        byte[] passwordBytes = PBEParametersGenerator.PKCS5PasswordToBytes(password.toCharArray());

        // Initialize the parameters generator
        generator.init(passwordBytes, salt, iterations);

        // Generate the key
        byte[] key = ((KeyParameter) generator.generateDerivedParameters(keyLength)).getKey();

        return toString(salt) + "|" + toString(key);
    }

    /**
     * Verifies if hash corresponds to the salted password
     *
     * @param password
     * @param hash
     * @return true if its valid; false otherwise
     * @throws CryptoException
     */
    public static boolean validateHash(String password, String hash) throws CryptoException {
        if (password == null || password.isEmpty())
            throw new CryptoException("Password cannot be null nor empty");

        if (hash == null || hash.isEmpty())
            throw new CryptoException("Hash cannot be null nor empty");

        // Extract salt
        byte[] salt = toByteArray(getSalt(hash));

        //Compute hash and compare
        return hash(password, salt).equals(hash);
    }

    /**
     * Extracts the salt from a string with the following format: salt|*
     *
     * @param input
     * @return salt value
     */
    public static String getSalt(String input) {
        return input.substring(0, input.indexOf("|"));
    }

    /***
     * Extracts private key from keystore
     * @param keystoreFile  file where the keystore is
     * @param keystorePwd password for the keystore
     * @param alias alias for the requested key
     * @param keyPwd password for the requested key
     * @return private key
     * @throws CryptoException
     */
    public static Key getPrivateKey(String keystoreFile, String keystorePwd, String alias, String keyPwd) throws CryptoException {

        // Get keystore
        KeyStore keystore = loadKeystore(keystoreFile, keystorePwd);

        // Get the key
        try {
            return keystore.getKey(alias, keyPwd.toCharArray());

        } catch (Exception e) {
            throw new CryptoException("Failed to extract key from keystore.");
        }
    }

    /**
     * Loads the keystore from a file
     * @param keystoreFile file where the keystore is
     * @param keystorePwd keystore password
     * @return the keystore
     * @throws CryptoException
     */
    private static KeyStore loadKeystore(String keystoreFile, String keystorePwd) throws CryptoException {
        try {
            FileInputStream is = new FileInputStream(keystoreFile);

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, keystorePwd.toCharArray());

            return keystore;

        } catch (IOException e) {
            throw new CryptoException("File is invalid");
        } catch (Exception e) {
            throw new CryptoException("Failed to load the keystore.");
        }
    }

    /**
     * Extracts the public key from the keystore
     * @param keystoreFile file where the keystore is
     * @param keystorePwd keystore password
     * @param alias
     * @return
     * @throws CryptoException
     */
    public static PublicKey getPublicKey(String keystoreFile, String keystorePwd, String alias) throws CryptoException {
        // Load keystore
        KeyStore keystore = loadKeystore(keystoreFile, keystorePwd);
        try {
            // Get public key certificate
            Certificate cert = keystore.getCertificate(alias);
            // Extract public key
            return  cert.getPublicKey();
        } catch (KeyStoreException e) {
            throw new CryptoException("Failed to retrieve the key");
        }
    }
}