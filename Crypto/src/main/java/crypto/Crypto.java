package crypto;


import crypto.exception.CryptoException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class Crypto {

    /**
     * Sets the Security policy as unlimited
     * Adds the security provider BouncyCastle
     */
    public static void init(){
        // Set java security policy
        Security.setProperty("crypto.policy", "unlimited");

        // Add security provider
        if(Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());
    }
    /***********************************************
     *         Password Crypto Functions
     ***********************************************/
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


    public static String toString(byte[] input) {
        //return Base64.getEncoder().encodeToString(input);
        return new String(Base64.encode(input));
    }

    /**
     * Converts a byte array to a string
     *
     * @param input byte array
     * @return converted string
     */
    public static byte[] toByteArray(String input) {
        //return Base64.getDecoder().decode(input);
        return Base64.decode(input);
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

    /**************************************************************
     *              Asymmetric Crypto Functions
     **************************************************************/


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


    /*************************************************************************
     *                  DIGITAL SIGNATURE FUNCTION
     *************************************************************************/

    /**
     * Signs data with a private key
     * @param data data to sign
     * @param signingKey private key used to sign
     * @return signature
     * @throws CryptoException
     */
    public static String sign(byte[] data, PrivateKey signingKey) throws CryptoException {

        if(data == null)
            throw  new CryptoException("Data is null");

        try {
            // Create RSA signature instance
            Signature signAlg = Signature.getInstance("SHA256withRSA", "BC");
            // Initialize the signature with the private key
            signAlg.initSign(signingKey);
            // Load the data
            signAlg.update(data);
            // Sign data
            return toString(signAlg.sign());


        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new CryptoException("Invalid Key");
        } catch (Exception e){
            e.printStackTrace();
            throw new CryptoException("Signing failed");
        }
    }

    /**
     * Verifying the signature
     * @param signature
     * @param data data signed
     * @param key public key
     * @return true if valid, false otherwise
     * @throws CryptoException
     */
    public static boolean verifySignature(String signature, byte[] data, PublicKey key) throws CryptoException {
        if(data == null)
            throw  new CryptoException("Data is null");
        if(signature == null)
            throw  new CryptoException("Signature is null");
        try {
            Signature signAlg = Signature.getInstance("SHA256withRSA", "BC");
            signAlg.initVerify(key);
            signAlg.update(data);
            return  signAlg.verify(toByteArray(signature));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new CryptoException("Invalid Key");
        } catch (Exception e){
            e.printStackTrace();
            throw new CryptoException("Verification failed");
        }

    }

    /*****************************************************************************
     *                      RSA Encryption and Decryption
     ****************************************************************************/

    public static KeyPair generateRSAKeys() throws CryptoException {
        return generateRSAKeys(2048);
    }
    public static KeyPair generateRSAKeys(int blockSize) throws CryptoException {
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Failed to generate RSA key pair");
        }
        kpg.initialize(blockSize);
        KeyPair kp = kpg.generateKeyPair();
        return  kp;
    }

    /**
     * Converts the public key bytes to a PublicKey instance
     * @param encoded
     * @return PublicKey instance
     * @throws CryptoException
     */
    public static PublicKey recoverPublicKey(byte[] encoded) throws CryptoException {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
        } catch (InvalidKeySpecException e) {
            throw new CryptoException("Invalid key");
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Failed to recover public key");
        }
    }

    public static byte[] encryptRSA (byte[] data, Key key) throws CryptoException {
        if(data == null)
            throw  new CryptoException("Data is null");

        try {
            Cipher cipher = Cipher.getInstance("RSA", "BC");
            cipher.init(ENCRYPT_MODE, key);
            return (cipher.doFinal(data));

        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new CryptoException("Invalid Key");
        } catch (Exception e){
            e.printStackTrace();
            throw new CryptoException("Verification failed");
        }
    }

    public static byte[] decryptRSA (byte[] data, Key key) throws CryptoException {
        if(data == null)
            throw  new CryptoException("Data is null");

        try {
            Cipher cipher = Cipher.getInstance("RSA", "BC");
            cipher.init(DECRYPT_MODE, key);
            return (cipher.doFinal((data)));

        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new CryptoException("Invalid Key");
        } catch (Exception e){
            e.printStackTrace();
            throw new CryptoException("Verification failed");
        }
    }

    /************************************************************************
     *                  SYMMETRIC ENCRYPTION / DECRYPTION
     ***********************************************************************/

    public static SecretKey generateSecretKey() throws CryptoException {
        return generateSecretKey(256, "AES");
    }

        /**
         * Generates a secret key
         * @param size key size
         * @param alg algorithm
         * @return secret key
         * @throws CryptoException
         */
    public static SecretKey generateSecretKey(int size, String alg) throws CryptoException {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            throw  new CryptoException("Failed to generate secret key");
        }
        keyGen.init(size);
        return keyGen.generateKey();
    }


    public static byte[] encryptAES(byte[] key, byte[] data) throws CryptoException {
        //Create secret key
        SecretKeySpec sKey = new SecretKeySpec(key,"AES");

        // Create IV param
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParam = new IvParameterSpec(iv);

        try {
            //Create cipher instance
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
            cipher.init(ENCRYPT_MODE, sKey, ivParam);

            // Cipher data
            byte[] cipheredData = cipher.doFinal(data);

            // Concat the IV and the ciphered data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write( cipher.getIV());
            outputStream.write( cipheredData );
            return outputStream.toByteArray();


        } catch (InvalidKeyException e) {
            throw new CryptoException("Invalid key");
        } catch (Exception e) {
            e.printStackTrace();
            throw new CryptoException("Encryption failed");
        }
    }


    public static byte[] decryptAES(byte[] key, byte[] cipherIVandData) throws CryptoException {
        //Extract IV
        byte iv[] = new byte[16];
        System.arraycopy(cipherIVandData, 0, iv, 0, iv.length);
        IvParameterSpec ivParam = new IvParameterSpec(iv);

        //Extract encrypted data
        byte[] cipheredData = new byte[cipherIVandData.length - iv.length];
        System.arraycopy(cipherIVandData, iv.length, cipheredData, 0, cipheredData.length);

        //Extract secret key
        SecretKeySpec sKey = new SecretKeySpec(key, "AES");

        try{
            //Create cipher instance
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");

            cipher.init(DECRYPT_MODE, sKey, ivParam);

            // Decipher data
            return cipher.doFinal(cipheredData);

        } catch (InvalidKeyException e) {
            throw new CryptoException("Invalid Key");
        } catch (Exception e){
            throw new CryptoException("Decryption failed");
        }

    }

    /******************************************************************************************
     *                          MESSAGE AUTHENTICATION CODE (MAC)
     *****************************************************************************************/

    public static String computeMAC(byte[] key, byte[] message) throws CryptoException {
        return computeMAC(key, message, "HmacSHA256");
    }

    public static boolean validateMAC(byte[] key, byte[] message, String mac) throws CryptoException {
        return validateMAC(key, message, mac, "HmacSHA256");

    }
        /**
         * Computes the MAC of a message
         * @param key secret key
         * @param message message
         * @param alg MAC algorithm
         * @return MAC
         * @throws CryptoException
         */
    public static String computeMAC(byte[] key, byte[] message, String alg) throws CryptoException {

        if(message == null)
            throw  new CryptoException("Message cannot be null");
        try {
            SecretKeySpec keyParam = new SecretKeySpec(key, alg);
            Mac mac = Mac.getInstance(alg, "BC");
            mac.init(keyParam);
            return toString(mac.doFinal(message));

        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("No such algorithm");
        } catch (InvalidKeyException e) {
            throw  new CryptoException("Invalid Key");
        } catch (NoSuchProviderException e) {
            throw  new CryptoException("Failed to generate MAC");
        }


    }

    /**
     * Checks if MAC matches message
     * @param key secret key
     * @param alg MAC algorithm
     * @param message message
     * @param mac MAC
     * @return true if matches; false otherwise
     */
    public static boolean validateMAC(byte[] key, byte[] message, String mac, String alg){



        String computedMac = null;
        try {
            computedMac = computeMAC(key, message, alg);
        } catch (CryptoException e) {
            return false;
        }
        return computedMac.equals(mac);
    }
}