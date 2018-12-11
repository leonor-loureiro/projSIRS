import crypto.Crypto;
import crypto.KeystoreManager;
import crypto.exception.CryptoException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

public class StoreSecretKeyTest {

    String keystoreFileName = "./testKeystore.jceks";
    char[] passwordArray;
    String alias = "alias";
    SecretKey secretKey;
    String password = "password";

    @Before
    public void setUp() throws CryptoException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {

        passwordArray = password.toCharArray();
        secretKey = Crypto.generateSecretKey();

        //Create a keystore
        KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(null,passwordArray);

        //Stores the entry in the keystore
        try (FileOutputStream fos = new FileOutputStream( keystoreFileName)){
            ks.store(fos, passwordArray);
        }
    }

    @Test
    public void storeSecretKey() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {

        KeystoreManager.StoreSecretKey(secretKey, keystoreFileName, alias, passwordArray);
        SecretKey extractedKey = KeystoreManager.getSecretKey(keystoreFileName, alias, passwordArray);

        Assert.assertTrue(extractedKey.equals(secretKey));
    }


    @After
    public void cleanUp() throws IOException {
        Path fileToDeletePath = Paths.get(keystoreFileName);
        Files.delete(fileToDeletePath);
    }

}
