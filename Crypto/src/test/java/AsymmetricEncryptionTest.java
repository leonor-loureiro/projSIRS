import crypto.Crypto;
import crypto.exception.CryptoException;
import org.bouncycastle.util.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Random;

public class AsymmetricEncryptionTest {

    KeyPair keyPair;
    byte[] data;

    @Before
    public void setUp() throws CryptoException {
        //Initialize security settings
        Crypto.init();
        //generate secret key
        keyPair = Crypto.generateRSAKeys();
        //generate random data bytes
        data = new byte[50];
        new Random().nextBytes(data);

    }
    @Test
    public void success() throws CryptoException {

        byte[] cipher = Crypto.encryptRSA(data, keyPair.getPublic());
        byte[] decipher =  Crypto.decryptRSA(cipher, keyPair.getPrivate());

        Assert.assertArrayEquals(decipher, data);
    }
    @Test
    public void diffKey() throws CryptoException {

        byte[] cipher = Crypto.encryptRSA(data, keyPair.getPublic());
        KeyPair diffKeyPair = Crypto.generateRSAKeys();
        byte[] decipher =  Crypto.decryptRSA(cipher, diffKeyPair.getPrivate());
        Assert.assertFalse(Arrays.areEqual(decipher, data));
    }

    @Test (expected = CryptoException.class)
    public void emptyKeyEncryption() throws CryptoException {
        byte[] cipher = Crypto.encryptRSA(data, null);
    }

    @Test (expected = CryptoException.class)
    public void emptyDataEncryption() throws CryptoException {
        byte[] cipher = Crypto.encryptRSA(null, keyPair.getPublic());
    }

    @Test (expected = CryptoException.class)
    public void emptyKeyDecipher() throws CryptoException {
        byte[] cipher = Crypto.encryptRSA(data, keyPair.getPublic());
        byte[] decipher =  Crypto.decryptRSA(cipher, null);
    }

    @Test (expected = CryptoException.class)
    public void emptyDataDecipher() throws CryptoException {
        byte[] cipher = Crypto.encryptRSA(data, keyPair.getPublic());
        byte[] decipher =  Crypto.decryptRSA(null, keyPair.getPrivate());
    }


    @After
    public void tearDown() {
        data = null;
        keyPair = null;
    }

}
