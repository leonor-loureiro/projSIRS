import crypto.Crypto;
import crypto.exception.CryptoException;
import org.bouncycastle.util.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class SymmetricEncryptionTest {

    byte[] secretKey;
    byte[] data;

    @Before
    public void setUp() throws CryptoException {
        //Initialize security settings
        Crypto.init();
        //generate secret key
        secretKey = Crypto.generateSecretKey().getEncoded();
        //generate random data bytes
        data = new byte[50];
        new Random().nextBytes(data);

    }
    @Test
    public void success() throws CryptoException {

        byte[] cipher = Crypto.encryptAES(secretKey, data);
        byte[] decipher =  Crypto.decryptAES(secretKey, cipher);

        Assert.assertArrayEquals(decipher, data);
    }
    @Test (expected = CryptoException.class)
    public void diffKey() throws CryptoException {

        byte[] cipher = Crypto.encryptAES(secretKey, data);
        byte[] diffKey = Crypto.generateSecretKey().getEncoded();
        byte[] decipher = Crypto.decryptAES(diffKey, cipher);

        Assert.assertFalse(Arrays.areEqual(decipher, data));
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyEncryption() throws CryptoException {
        byte[] cipher = Crypto.encryptAES(null, data);
    }

    @Test (expected = CryptoException.class)
    public void emptyDataEncryption() throws CryptoException {
        byte[] cipher = Crypto.encryptAES(secretKey, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyDecipher() throws CryptoException {
        byte[] cipher = Crypto.encryptAES(secretKey, data);
        byte[] decipher = Crypto.encryptAES(null, data);
    }

    @Test (expected = NullPointerException.class)
    public void emptyDataDecipher() throws CryptoException {
        byte[] cipher = Crypto.encryptAES(secretKey, data);
        byte[] decipher = Crypto.decryptAES(secretKey, null);
    }


    @After
    public void tearDown() {
        data = null;
        secretKey = null;
    }
}
