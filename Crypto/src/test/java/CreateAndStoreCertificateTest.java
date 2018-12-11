import crypto.Crypto;
import crypto.KeystoreManager;
import crypto.exception.CryptoException;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

public class CreateAndStoreCertificateTest {

    KeyPair keyPair;
    String password = "password";
    char[] passwordArray;
    String keystoreFileName = "./testKeystore.jceks";
    String alias = "alias";

    @Before
    public void setUp() throws CryptoException {
        passwordArray = password.toCharArray();
        keyPair = Crypto.generateRSAKeys();
    }


    @Test
    public void storeAsymmetricKeys()
            throws OperatorCreationException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {

        KeystoreManager.CreateAndStoreCertificate(keyPair,keystoreFileName, alias, passwordArray);
        PublicKey pubKey = KeystoreManager.getPublicKey(keystoreFileName, alias + "-certificate", passwordArray);
        PrivateKey privateKey = (PrivateKey) KeystoreManager.getPrivateKey(keystoreFileName, alias + "-privateKey", passwordArray);

        Assert.assertTrue(pubKey.equals(keyPair.getPublic()));
        Assert.assertTrue(privateKey.equals(keyPair.getPrivate()));

    }

    @After
    public void cleanUp() throws IOException {
        Path fileToDeletePath = Paths.get(keystoreFileName);
        Files.delete(fileToDeletePath);
    }
}
