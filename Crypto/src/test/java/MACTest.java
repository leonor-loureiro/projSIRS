import crypto.Crypto;
import crypto.exception.CryptoException;
import org.bouncycastle.util.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Random;

public class MACTest {

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

        String mac = Crypto.computeMAC(secretKey, data);
        Assert.assertTrue(Crypto.validateMAC(secretKey, data, mac));
    }

    @Test
    public void failure() throws CryptoException {
        String mac = Crypto.computeMAC(secretKey, data);
        byte[] corruptedData = new byte[50];
        new Random().nextBytes(corruptedData);
        Assert.assertFalse(Crypto.validateMAC(secretKey, corruptedData, mac));

    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKey() throws CryptoException {
        String mac = Crypto.computeMAC(null, data);
    }

    @Test (expected = CryptoException.class)
    public void emptyData() throws CryptoException {
        String mac = Crypto.computeMAC(secretKey, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyValidation() throws CryptoException {
        String mac = Crypto.computeMAC(null, secretKey);
        Crypto.validateMAC(null, data, mac);
    }

    @Test
    public void emptyDataValidation() throws CryptoException {
        String mac = Crypto.computeMAC(secretKey, secretKey);
        Assert.assertFalse(Crypto.validateMAC(secretKey, null, mac));

    }

    @Test
    public void emptyMacValidation() throws CryptoException {
        String mac = Crypto.computeMAC(secretKey, secretKey);
        Assert.assertFalse(Crypto.validateMAC(secretKey, data, null));

    }



    @After
    public void tearDown() {
        data = null;
        secretKey = null;
    }
}
